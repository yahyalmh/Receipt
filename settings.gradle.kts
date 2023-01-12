pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Receipt"
include(":app")

include(":ui:main")
include(":ui:common")
include(":ui:home")
include(":ui:setting")
include(":ui:scan")

include(":data:mlkit")
include(":data:firebase")
include(":data:common")
include(":data:datastore")
