pluginManagement {
    repositories {
        // 必须放最前：plugins { id("com.stone.hotfix") } 从这里解析，不是 implementation
        maven {
            url = uri(rootDir.resolve("javassist/repo"))
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Interview"
include(":app")
include(":common")
include(":mydemo")
include(":restaurant")
