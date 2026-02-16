plugins {
  kotlin("jvm") version "2.3.0"
  kotlin("plugin.serialization").version("2.3.0")
  id("com.diffplug.spotless") version "7.0.2"
}

group = "md.injester"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
  testImplementation(platform("org.junit:junit-bom:5.9.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.junit.jupiter:junit-jupiter-params")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("io.kotest:kotest-assertions-core:5.5.4")
}

kotlin { jvmToolchain(17) }

tasks.test { useJUnitPlatform() }

spotless {
  kotlin { ktfmt() }
  kotlinGradle { ktfmt() }
}
