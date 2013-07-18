ScalaTest-DBReporter
====================

ScalaTest database reporter is a custom reporter plugin for ScalaTest.

By default it'll collect test suites information into database. It's not hard to implement other features if you needed.

DBReporter connect DB using Slick and using H2 as default DB.

The code aims to provide an example on how to implement a custom reporter in ScalaTest.

How to use
-------

run ScalaTest using *-C x.y.z.DBReporter* option.

Table Structure
--------
    INSERT INTO "TESTSUITES" ("SUITENAME",
      "CLASSNAME",
      "TIMESTAMP",
      "SUCCESSES",
      "FAILURES",
      "IGNORES",
      "PENDINGS",
      "DURATION")
    VALUES (?,?,?,?,?,?,?,?)

And a h2 db file will be created at the current directory.

License
-------

Copyright Zane.XY.W

Licensed under the [Apache License, Version 2.0][apache2] (the "License"); you
may not use this software except in compliance with the License. You may obtain
a copy of the License at:

[http://www.apache.org/licenses/LICENSE-2.0][apache2]

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

[giter8]: https://github.com/n8han/giter8
[Scala]: http://www.scala-lang.org/
[sbt]: http://github.com/harrah/xsbt/
[apache2]: http://www.apache.org/licenses/LICENSE-2.0