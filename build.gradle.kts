@file:Suppress("ConvertLambdaToReference")

plugins {
	kotlin("jvm")
	id("org.jetbrains.intellij.platform")
}

group = "com.chylex.intellij.rider.vcsgroupbyproject"
version = "1.0.3"

repositories {
	mavenCentral()
	
	intellijPlatform {
		defaultRepositories()
	}
}

dependencies {
	intellijPlatform {
		rider("2025.3") {
			useInstaller = false
		}
		
		bundledModules("intellij.platform.vcs.impl")
		bundledModules("intellij.platform.vcs.impl.shared")
	}
}

intellijPlatform {
	pluginConfiguration {
		ideaVersion {
			sinceBuild.set("253")
			untilBuild.set(provider { null })
		}
	}
	
	pluginVerification {
		freeArgs.add("-mute")
		freeArgs.add("TemplateWordInPluginId")
		
		ides {
			recommended()
		}
	}
	
	buildSearchableOptions = false
}

kotlin {
	jvmToolchain(21)
	
	compilerOptions {
		freeCompilerArgs = listOf(
			"-X" + "jvm-default=all",
			"-X" + "lambdas=indy"
		)
	}
}
