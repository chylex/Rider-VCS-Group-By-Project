@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.0"
	id("org.jetbrains.intellij") version "1.15.0"
}

group = "com.chylex.intellij.rider.vcsgroupbyproject"
version = "1.0.2"

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(17)
}

intellij {
	type.set("RD")
	version.set("2023.1")
	updateSinceUntilBuild.set(false)
}

tasks.patchPluginXml {
	sinceBuild.set("231")
}

tasks.buildSearchableOptions {
	enabled = false
}

tasks.withType<KotlinCompile> {
	kotlinOptions.freeCompilerArgs = listOf(
		"-Xjvm-default=all"
	)
}
