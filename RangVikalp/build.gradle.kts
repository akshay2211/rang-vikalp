import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
            namespace = "io.ak1.rangvikalp"
            compileSdk =
                libs.versions.android.compileSdk
                    .get()
                    .toInt()
            minSdk =
                libs.versions.android.minSdk
                    .get()
                    .toInt()

            compilerOptions {
                jvmTarget = JvmTarget.JVM_21
            }
        }

        jvm()

        js {
            browser()
        }

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser()
        }

        val xcfName = "RangVikalp"
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = xcfName
                isStatic = true
            }
        }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

/*        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.runner)
                implementation(libs.androidx.testExt.junit)
            }
        }*/

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }

}


mavenPublishing {
    pom {
        name.set("RangVikalp")
        description.set(
            "Kotlin Multiplatform colour picker built on Compose Multiplatform. " +
                    "Ships a tabbed HSV picker plus drop-in components: saturation/value box & circle, " +
                    "linear and arc hue / alpha sliders, preset swatch grid with shade expansion, hex + copy, " +
                    "and a quick-pick presets row. Hoisted state, full dark/light theming, no extra " +
                    "drawable resources. Runs on Android, iOS, Web (WASM/JS), and JVM from a single shared codebase."
        )
        inceptionYear.set("2022")
        url.set("https://github.com/akshay2211/rang-vikalp")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("akshay2211")
                name.set("Akshay Sharma")
                email.set("fxn769@gmail.com")
                url.set("https://akshay2211.github.io/")
            }
        }

        scm {
            url.set("https://github.com/akshay2211/rang-vikalp")
            connection.set("scm:git:git://github.com/akshay2211/rang-vikalp.git")
            developerConnection.set("scm:git:git@github.com:akshay2211/rang-vikalp.git")
        }

        issueManagement {
            system.set("Github")
            url.set("https://github.com/akshay2211/rang-vikalp/issues")
        }
    }
}
