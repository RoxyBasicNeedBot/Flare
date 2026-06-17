plugins {
    alias(libs.plugins.kotlinJvm)
    `maven-publish`
}

dependencies {
    // Zero dependencies (pure Kotlin library)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.github.RoxyBasicNeedBot"
            artifactId = "flare-core"
            version = "1.0.0"
            pom {
                name.set("Flare Core")
                description.set("Core logic for Flare alerts & toasts")
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
