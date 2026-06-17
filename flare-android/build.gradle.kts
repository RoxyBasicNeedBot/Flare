import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

android {
    namespace = "com.roxy.flare.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.android.jvm.get().toInt())
        targetCompatibility = JavaVersion.toVersion(libs.versions.android.jvm.get().toInt())
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.jvm.get()))
    }
}

dependencies {
    implementation(project(":flare-core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.dynamicanimation)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.RoxyBasicNeedBot"
                artifactId = "flare-android"
                version = "1.0.0"
                pom {
                    name.set("Flare Android")
                    description.set("Android View system implementation of Flare alerts & toasts")
                    url.set("https://github.com/RoxyBasicNeedBot/Flare")
                    licenses {
                        license {
                            name.set("The BSD 3-Clause License")
                            url.set("https://opensource.org/licenses/BSD-3-Clause")
                        }
                    }
                    developers {
                        developer {
                            id.set("RoxyBasicNeedBot")
                            name.set("Roxy")
                            email.set("RoxyBasicNeedBot@users.noreply.github.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/RoxyBasicNeedBot/Flare.git")
                        developerConnection.set("scm:git:ssh://github.com/RoxyBasicNeedBot/Flare.git")
                        url.set("https://github.com/RoxyBasicNeedBot/Flare")
                    }
                }
            }
        }
    }
}
