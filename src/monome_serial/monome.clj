(ns monome-serial.monome
  (:require [monome-serial.communicator :as communicator]
            [monome-serial.protocol     :as protocol]))

(defrecord Monome [port handlers])
(defrecord Frame  [col1 col2 col3 col4 col5 col6 col7 col8])

(defn connect [port-name]
  "Connect to a monome with a given port identifier"
  (let [port   (communicator/open-port port-name)
        monome (Monome. port (ref {}))]
    (communicator/listen monome)
    monome))

(defn disconnect [monome]
  "Close the monome down"
  (communicator/close-port (:port monome)))

(defn- send-bytes [monome bytes]
  (communicator/write (:port monome) bytes))

(defn led-on [m x y]
  (send-bytes m (protocol/led-on-mesg x y)))

(defn led-off [m x y]
  (send-bytes m (protocol/led-off-mesg x y)))

(defn clear [m]
  (send-bytes m protocol/clear-mesg))

(defn all [m]
  (send-bytes m protocol/all-mesg))

(defn test-mode-all [m]
  (send-bytes m protocol/test-mode-all-mesg))

(defn test-mode-clear [m]
  (send-bytes m protocol/test-mode-clear-mesg))

(defn normal-mode [m]
  (send-bytes m protocol/normal-mode-mesg))

(defn frame [m frame]
  (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
      (send-bytes m (protocol/frame-mesg row1 row2 row3 row4 row5 row6 row7 row8))))

(defn brightness [m intensity]
  (send-bytes m (protocol/intensity-mesg intensity)))

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

(defn on [f x y]
  (update-in f [x] bit-set y))

(defn off [f x y]
  (update-in f [x] bit-clear y))

(def s [
        "00000000"
        "11100010"
        "10000110"
        "10001010"
        "11101111"
        "10100010"
        "11100010"
        "00000000"
        ])

(defn from-string-base2
  [s]
  (loop [str (reverse s)
         count 0
         idx   0]
    (let [fst (first str)
          rst (rest  str)
          val (if (= fst \1)
                (int (Math/pow 2 idx))
                0)]

      (if-not fst
        count
        (recur rst (+ val count) (inc idx))))))

(defn frame-from-string-seq
  [ss]
  (map #(-> % reverse from-string-base2) ss))







