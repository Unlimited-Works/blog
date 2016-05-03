
name := "blog"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe repository" at
  "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.26.0",
  "net.liftweb" %% "lift-json" % "3.0-M8"
)

enablePlugins(PlayScala)

unmanagedBase :=  baseDirectory.value / "mylib"
