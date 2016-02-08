import sbt.Keys.{artifactPath, libraryDependencies, mainClass, managedClasspath, name, organization, packageBin, resolvers, version}


lazy val projectName = "base"
lazy val projectVersion = "0.0.1-SNAPSHOT"
name := projectName

lazy val dhRegistry = "dh.livetex.ru"
lazy val dhNamespace = "service"
lazy val nexusUrl = "http://sonatype-nexus.livetex.ru"
lazy val finagleVersion = "6.28.0"
lazy val scroogeVersion = "3.20.0"

lazy val commonSettings = Seq(
  resolvers ++= Seq(
    "Sonatype Livetex" at nexusUrl + "/nexus/content/groups/public",
    "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases/"
  ),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  scalaVersion := "2.11.7",
  organization := "ru.livetex"
)

lazy val root = (project in file(".")).aggregate(interface, service)

lazy val service =
  project.in(file("service")).
    configs(IntegrationTest).
    enablePlugins(BuildInfoPlugin).
    settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "ru.livetex.base.service"
    ).
    settings(commonSettings: _*).
    settings(Defaults.itSettings: _*).
    settings(
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
      version := projectVersion,
      name := s"$projectName-service",
      libraryDependencies ++= Seq(
        "com.twitter" %% "finagle-core" % finagleVersion,
        "com.twitter" %% "finagle-http" % finagleVersion,
        "com.typesafe" % "config" % "1.3.0",
        "com.codacy" %% "scala-consul" % "1.1.0-SNAPSHOT",
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "ru.livetex" %% s"$projectName-interface" % s"$projectVersion-SNAPSHOT" withSources(),
        "ru.livetex" %% "scala-utils-finagle" % "0.0.1-SNAPSHOT"

  )
    ).
    enablePlugins(DockerPlugin).
    settings(docker <<= docker.dependsOn(Keys.`package`.in(Compile, packageBin))).
    settings(
      imageNames in docker := Seq(
        ImageName(s"$dhRegistry/$dhNamespace/${name.value.toLowerCase}:latest"),
        ImageName(
          registry = Some(dhRegistry),
          repository = name.value.toLowerCase,
          namespace = Some("service"),
          tag = Some(version.value)
        )
      )).
    settings(
      dockerfile in docker := {
        val jarFile = artifactPath.in(Compile, packageBin).value
        val classpath = managedClasspath.in(Compile).value
        val depClasspath = dependencyClasspath.in(Runtime).value
        val mainclass = mainClass.in(Compile, packageBin).value.get
        val app = "/app"
        val etc = s"$app/etc"
        val data = s"$app/data"
        val log = s"$app/log"
        val libs = s"$app/libs"
        val jarTarget = s"$app/${name.value}.jar"
        val classpathString = s"$libs/*:$jarTarget"
        new Dockerfile {
          from("dh.livetex.ru/lang/java:1.8")
          run("mkdir", app, etc, data, log)
          workDir(app)
          classpath.files.foreach { depFile =>
            val target = file(libs) / depFile.name
            stageFile(depFile, target)
          }
          depClasspath.files.foreach { depFile =>
            val target = file(libs) / depFile.name
            stageFile(depFile, target)
          }
          addRaw(libs, libs)
          add(jarFile, jarTarget)
          cmd("java", "-cp", classpathString, mainclass)
        }
      }
    )


lazy val interface = project.in(file("interface")).
  settings(commonSettings: _*).
  settings(
    version := s"$projectVersion-SNAPSHOT",
    name := s"$projectName-interface",
    libraryDependencies ++= Seq(
      "com.twitter" %% "scrooge-core" % scroogeVersion,
      "com.twitter" %% "scrooge-linter" % scroogeVersion,
      "com.twitter" %% "finagle-thrift" % finagleVersion,
      "org.apache.thrift" % "libthrift" % "0.8.0")
  ).
  settings(
    com.twitter.scrooge.ScroogeSBT.newSettings,
    publishMavenStyle := true,
    exportJars := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishTo := {
      if (isSnapshot.value) {
        Some("snapshots" at nexusUrl + "/nexus/content/repositories/snapshots/")
      } else {
        Some("releases" at nexusUrl + "/nexus/content/repositories/releases/")
      }
    },
    pomExtra := <url>https://github.com/splusminusx/Base-Service</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/bsd-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:LiveTex/splusminusx/Base-Service.git</url>
        <connection>scm:git:LiveTex/splusminusx/Base-Service.git</connection>
      </scm>
      <developers>
        <developer>
          <id>splusminusx</id>
          <name>Roman Malygin</name>
        </developer>
      </developers>)