(ns monome-serial.comms)

(defn send-bytes [monome bytes]
  (.put (:queue monome) bytes)
  monome)
