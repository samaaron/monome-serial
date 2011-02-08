Clojure Monome Serial
=====================

Communicate with a monome via the serial port. Receive button events, illuminate lights, build the grid interface of your dreams.

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

clj-monome-serial has a number of dependencies that need to be on your system for it to work. Install them with leiningen is trivial:

    lein deps


Use
===

clj-monome-serial provides a very low-level monome API. If you're after something more featureful you're advised to check out [Polynome](https://github.com/improcess/polynome)

Contributors
------------

* Sam Aaron
* Jeff Rose
