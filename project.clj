(defproject monome-serial "0.4-SNAPSHOT"
  :description "A library for the monome (http://monome.org). Implements the overtone.device.grid protocol."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.0"]
                 [serial-port "1.1.2"]
                 [overtone/device.grid "0.0.3"]
                 [overtone/libs.handlers "0.0.1"]])
