val jacksonScalaModuleVersion = "2.8.6"
val scalatestVersion = "3.0.1"
val sparkeyVersion = "2.1.3"

val commonSettings = Seq(
  organization       := "me.andrew",
  scalaVersion       := "2.12.1",
  crossScalaVersions := Seq("2.11.8", "2.12.2"),
  scalacOptions                   ++= Seq("-target:jvm-1.8", "-deprecation", "-feature", "-unchecked"),
  javacOptions                    ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked"),
  javacOptions in (Compile, doc)  := Seq("-source", "1.8")
)

lazy val sparkeyScala: Project = Project(
  "sparkey-scala",
  file(".")
).settings(
  commonSettings,
  description := "Type-safe Scala wrapper on top of Sparkey Java API",
  libraryDependencies ++= Seq(
    "com.spotify.sparkey" % "sparkey" % sparkeyVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % "test"
  )
)
    