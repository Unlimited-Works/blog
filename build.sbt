
name := "blog"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.26.2",
  "net.liftweb" %% "lift-json" % "3.0-M8",
  "com.googlecode.xmemcached" % "xmemcached" % "2.0.1",
//  "net.debasishg" %% "redisclient" % "3.1",
  "com.livestream" %% "scredis" % "2.0.6"

)

enablePlugins(PlayScala)

unmanagedBase :=  baseDirectory.value / "mylib"
