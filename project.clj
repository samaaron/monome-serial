(defproject monome-serial "0.1"
  :description "An interface to the monome (http://monome.org)"
  :repositories [["java.net" "http://download.java.net/maven/2/"]]
  :dependencies [[org.clojure/clojure "1.1.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.1.0-master-SNAPSHOT"]
                 [byte-spec "0.1"]]
  :dev-dependencies [[native-deps "1.0.0"]
                     [lein-clojars "0.5.0-SNAPSHOT"]
                     [autodoc "0.7.0"]
                     [leiningen/lein-swank "1.1.0"]
                     [org.clojars.nakkaya/rxtx-macosx-native-deps "2.1.7"]])
