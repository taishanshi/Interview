package com.stone.interview.javassist

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import kotlin.jvm.functions.Function1
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 对应课程 ModifyPlugin：把 Javassist 挂进 Android 编译流水线。
 *
 * 课程写法：
 *   project.android.registerTransform(new ModifyTransform(project))
 * 现在不能这么写：AGP 8 删除了 Transform。替代方案是 androidComponents + ScopedArtifacts，
 * 让 AGP 把「本 module 的 CLASSES」喂给我们的任务，改完再交给 dex。
 *
 * 为什么用 withPlugin 而不是 apply() 里直接拿 android 扩展：
 *   Hotfix 插件和 com.android.application 都在 APP 的 plugins {} 里。
 *   如果有人把 hotfix 写在 android 前面，此时还没有 AndroidComponentsExtension，直接 getByType 会崩。
 *   withPlugin("com.android.application", hook) 的意思是：
 *     - 若 android 插件已经 apply，立刻执行 hook
 *     - 若还没 apply，等它 apply 完再执行 hook
 *   这样不依赖 plugins 块里的书写顺序。
 */
class HotfixPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Action hook = { plugin ->
            // android 插件已就绪，才能拿到 androidComponents（AGP 7+ 的变体 API）
            AndroidComponentsExtension androidComponents = project.extensions.getByType(AndroidComponentsExtension)
            // Groovy 不能写成 onVariants { }：Kotlin 重载是 (selector, Action)，单闭包对不上
            androidComponents.onVariants(androidComponents.selector().all(), { variant ->
                def cap = variant.name.capitalize()
                def taskProvider = project.tasks.register("insertHotfix${cap}", HotfixTransformTask) { task ->
                    task.group = "hotfix"
                    task.description = "编译期 Javassist 插入热修复判断 (${variant.name})"
                }
                // Scope.PROJECT：只改本 module 自己编出来的 class。
                // 不用 ALL，否则会扫到 androidx 等依赖 jar，又慢又容易把三方库改坏。
                // 课程 SCOPE_FULL_PROJECT 其实也对 jar 原样 copy，效果接近 PROJECT。
                //
                // toTransform 会让 AGP 自动接线：
                //   输入目录 ← compileDebugKotlin / compileDebugJavaWithJavac 的 classes
                //   输入 jar  ← 例如 processDebugResources 产出的 R.jar
                //   输出     → app/build/intermediates/classes/<variant>/PROJECT/insertHotfixXxx/classes.jar
                // 后面的 transform*Asm、dexBuilder 读的是这份输出，所以不必再 finalizedBy / dependsOn。
                variant.artifacts
                        .forScope(ScopedArtifacts.Scope.PROJECT)
                        .use(taskProvider)
                        .toTransform(
                                ScopedArtifact.CLASSES.INSTANCE,
                                { HotfixTransformTask t -> t.allJars } as Function1,
                                { HotfixTransformTask t -> t.allDirectories } as Function1,
                                { HotfixTransformTask t -> t.output } as Function1
                        )
            } as Action)
        }
        // 等 Android 插件就绪后再挂任务。application / library 都支持，纯 java-library 没有 CLASSES 产物，不挂。
        project.pluginManager.withPlugin("com.android.application", hook)
        project.pluginManager.withPlugin("com.android.library", hook)
    }
}
