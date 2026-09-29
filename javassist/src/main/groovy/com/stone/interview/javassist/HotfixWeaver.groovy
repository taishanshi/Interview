package com.stone.interview.javassist

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtBehavior
import javassist.CtClass
import javassist.CtConstructor
import javassist.CtField
import javassist.Modifier
import javassist.NotFoundException
import javassist.bytecode.AccessFlag
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * 对应课程 ModifyTransform.addCode：真正改字节码的地方。
 *
 * 课程按参数个数 insertBefore("System.out.println($1)")，只为证明 Javassist 能改方法。
 * 热修复要的是：每个方法开头加 bool 判断，命中则反射走补丁，否则执行原方法体（美团 Robust）。
 *
 * 为什么改完打成一个 jar：AGP 9 的 CLASSES toTransform 出口是 RegularFile，不是目录。
 * 为什么 jar 原样拷贝、只改 directory：和课程一致，依赖/R 类没有补丁入口。
 */
class HotfixWeaver {

    static final String REDIRECT_TYPE = "com.stone.interview.hotfix.ChangeQuickRedirect"
    static final String PROXY_TYPE = "com.stone.interview.hotfix.PatchProxy"
    static final String FIELD_NAME = "changeQuickRedirect"

    private final ClassPool pool
    private int classCount
    private int methodCount
    private int skipMethodCount
    private final List<String> reportLines = []

    HotfixWeaver(ClassPool pool) {
        this.pool = pool
    }

    static HotfixWeaver create(File androidJar, Collection<File> classpath, File tmpDir) {
        tmpDir.mkdirs()
        // true：带上 JDK 类。再追加 android.jar + 本 module class，insertBefore 才能编译通过
        ClassPool pool = new ClassPool(true)
        if (androidJar != null && androidJar.isFile()) {
            pool.appendClassPath(androidJar.absolutePath)
            println "[hotfix] android.jar = ${androidJar.absolutePath}"
        }
        classpath.each { file ->
            appendClasspath(pool, file, tmpDir)
        }
        return new HotfixWeaver(pool)
    }

