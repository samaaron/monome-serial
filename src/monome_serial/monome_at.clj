(ns monome-serial.monome-at
  (:require [monome-serial.port-handler    :as port-handler]
            [monome-serial.series-protocol :as protocol])
  (:use monome-serial.monome)
  (:use monome-serial.at-at))

(defn led-on-at
  ([m time coords] (apply led-on-at m time coords))
  ([m time x y]
     (let [msg (protocol/led-on-mesg x y)]
       (apply-at send-bytes time m msg))))

(defn led-off-at
  ([m time coords] (apply led-off-at m time coords))
  ([m time x y]
     (let [msg (protocol/led-off-mesg x y)]
       (apply-at send-bytes time m msg))))

