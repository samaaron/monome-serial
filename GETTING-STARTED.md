Getting Started
===============

Java 6 JDK
----------

To get started you just need the Java 6 JDK. If you don't have it [download and install the appropriate version for your system](http://java.sun.com/javase/downloads/widget/jdk6.jsp)


Leiningen
---------

Next up you need [Leiningen](http://github.com/technomancy/leiningen) installed. This can be achieved with the following three super simple steps:

1. Download the script from [here](http://github.com/technomancy/leiningen/raw/stable/bin/lein)
2. Place it on your path and chmod it to be executable. (chmod u+x /path/to/lein)
3. Run: lein self-install


Install dependencies
--------------------

Polynome has a number of dependencies that need to be on your system for it to work. Install them with leiningen is trivial:

    lein deps


Install dxtx Libraries
----------------------

Due to the ridiculous fact that the JVM doesn't ship with a serial library, you'll need to install the dxtx library for your system.

**OS X 10.6 (Snow Leopard)**

First up, you need the latest Developer tools installed (http://developer.apple.com/tools/). If you're unsure whether you have this installed, open a terminal and type:

    gcc --version

You should see something this if you already have the Developer tools:

    i686-apple-darwin10-gcc-4.2.1 (GCC) 4.2.1 (Apple Inc. build 5646) (dot 1)
    Copyright (C) 2007 Free Software Foundation, Inc.
    This is free software; see the source for copying conditions.  There is NO
    warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE

Next, unzip and compile the library:

    cd extras/rxtx/osx/
    unzip rxtx-2-1.1-7r2.zip
    cd rxtx-2.1-7r2
    ./configure
    make

Copy the libraries to the system Java extension dir:

    cp RXTXcomm.jar /Library/Java/Extensions
    cp i686-apple-darwin10.2.0/librxtxSerial.jnilib /Library/Java/Extensions

Finally run the fix permission shell script and add yourself to the uucp group:

    cd ../ #you should now be in extras/rxtx/osx/
    sudo sh fix_permissions.sh
    sudo dscl . -append /Groups/uucp GroupMembership <your usename>






