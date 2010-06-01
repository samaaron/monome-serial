(ns monome-serial.monome
  (:require [monome-serial.port-handler    :as port-handler]
            [monome-serial.series-protocol :as protocol]))

(defrecord Monome [send close handlers open?])

(defn connected?
  "Determines whether a given monome is connected"
  [m]
  @(:open? m))

;;TODO fixme
(defn disconnect
  "Close the monome down. Currently crashes the JVM due to a bug in the port-handler code"
  [monome]
  (dosync
   (apply (:close monome) [])
   (ref-set (:open? monome) false)))

(defn- send-bytes [monome bytes]
  (apply (:send monome) [bytes]))

(defn led-on
  "Turn a specific monome led on for given set of x y coords"
  [m x y]
  (send-bytes m (protocol/led-on-mesg x y)))

(defn led-off
  "Turn a specific monome led off for given set of x y coords"
  [m x y]
  (send-bytes m (protocol/led-off-mesg x y)))

(defn clear
  "Turn all the monome leds off"
  [m]
  (send-bytes m protocol/clear-mesg))

(defn all
  "Turn all the monome leds on"
  [m]
  (send-bytes m protocol/all-mesg))

(defn test-mode-all
  "Enter monome test mode (does not lose led state from normal mode) and turn all leds on.
   Use normal-mode to restore led state"
  [m]
  (send-bytes m protocol/test-mode-all-mesg))

(defn test-mode-clear
  "Enter monome test mode (does not lose led state from normal mode) and turn all leds off
   Use normal-mode to restore led state"
  [m]
  (send-bytes m protocol/test-mode-clear-mesg))

(defn normal-mode
  "Exit monome test mode and restore led state as it was before test mode was entered"
  [m]
  (send-bytes m protocol/normal-mode-mesg))

(defn row
  "Send an integer representing the pattern for an entire row of 8 leds. If the row is 16 leds, then you can pass an extra pattern to this function to span them both"
  ([m idx pattern]
     (send-bytes m protocol/row-mesg idx pattern))
  ([m idx pattern pattern2]
     (send-bytes m protocol/row-mesg idx pattern pattern2)))

(defn col
  "Send an integer representing the pattern for an entire column of 8 leds. If the column is 16 leds, then you can pass an extra pattern to this function to span them both"
  ([m idx pattern]
     (send-bytes m protocol/col-mesg idx pattern))
  ([m idx pattern pattern2]
     (send-bytes m protocol/col-mesg idx pattern pattern2)))

(defn frame
  "Send a complete frame (8x8) to the monome. Frame is a sequence of 8 integers representing bit arrays for each row of the monome. You may specify an index if your monome consists of more than one e.g. 0-3 for a 256 monome."
  ([m frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (send-bytes m (protocol/frame-mesg 0 row1 row2 row3 row4 row5 row6 row7 row8))))
  ([m idx frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (send-bytes m (protocol/frame-mesg idx row1 row2 row3 row4 row5 row6 row7 row8)))))

(defn brightness
  "Set the brightness for all the leds on the monome. Specify an integer intensity in the range 0-15"
  [m intensity]
  (send-bytes m (protocol/intensity-mesg intensity)))

(defn on-action
  "Add an event handler function f to monome m.
  The function takes 3 args: action x y
  Where action is either :press or :release and x and y are the coords of the button that generated the event"
  ([m f] (on-action m f f))
  ([m f name]
     (dosync (alter (:handlers m) assoc name f))))

(defn on-press
  "Add an event handler function f to monome m that only handles key press events
  The function takes 2 args: x y
  Where x and y are the coords of the button that was pressed"
  ([m f] (on-press m f f))
  ([m f name]
     (on-action m (fn [op x y] (if (= op :press) (apply f [x y]))) name)))

(defn on-release
  "Add an event handler function f to monome m that only handles key release events
  The function takes 2 args: x y
  Where x and y are the coords of the button that was released"
  ([m f] (on-release m f f))
  ([m f name]
     (on-action m (fn [op x y] (if (= op :release) (apply f [x y]))) name)))

(defn remove-handler
  "Remove the given handler function with name from monome m."
  [m name]
  (dosync (alter (:handlers m) dissoc name)))

(defn intromation
  [m]
  (clear m)
  (brightness m 0)
  (all m)
  (let [sleep-times [ 0, 0.072, 0.1030, 0.1159, 0.1236, 0.1287, 0.1324, 0.1352, 0.1373, 0.1390, 0.1404, 0.1416, 0.1426, 0.1435, 0.1442, 0.1448, 0.1278 ]]

    (dotimes [i 16] (Thread/sleep (* 250 (nth sleep-times i))) (brightness m i))
    (dotimes [i 16] (Thread/sleep (* 500 (nth sleep-times i))) (brightness m (- 15 i))))
  (clear m)
  (brightness m 15))


(defn connect
  "Connect to a monome with a given port identifier"
  [port-name]
  (let [port     (port-handler/open-port port-name)
        send     (fn [bytes] (port-handler/write port bytes))
        close    (fn []      (port-handler/close-port port))
        handlers (ref {})
        open?    (ref true)
        _        (port-handler/listen port handlers)
        monome   (Monome. send close handlers open?)]
    (intromation monome)
    monome))

