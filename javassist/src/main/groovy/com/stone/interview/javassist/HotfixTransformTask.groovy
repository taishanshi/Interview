package com.stone.interview.javassist

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * 对应课程 ModifyTransform：拿 class 输入 → Javassist 改 → 交给下一环。
 *
 * 课程从 TransformInvocation.inputs 自己找目录/jar，再用 outputProvider.getContentLocation + copy。
 * AGP 9 改成框架往任务上「注入」三个属性（下面的 allJars / allDirectories / output），
 * 我们只负责读写这些文件。输出必须写到 output 指向的那个 jar，AGP 才认。
 *
 * 输入大概在：
 *   app/build/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes
 *   app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes
 *   app/build/intermediates/compile_and_runtime_r_class_jar/debug/.../R.jar
 * 输出在：
 *   app/build/intermediates/classes/debug/PROJECT/insertHotfixDebug/classes.jar
 * 插桩清单（给人看的，不是给 AGP 的）：
 *   app/build/hotfix/insertHotfixDebug.txt
 */
abstract class HotfixTransformTask extends DefaultTask {

    /**
     * AGP 注入的 jar 形态 class（常见是 R.jar）。
     * 为什么不改：课程也是 jar 原样 copy；R 类没有业务方法，改了也没有热修复价值。
     */
    @InputFiles
    abstract ListProperty<RegularFile> getAllJars()

    /**
     * AGP 注入的 class 目录，来自 kotlinc / javac。
     * 业务代码在这里，Javassist 只处理这个目录里的 .class。
     */
    @InputFiles
    abstract ListProperty<Directory> getAllDirectories()

    /**
     * AGP 指定的唯一出口。必须把改完的 class 打进这个 jar，
     * 否则 Asm / dex 仍会用未插桩的原始 class，热修复判断不会进 APK。
     */
    @OutputFile
    abstract RegularFileProperty getOutput()

    @TaskAction
    void transform() {
        def outFile = output.get().asFile
        outFile.parentFile.mkdirs()
        def tmpDir = new File(project.buildDir, "hotfix/tmp")
        def jars = allJars.get().collect { it.asFile }
        def dirs = allDirectories.get().collect { it.asFile }
        def classpath = []
        classpath.addAll(jars)
        classpath.addAll(dirs)
        // 输入目录也要进 ClassPool：插桩代码会引用同模块的 PatchProxy、ChangeQuickRedirect
        // android.jar：解析 Activity 等。AGP 9 的 android {} 已没有旧 bootClasspath，只能自己找 SDK
        def weaver = HotfixWeaver.create(findAndroidJar(), classpath, tmpDir)
        weaver.writeToJar(jars, dirs, outFile)
        weaver.writeReport(new File(project.buildDir, "hotfix/${name}.txt"))
    }

    private File findAndroidJar() {
        def sdkDir = sdkDirectory()
        def platforms = new File(sdkDir, "platforms")
        for (name in ["android-36.1", "android-36", "android-35", "android-34"]) {
            def jar = new File(platforms, "${name}/android.jar")
            if (jar.isFile()) {
                return jar
            }
        }
        def found = platforms.listFiles()?.findAll { new File(it, "android.jar").isFile() }?.max { it.name }
        if (found == null) {
            throw new GradleException("找不到 android.jar")
        }
        return new File(found, "android.jar")
    }

    private File sdkDirectory() {
        def local = new File(project.rootProject.projectDir, "local.properties")
        if (local.isFile()) {
            def props = new Properties()
            local.withInputStream { props.load(it) }
            def dir = props.getProperty("sdk.dir")
            if (dir) {
                return new File(dir)
            }
        }
        def env = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        if (env) {
            return new File(env)
        }
        throw new GradleException("找不到 Android SDK")
    }
}
