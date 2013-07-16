
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP21",
    "org.pegdown" % "pegdown" % "1.2.1",
    "com.h2database" % "h2" % "1.3.172",
    "com.typesafe.slick" %% "slick" % "1.0.1"
)
