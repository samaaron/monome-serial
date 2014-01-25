


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

Communicate with a Monome via the serial port. Receive button events, illuminate lights, build the grid interface of your dreams.

## Dependencies

* [Clojure 1.2 or 1.3](http://clojure.org)
* [serial-port](http://github.com/samaaron/serial-port)

## Installation

Clone into a dir on your machine:

    git clone git://github.com/improcess/monome-serial.git

Pull in dependencies with lein:

    lein deps

## Use

clj-monome-serial provides a very low-level Monome API. If you're after something more featureful you're advised to check out [Polynome](https://github.com/samaaron/polynome)

### Protocol versions

Monome's have progressed through a few protocols (http://monome.org/docs/tech:serial).

Currently supported:

* protocol-070903 (default)
* protocol-110626 (new ~2013 Monomes)

The best way to know which protocol to use is to test it out in a repl:

`lein repl`

```clojure
(use 'monome-serial.core)

;;Try and connect with default protocol
(def m (connect "/dev/tty.ID-OF-YOUR-DEVICE"))

;;Did you see any lights turn?
;;If not lets try another protocol

(disconnect m)

;;Unplug and plugin back in Monome

;;Try 110626
(def m (connect "/dev/tty.ID-OF-YOUR-DEVICE" :110626))
```

## Contributors

* Sam Aaron
* Jeff Rose
* [Joseph Wilk](http://blog.josephwilk.net)
