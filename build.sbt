name := """hope-web"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.8.10.1"
libraryDependencies += "org.apache.httpcomponents" % "fluent-hc" % "4.3.3"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.4"
libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.9.Final" // replace by your jpa implementation
)
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.13"
libraryDependencies += "org.apache.poi" % "poi" %  "3.13"
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.10"
libraryDependencies += "commons-io" % "commons-io" % "2.4"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

//libraryDependencies += "org.glassfish.main.common" % "common-util" % "3.1.2"


// libraryDependencies += "com.google.guava" % "guava" % "18.0"

PlayKeys.externalizeResources := false //这句要不要加的？