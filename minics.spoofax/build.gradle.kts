import mb.spoofax.compiler.adapter.*
import mb.spoofax.compiler.adapter.data.*
import mb.spoofax.compiler.gradle.plugin.*
import mb.spoofax.compiler.util.*
import mb.spoofax.core.language.command.*
import mb.spoofax.compiler.util.StringUtil.*

plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.spoofax.compiler.gradle.adapter")
}

dependencies {
  api("org.metaborg:statix.common")
  api("org.metaborg:statix.completions")
  api("org.metaborg:statix.strategies")
}

languageAdapterProject {
  languageProject.set(project(":minics"))
  compilerInput {
    withParser()
    withStyler()
    withCompleter().run {
      classKind(ClassKind.Manual)
      baseCompleteTaskDef("mb.minics.spoofax.task", "MinicsComplete")
    }
    withStrategoRuntime()
    withConstraintAnalyzer()
    project.configureCompilerInput()
  }
}

fun AdapterProjectCompiler.Input.Builder.configureCompilerInput() {
  val packageId = "mb.minics.spoofax"
  val taskPackageId = "$packageId.task"
  val commandPackageId = "$packageId.command"

  // Custom component and additional modules
  baseComponent(packageId, "GeneratedMinicsComponent")
  extendComponent(packageId, "MinicsComponent")
  baseInstance(packageId, "GeneratedMinicsInstance")
  extendInstance(packageId, "MinicsInstance")
  addAdditionalModules(packageId, "SpoofaxModule")
  addAdditionalModules(packageId, "MinicsModuleExt")


  val showArgsType = TypeInfo.of("mb.minics.spoofax.task", "MinicsShowArgs")

  // Compile file
  val compileFile = TypeInfo.of(taskPackageId, "MinicsCompileFile")
  addTaskDefs(compileFile)
  val compileFileCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsCompileFileCommand")
    .taskDefType(compileFile)
    .argType(compileFile.appendToId(".Args"))
    .displayName("Compile file (list literals)")
    .description("")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous, CommandExecutionType.AutomaticContinuous)
    .addAllParams(listOf(
      ParamRepr.of("file", TypeInfo.of("mb.resource.hierarchical", "ResourcePath"), true, ArgProviderRepr.context(CommandContextType.File))
    ))
    .build()
  addCommandDefs(compileFileCommand)

  // Compile file (Alt)
  val compileFileAlt = TypeInfo.of(taskPackageId, "MinicsCompileFileAlt")
  addTaskDefs(compileFileAlt)
  val compileFileAltCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsCompileFileAltCommand")
    .taskDefType(compileFileAlt)
    .argType(compileFileAlt.appendToId(".Args"))
    .displayName("Alternative compile file")
    .description("")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous, CommandExecutionType.AutomaticContinuous)
    .addAllParams(listOf(
      ParamRepr.of("file", TypeInfo.of("mb.resource.hierarchical", "ResourcePath"), true, ArgProviderRepr.context(CommandContextType.File)),
      ParamRepr.of("listDefNames", TypeInfo.ofBoolean(), false, ArgProviderRepr.value("true")),
      ParamRepr.of("base64Encode", TypeInfo.ofBoolean(), false, ArgProviderRepr.value("false")),
      ParamRepr.of("compiledFileNameSuffix", TypeInfo.ofString(), false, ArgProviderRepr.value(doubleQuote("defnames.aterm")))
    ))
    .build()
  addCommandDefs(compileFileAltCommand)

  // Compile directory
  val compileDirectory = TypeInfo.of(taskPackageId, "MinicsCompileDirectory")
  addTaskDefs(compileDirectory)
  val compileDirectoryCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsCompileDirectoryCommand")
    .taskDefType(compileDirectory)
    .argType(compileDirectory.appendToId(".Args"))
    .displayName("Compile directory (list definition names)")
    .description("")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous, CommandExecutionType.AutomaticContinuous)
    .addAllParams(listOf(
      ParamRepr.of("dir", TypeInfo.of("mb.resource.hierarchical", "ResourcePath"), true, ArgProviderRepr.context(CommandContextType.Directory))
    ))
  .build()
  addCommandDefs(compileDirectoryCommand)

  // Show parsed AST
  val showParsedAst = TypeInfo.of(taskPackageId, "MinicsShowParsedAst")
  addTaskDefs(showParsedAst)
  val showParsedAstCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsShowParsedAstCommand")
    .taskDefType(showParsedAst)
    .argType(showArgsType)
    .displayName("Show parsed AST")
    .description("Shows the parsed Abstract Syntax Tree of the program.")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
    .addAllParams(listOf(
      ParamRepr.of("resource", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.ResourceKey)),
      ParamRepr.of("region", TypeInfo.of("mb.common.region", "Region"), false, ArgProviderRepr.context(CommandContextType.Region))
    ))
    .build()
  addCommandDefs(showParsedAstCommand)

  // Show desugared AST
  val showDesugaredAst = TypeInfo.of(taskPackageId, "MinicsShowDesugaredAst")
  addTaskDefs(showDesugaredAst)
  val showDesugaredAstCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsShowDesugaredAstCommand")
    .taskDefType(showDesugaredAst)
    .argType(showArgsType)
    .displayName("Show desugared AST")
    .description("Shows the desugared Abstract Syntax Tree of the program.")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
    .addAllParams(listOf(
      ParamRepr.of("resource", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.ResourceKey)),
      ParamRepr.of("region", TypeInfo.of("mb.common.region", "Region"), false, ArgProviderRepr.context(CommandContextType.Region))
    ))
    .build()
  addCommandDefs(showDesugaredAstCommand)

  // Show analyzed AST
  val showAnalyzedAst = TypeInfo.of(taskPackageId, "MinicsShowAnalyzedAst")
  addTaskDefs(showAnalyzedAst)
  val showAnalyzedAstCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsShowAnalyzedAstCommand")
    .taskDefType(showAnalyzedAst)
    .argType(showArgsType)
    .displayName("Show analyzed AST")
    .description("Shows the analyzed Abstract Syntax Tree of the program.")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
    .addAllParams(listOf(
      ParamRepr.of("resource", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.ResourceKey)),
      ParamRepr.of("region", TypeInfo.of("mb.common.region", "Region"), false, ArgProviderRepr.context(CommandContextType.Region))
    ))
    .build()
  addCommandDefs(showAnalyzedAstCommand)

  // Show pretty-printed text
  val showPrettyPrintedText = TypeInfo.of(taskPackageId, "MinicsShowPrettyPrintedText")
  addTaskDefs(showPrettyPrintedText)
  val showPrettyPrintedTextCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsShowPrettyPrintedTextCommand")
    .taskDefType(showPrettyPrintedText)
    .argType(showArgsType)
    .displayName("Show pretty-printed text")
    .description("Shows a pretty-printed version of the program.")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
    .addAllParams(listOf(
      ParamRepr.of("resource", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.ResourceKey)),
      ParamRepr.of("region", TypeInfo.of("mb.common.region", "Region"), false, ArgProviderRepr.context(CommandContextType.Region))
    ))
    .build()
  addCommandDefs(showPrettyPrintedTextCommand)

  // Show scope graph
  val showScopeGraph = TypeInfo.of(taskPackageId, "MinicsShowScopeGraph")
  addTaskDefs(showScopeGraph)
  val showScopeGraphCommand = CommandDefRepr.builder()
    .type(commandPackageId, "MinicsShowScopeGraphCommand")
    .taskDefType(showScopeGraph)
    .argType(showArgsType)
    .displayName("Show scope graph")
    .description("Shows the scope graph for the program")
    .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
    .addAllParams(listOf(
      ParamRepr.of("resource", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.ResourceKey)),
      ParamRepr.of("region", TypeInfo.of("mb.common.region", "Region"), false, ArgProviderRepr.value("null"))
    ))
    .build()
  addCommandDefs(showScopeGraphCommand)

  // Menu bindings
  val mainAndEditorMenu = listOf(
    MenuItemRepr.menu("Compile",
      MenuItemRepr.menu("Static Semantics",
        CommandActionRepr.builder().manualOnce(compileFileCommand).fileRequired().buildItem(),
        CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- default").fileRequired().buildItem(),
        CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- list literal values instead", mapOf("listDefNames" to "false", "compiledFileNameSuffix" to doubleQuote("litvals.aterm"))).fileRequired().buildItem(),
        CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- base64 encode", mapOf("base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("defnames_base64.txt"))).fileRequired().buildItem(),
        CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- list literal values instead + base64 encode", mapOf("listDefNames" to "false", "base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("litvals_base64.txt"))).fileRequired().buildItem(),
        CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- default").fileRequired().buildItem(),
        CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- list literal values instead", mapOf("listDefNames" to "false", "compiledFileNameSuffix" to doubleQuote("litvals.aterm"))).fileRequired().buildItem(),
        CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- base64 encode", mapOf("base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("defnames_base64.txt"))).fileRequired().buildItem(),
        CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- list literal values instead + base64 encode", mapOf("listDefNames" to "false", "base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("litvals_base64.txt"))).fileRequired().buildItem()
      )
    ),
    MenuItemRepr.menu("Debug",
      MenuItemRepr.menu("Syntax",
        CommandActionRepr.builder().manualOnce(showParsedAstCommand).buildItem(),
        CommandActionRepr.builder().manualContinuous(showParsedAstCommand).buildItem(),
        CommandActionRepr.builder().manualOnce(showPrettyPrintedTextCommand).buildItem(),
        CommandActionRepr.builder().manualContinuous(showPrettyPrintedTextCommand).buildItem()
      ),
      MenuItemRepr.menu("Static Semantics",
        CommandActionRepr.builder().manualOnce(showAnalyzedAstCommand).buildItem(),
        CommandActionRepr.builder().manualContinuous(showAnalyzedAstCommand).buildItem()
      ),
      MenuItemRepr.menu("Transformations",
        CommandActionRepr.builder().manualOnce(showDesugaredAstCommand).buildItem(),
        CommandActionRepr.builder().manualContinuous(showDesugaredAstCommand).buildItem()
      )
    )
  )
  addAllMainMenuItems(mainAndEditorMenu)
  addAllEditorContextMenuItems(mainAndEditorMenu)
  addResourceContextMenuItems(
    MenuItemRepr.menu("Compile",
      CommandActionRepr.builder().manualOnce(compileFileCommand).fileRequired().buildItem(),
      CommandActionRepr.builder().manualOnce(compileDirectoryCommand).directoryRequired().buildItem(),
      CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- default").fileRequired().buildItem(),
      CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- list literal values instead", mapOf("listDefNames" to "false", "compiledFileNameSuffix" to doubleQuote("litvals.aterm"))).fileRequired().buildItem(),
      CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- base64 encode", mapOf("base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("defnames_base64.txt"))).fileRequired().buildItem(),
      CommandActionRepr.builder().manualOnce(compileFileAltCommand, "- list literal values instead + base64 encode", mapOf("listDefNames" to "false", "base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("litvals_base64.txt"))).fileRequired().buildItem(),
      CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- default").fileRequired().buildItem(),
      CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- list literal values instead", mapOf("listDefNames" to "false", "compiledFileNameSuffix" to doubleQuote("litvals.aterm"))).fileRequired().buildItem(),
      CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- base64 encode", mapOf("base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("defnames_base64.txt"))).fileRequired().buildItem(),
      CommandActionRepr.builder().manualContinuous(compileFileAltCommand, "- list literal values instead + base64 encode", mapOf("listDefNames" to "false", "base64Encode" to "true", "compiledFileNameSuffix" to doubleQuote("litvals_base64.txt"))).fileRequired().buildItem()
    ),
    MenuItemRepr.menu("Debug",
      MenuItemRepr.menu("Syntax",
        CommandActionRepr.builder().manualOnce(showParsedAstCommand).buildItem(),
        CommandActionRepr.builder().manualOnce(showPrettyPrintedTextCommand).buildItem()
      ),
      MenuItemRepr.menu("Static Semantics",
        CommandActionRepr.builder().manualOnce(showAnalyzedAstCommand).buildItem()
      ),
      MenuItemRepr.menu("Transformations",
        CommandActionRepr.builder().manualOnce(showDesugaredAstCommand).buildItem()
      )
    )
  )
}
