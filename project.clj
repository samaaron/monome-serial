(defproject monome-serial "0.1"
  :description "An interface to the monome (http://monome.org)"
  :repositories [["java.net" "http://download.java.net/maven/2/"]]
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
                 [byte-spec "0.1"]]
  :native-dependencies [[org.clojars.samaaron/rxtx-macosx-native-deps "2.2"]]
  :dev-dependencies [[native-deps "1.0.0"]
                     [lein-clojars "0.5.0-SNAPSHOT"]
                     [swank-clojure "1.2.1"]])

