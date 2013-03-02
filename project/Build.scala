import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "swe-vs-fin"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.scalaj" % "scalaj-time_2.10.0-M7" % "0.6"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
