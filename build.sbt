import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.12",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "try-scalikejdbc-bq",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.postgresql"  %  "postgresql"         % "42.1.+",
      "ch.qos.logback"  %  "logback-classic"    % "1.2.+",
      "com.mayreh" %% "scalikejdbc-bigquery" % "0.0.7",
      "com.google.cloud" % "google-cloud-bigquery" % "0.30.0-beta",
      "org.scalikejdbc" %% "scalikejdbc"        % "3.2.+"
    )
  )