    /** jar 不改（课程同样 copyFile）；directory 里的 .class 插桩后再写入 AGP 指定的输出 jar */
    void writeToJar(Collection<File> jars, Collection<File> dirs, File outFile) {
        def seen = new HashSet<String>()
        new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile))).withCloseable { zos ->
            jars.each { copyJar(it, zos, seen) }
            dirs.each { processDir(it, zos, seen) }
        }
    }

    private static void copyJar(File jar, ZipOutputStream zos, Set<String> seen) {
        def zip = new ZipFile(jar)
        try {
            zip.entries().each { entry ->
                if (!seen.add(entry.name)) {
                    return
                }
                zos.putNextEntry(new ZipEntry(entry.name))
                if (!entry.directory) {
                    zip.getInputStream(entry).withStream { zos << it }
                }
                zos.closeEntry()
            }
        } finally {
            zip.close()
        }
    }

    private void processDir(File dir, ZipOutputStream zos, Set<String> seen) {
        def base = dir.toPath()
        Files.walk(base).withCloseable { stream ->
            stream.filter { Files.isRegularFile(it) }.forEach { path ->
                def entryName = base.relativize(path).toString().replace("\\", "/")
                if (!seen.add(entryName)) {
                    return
                }
                zos.putNextEntry(new ZipEntry(entryName))
                def bytes = Files.readAllBytes(path)
                if (entryName.endsWith(".class")) {
                    // null = 跳过/已插过/编译失败：必须写回原始字节，否则这个类会从输出 jar 里消失
                    def modified = transformBytes(bytes)
                    zos.write(modified != null ? modified : bytes)
                } else {
                    zos.write(bytes)
                }
                zos.closeEntry()
            }
        }
    }

    private static void appendClasspath(ClassPool pool, File file, File tmpDir) {
        if (file == null || !file.exists()) {
            return
        }
        try {
            if (file.directory || file.name.endsWith(".jar") || file.name.endsWith(".zip")) {
                pool.appendClassPath(file.absolutePath)
            } else if (file.name.endsWith(".aar")) {
                def extracted = extractAarClassesJar(file, tmpDir)
                if (extracted != null) {
                    pool.appendClassPath(extracted.absolutePath)
                }
            }
        } catch (NotFoundException ignored) {
        }
    }

    private static File extractAarClassesJar(File aar, File tmpDir) {
        def out = new File(tmpDir, aar.name + "-classes.jar")
        if (out.isFile() && out.length() > 0) {
            return out
        }
        def zip = new java.util.zip.ZipFile(aar)
        try {
            def entry = zip.getEntry("classes.jar")
            if (entry == null) {
                return null
            }
            out.parentFile.mkdirs()
            out.withOutputStream { os ->
                zip.getInputStream(entry).withStream { is -> os << is }
            }
        } finally {
            zip.close()
        }
        return out
    }

    byte[] transformBytes(byte[] classBytes) {
        CtClass ctClass = null
        try {
            // makeClass 从字节流读，避免 ClassPool.get(name) 拿到错误路径上的旧 class
            ctClass = pool.makeClass(new ByteArrayInputStream(classBytes))
            if (!transformClass(ctClass)) {
                return null
            }
            return ctClass.toBytecode()
        } catch (Throwable t) {
            def name = ctClass != null ? ctClass.name : "unknown"
            println "[hotfix] skip class ${name} : ${t.message}"
            reportLines << "SKIP_CLASS ${name} ${t.message}"
            return null
        } finally {
            // 必须 detach，否则 ClassPool 缓存冻结的 CtClass，增量编译会改不了
            ctClass?.detach()
        }
    }

    void writeReport(File reportFile) {
        def summary = "classes=${classCount} methods=${methodCount} skippedMethods=${skipMethodCount}"
        reportLines << summary
        if (reportFile != null) {
            reportFile.parentFile?.mkdirs()
            reportFile.setText(reportLines.join("\n") + "\n", "UTF-8")
        }
        println "[hotfix] inserted ${summary}"
    }

    private boolean transformClass(CtClass ctClass) {
        if (ctClass.isInterface() || ctClass.isAnnotation() || ctClass.isEnum() || ctClass.isPrimitive()) {
            return false
        }
        if (shouldSkipClass(ctClass.getName())) {
            return false
        }
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }
        try {
            ctClass.getDeclaredField(FIELD_NAME)
            // 字段已存在说明插过。再插会重复 if，包体积翻倍，运行也乱
            return false
        } catch (NotFoundException ignored) {
        }

        // 运行时 HotfixManager 反射写的就是这个字段。源码里没有，所以只能反射，不能 HotfixTarget.changeQuickRedirect = ...
        ctClass.addField(CtField.make(
                "public static volatile ${REDIRECT_TYPE} ${FIELD_NAME};",
                ctClass))
        ctClass.declaredConstructors.each { insertHook(ctClass, it, true) }
        ctClass.declaredMethods.each { insertHook(ctClass, it, false) }
        classCount++
        return true
    }

    private void insertHook(CtClass ctClass, CtBehavior behavior, boolean constructor) {
        int mod = behavior.modifiers
        if (Modifier.isAbstract(mod) || Modifier.isNative(mod)) {
            skipMethodCount++
            return
        }
        int access = behavior.methodInfo.accessFlags
        if ((access & AccessFlag.SYNTHETIC) != 0 || (access & AccessFlag.BRIDGE) != 0) {
            // Kotlin 编译器生成的桥接 / access$ 方法，插了容易和真实方法抢补丁
            skipMethodCount++
            return
        }
        if ("<clinit>" == behavior.name) {
            // 静态初始化里插热修复没有业务意义，还可能在类加载阶段就去反射
            skipMethodCount++
            return
        }
        boolean isStatic = Modifier.isStatic(mod)
        def methodId = "${ctClass.name}.${behavior.name}:${behavior.methodInfo.descriptor}:${isStatic ? "1" : "0"}"
        def code = buildInsertCode(methodId, isStatic, constructor)
        try {
            if (constructor && behavior instanceof CtConstructor) {
                // JVM 要求构造方法里 super()/this() 必须最先执行。
                // insertBefore 会插到 super 前面，未初始化的 this 传给 PatchProxy 会 VerifyError。
                // insertBeforeBody 插在 super 之后、原方法体之前，这才合法。
                ((CtConstructor) behavior).insertBeforeBody(code)
            } else {
                behavior.insertBefore(code)
            }
            methodCount++
            reportLines << "INSERT ${methodId}"
        } catch (CannotCompileException e) {
            skipMethodCount++
            reportLines << "SKIP_METHOD ${methodId} ${e.message}"
            println "[hotfix] skip method ${methodId} : ${e.message}"
        }
    }

    /**
     * 生成插到方法最前面的 Java 源码片段，由 Javassist 编进 bytecode。
     *
     * $args / $0 / $r 是 Javassist 特殊变量，不要改成普通 Java：
     *   $args = 装箱后的参数 Object[]
     *   $0    = this（静态方法没有 this，只能传 null）
     *   $r    = 按真实返回类型转换；void 方法会变成「执行表达式然后 return」
     *
     * 先读到局部变量 changeQuickRedirectLocal：加载补丁和调用可能不在同一线程，
     * 避免 if 判断时非 null、dispatch 时已被 clear 成 null。
     */
    private static String buildInsertCode(String methodId, boolean isStatic, boolean constructor) {
        def thisExpr = isStatic ? "(java.lang.Object)null" : "\$0"
        def quotedId = "\"${methodId.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        def check = """${REDIRECT_TYPE} changeQuickRedirectLocal = ${FIELD_NAME};
if (changeQuickRedirectLocal != null && ${PROXY_TYPE}.isSupport(\$args, ${thisExpr}, changeQuickRedirectLocal, ${isStatic}, ${quotedId})) {
"""
        if (constructor) {
            return check + """    ${PROXY_TYPE}.accessDispatch(\$args, ${thisExpr}, changeQuickRedirectLocal, ${isStatic}, ${quotedId});
    return;
}
"""
        }
        return check + """    return (\$r) ${PROXY_TYPE}.accessDispatch(\$args, ${thisExpr}, changeQuickRedirectLocal, ${isStatic}, ${quotedId});
}
"""
    }

    static boolean shouldSkipClass(String name) {
        if (name == null) {
            return true
        }
        // PatchProxy 自己若被插桩，isSupport 里一读 changeQuickRedirect 又进 isSupport，死递归
        if (name == PROXY_TYPE || name.startsWith(PROXY_TYPE + "\$") || name == REDIRECT_TYPE) {
            return true
        }
        // 补丁类和 Manager 是「加载补丁」的实现，不能再被补丁拦截
        if (name == "com.stone.interview.hotfix.HotfixManager"
                || name.startsWith("com.stone.interview.hotfix.HotfixManager\$")
                || name.startsWith("com.stone.interview.hotfix.patch.")) {
            return true
        }
        if (name.startsWith("android.") || name.startsWith("androidx.")
                || name.startsWith("com.google.") || name.startsWith("kotlin.")
                || name.startsWith("kotlinx.") || name.startsWith("dagger.")
                || name.startsWith("hilt_aggregated_deps.")
                || name.startsWith("javax.") || name.startsWith("java.")) {
            return true
        }
        def simple = name.substring(name.lastIndexOf('.') + 1)
        if (simple == "R" || simple.startsWith("R\$") || simple == "BR"
                || simple == "BuildConfig" || simple == "DataBinderMapperImpl") {
            return true
        }
        if (name.contains("DataBinderMapper") || name.contains("DataBindingComponent")) {
            return true
        }
        if (simple.startsWith("Hilt_") || simple.endsWith("_GeneratedInjector")
                || name.contains("_HiltModules") || name.contains("Hilt_")) {
            return true
        }
        if (simple.startsWith("Dagger") || simple.endsWith("_Factory")
                || simple.endsWith("_MembersInjector") || name.contains("Dagger")) {
            return true
        }
        if (simple.endsWith("Binding") || simple.endsWith("BindingImpl")
                || simple.toLowerCase(Locale.US).contains("databinding")) {
            return true
        }
        return false
    }
}
