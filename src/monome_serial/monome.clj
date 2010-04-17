(ns monome-serial.monome
  (:require [monome-serial.communicator :as communicator]
            [monome-serial.port-handler :as port-handler]))


(defn open [port-name]
  "Connect to a monome with a given port identifier"
  (let [monome (with-meta {:port (port-handler/open port-name)
                           :handlers (ref {})}
                 {:type ::monome})]
    (communicator/listen monome)
    monome))

(defn close [monome]
  "Close the monome down"
  (port-handler/close (:port monome)))

(defn- m-send
  ([monome a]     (communicator/send-short (monome :port) a))
  ([monome a b c] (communicator/send-long  (monome :port) a b c)))

(defn led-on [m x y]
  (m-send m 32 x y))

(defn led-off [m x y]
  (m-send m 48 x y))

(defn clear [m]
  (m-send m 144))

(defn fill [m]
  (m-send m 145))

(defn brightness [m level]
  (m-send m (+ 160 level)))

;(defn test [m]
;  (m-send m 177))

(defn shutdown [m]
  (m-send m 178))

(defn add-handler
  "Add an input handler function f to monome m.
  The function takes 3 args:
  (f op x y)
  Where op is either :up or :down."
  [m f]
  (dosync (alter (:handlers m) assoc f f)))

(defn remove-handler
  "Remove the given handler function f from monome m."
  [m f]
  (dosync (alter (:handlers m) dissoc f)))

