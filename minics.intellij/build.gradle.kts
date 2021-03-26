import mb.spoofax.compiler.gradle.plugin.*
import mb.spoofax.compiler.util.*

plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.spoofax.compiler.gradle.intellij")
}

fun compositeBuild(name: String) = "$group:$name:$version"

dependencies {
  // Platforms
  configurations.forEach { add(it.name, platform(compositeBuild("spoofax.depconstraints"))) }
}

languageIntellijProject {
  adapterProject.set(project(":minics.spoofax"))
  // We only need to manually provide MinicsCompletionContributor and plugins.xml,
  // but because we cannot choose to not generate plugins.xml (yet),
  // we set it to manual and provide all the files.
  compilerInput {
    classKind(ClassKind.Manual)
  }
}

intellij {
  version = "2020.2.4" // 2020.2.4 is the last version that can be built with Java 8.
//  version = "2020.3"
}

tasks {
  named("buildSearchableOptions") {
    enabled = false // Skip non-incremental and slow `buildSearchableOptions` task from `org.jetbrains.intellij`.
  }
  named<org.jetbrains.intellij.tasks.RunIdeTask>("runIde") {
    //this.jbrVersion("11_0_2b159") // Set JBR version because the latest one cannot be downloaded.
    // HACK: make task depend on the runtime classpath to forcefully make it depend on `spoofax.intellij`, which the
    //       `org.jetbrains.intellij` plugin seems to ignore. This is probably because `spoofax.intellij` is a plugin
    //       but is not listed as a plugin dependency. This hack may not work when publishing this plugin.
    dependsOn(configurations.runtimeClasspath)
  }
}

// Skip non-incremental, slow, and unnecessary buildSearchableOptions task from IntelliJ.
tasks.getByName("buildSearchableOptions").onlyIf { false }
