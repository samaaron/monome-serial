(ns example
  (:require [monome-serial.connector :as monome]))

(def m (monome/open (monome/port-at 0)))

