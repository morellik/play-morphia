PlayMorphia Play 2.5.x Module
=====================================

This is a Play 2.5.x Module for [Morphia](http://mongodb.github.io/morphia/)
(a MongoDB Java driver wrapper).

Installation
-----------

Add the following to your build.sbt:

    libraryDependencies ++= Seq(
        "org.mongodb.morphia" % "morphia" % "1.3.0",
        "org.mongodb" % "mongo-java-driver" % "3.2.2"
        )


Create a `lib` folder in your project directory and copy play-morphia.jar inside.

You will need to specify your MongoDB configuration in the `conf/application.conf`file:

    playmorphia {
        uri="mongodb://127.0.0.1:27017/YourDB"
        database="YourDB"
        host="127.0.0.1"
        port=27017
        models="models"
    }

Contact
-------

If you have a question or need some help you can just [open an issue](https://github.com/morellik/play-morphia/issues). 

License
-------

The license is Apache 2.0, see LICENSE.txt.
