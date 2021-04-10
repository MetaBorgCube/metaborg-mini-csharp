import mb.spoofax.compiler.gradle.plugin.*
import mb.spoofax.compiler.gradle.spoofax2.plugin.*
import mb.spoofax.compiler.language.*
import mb.spoofax.compiler.spoofax2.language.*
import mb.spoofax.compiler.util.*

plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
  id("org.metaborg.spoofax.compiler.gradle.spoofax2.language")
}

fun compositeBuild(name: String) = "$group:$name:$version"

dependencies {
  // Platforms
  configurations.forEach { add(it.name, platform(compositeBuild("spoofax.depconstraints"))) }

  testImplementation("org.metaborg:log.backend.slf4j")
  testImplementation("org.slf4j:slf4j-simple")
  testCompileOnly("org.checkerframework:checker-qual-android")
}

languageProject {
  shared {
    name("Microcs")
    defaultPackageId("mb.microcs")
    fileExtensions(listOf("ucs"))
  }
  compilerInput {
    withParser().run {
      startSymbol("Start")
    }
    withStyler()
    withCompleter().run {
      classKind(ClassKind.Manual)
    }
    withConstraintAnalyzer().run {
      enableNaBL2(false)
      enableStatix(true)
      multiFile(true)
    }
    withStrategoRuntime()
    withClassloaderResources()
  }
}

spoofax2BasedLanguageProject {
  compilerInput {
    withParser()
    withStyler()
    withConstraintAnalyzer().run {
      copyStatix(true)
    }
    withStrategoRuntime().run {
      copyCtree(true)
      copyClasses(false)
    }

    val spoofax2GroupId = "org.metaborg"
    val spoofax2Version = System.getProperty("spoofax2Version")
    project.languageSpecificationDependency(GradleDependency.project(":microcs.spoofaxcore"))
    //project.languageSpecificationDependency(GradleDependency.module("$spoofax2GroupId:microcs.spoofaxcore:$spoofax2Version"))
  }
}
