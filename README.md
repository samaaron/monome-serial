


                    MMMMMMMMMMMMMMMMMMMMMMMMMM
                    MM                      MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM                      MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM  ooo  ooo  ooo  ooo  MM
    01100 101 11 1 0 1                      MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM                      MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM  ooo  ooo  ooo  ooo  MM
                    MM                      MM
                    MMMMMMMMMMMMMMMMMMMMMMMMMM

Clojure Monome Serial
=====================

Communicate with a monome via the serial port. Receive button events, illuminate lights, build the grid interface of your dreams.

## Dependencies

* [Clojure 1.2 or 1.3](http://clojure.org)
* [serial-port](http://github.com/samaaron/serial-port)

## Installation

Clone into a dir on your machine:

    git clone git://github.com/improcess/monome-serial.git

Next you need to pull in the dependencies. The easiest way is to use [cake](http://clojure-cake.org/) as it handles the native dependencies within `serial-port` effortlessly:

    cake deps

## Use

clj-monome-serial provides a very low-level monome API. If you're after something more featureful you're advised to check out [Polynome](https://github.com/improcess/polynome)

## Contributors

* Sam Aaron
* Jeff Rose
