import mb.spoofax.compiler.util.*

plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.spoofax.compiler.gradle.eclipse")
}

fun compositeBuild(name: String) = "$group:$name:$version"

dependencies {
  // Platforms
  configurations.forEach { add(it.name, platform(compositeBuild("spoofax.depconstraints"))) }
}

languageEclipseProject {
  adapterProject.set(project(":microcs.spoofax"))
}
