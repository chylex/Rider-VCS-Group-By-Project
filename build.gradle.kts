@file:Suppress("ConvertLambdaToReference")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.10"
	id("org.jetbrains.intellij") version "1.6.0"
}

group = "com.chylex.intellij.rider.vcsgroupbyproject"
version = "1.0.1"

repositories {
	mavenCentral()
}

intellij {
	type.set("RD")
	version.set("2022.1")
	updateSinceUntilBuild.set(false)
}

tasks.patchPluginXml {
	sinceBuild.set("211")
	untilBuild.set("222") // 222 requires a rebuild
}

tasks.buildSearchableOptions {
	enabled = false
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "11"
	kotlinOptions.freeCompilerArgs = listOf(
		"-Xjvm-default=enable"
	)
}
