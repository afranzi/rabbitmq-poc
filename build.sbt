import scalariform.formatter.preferences._

// Set autoAPIMappings for sbt to tell scaladoc where it can find the API documentation for managed dependencies
autoAPIMappings := true

resolvers += Resolver.jcenterRepo

// SBT Resolver plugin
Revolver.settings

// SBT Native Packager plugin
enablePlugins(JavaAppPackaging)

// borrowed some from: https://www.threatstack.com/blog/useful-scalac-options-for-better-scala-development-part-1
// use of -Xlint: https://github.com/scala/scala/blob/v2.12.0/src/compiler/scala/tools/nsc/settings/Warnings.scala#L30
scalacOptions in Compile ++= List(
  "-deprecation",
  "-encoding", "UTF-8", // Yes, this is two arguments!
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xfuture",
  "-Xlint:nullary-unit",
  "-Xlint:private-shadow",
  "-Xlint:infer-any",
  // Following option is intentionally commented. The akka-http DSL makes uses of the magnet pattern when working
  // with parameters. That is, the automatic argument adaptation is intended by design.
  // See: https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/parameter-directives/parameters.html
  //  "-Xlint:adapted-args", // Make sure we don't have a method with `Unit` type that's trying to return a value
  "-Ywarn-value-discard", // To catch adapted arguments
  "-Ywarn-unused-import",
  "-Ywarn-dead-code"
)
scalacOptions in doc := Seq("-groups", "-implicits")
scalacOptions in Test -= "-Ywarn-value-discard"

// Scoverage
coverageMinimum := 0
coverageFailOnMinimum := true
coverageHighlighting := true
coverageEnabled in Test := true

// Scalariform
scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Force)
  .setPreference(NewlineAtEndOfFile, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 10)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DoubleIndentMethodDeclaration, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(SpacesAroundMultiImports, false)

// Tweaks
fork in run := true
parallelExecution in Test := false
logBuffered in Test := false
// Reduce verbosity during IVY resolution
ivyLoggingLevel in ThisBuild := UpdateLogging.Quiet

assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

artifact in(Compile, assembly) := {
  val art = (artifact in(Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}

addArtifact(artifact in(Compile, assembly), assembly)
