fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()

version = gitInfo.version

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.6.10"
    id("java")
    id("org.jetbrains.intellij") version "1.4.0"
    id("com.palantir.git-version") version "0.13.0"
}

dependencies {
    implementation(kotlin("reflect"))
}

intellij {
    version.set(properties["idea-version"] as String)
    pluginName.set("Composer Dump-Autoload")
    updateSinceUntilBuild.set(false)
    downloadSources.set(true)
    plugins.set(
        listOf("com.jetbrains.php:${properties["php-version"] as String}")
    )
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    patchPluginXml {
        setVersion(project.version)
    }

    publishPlugin {
        dependsOn("patchPluginXml")
        token.set(System.getenv("JB_TOKEN"))
    }
}
