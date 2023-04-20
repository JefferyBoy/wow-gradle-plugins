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
rootProject.name = "wow-gradle-plugins"
includeBuild("./plugin/aspectj")
includeBuild("./plugin/tencent-legu")

include(":library:easy-permission")

include(":example:legu-app")
include(":example:aspectj-app")
include(":example:aspectj-library")
