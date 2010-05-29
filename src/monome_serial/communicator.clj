(ns monome-serial.communicator
  (:refer-clojure :exclude [test])
  (:import
     (gnu.io CommPortIdentifier
             SerialPort
             SerialPortEventListener
             SerialPortEvent)))

(def PORT-OPEN-TIMEOUT 2000)

(defrecord Port [raw-port out-stream in-stream])

(defn port-ids
  "Returns a seq representing all port identifiers visible to the system"
  []
  (enumeration-seq (CommPortIdentifier/getPortIdentifiers)))

(defn port-at
  "Returns the name of the serial port at idx."
  [idx]
  (.getName (nth (port-ids) idx)))

(defn list-ports
  "Print out the available ports with an index number for future reference
  with (port-at <i>)."
  ([] (list-ports (port-ids) 0))
  ([ports idx]
   (when ports
     (println idx ":" (.getName (first ports)))
     (recur (next ports) (inc idx)))))

(defn close-port
  "Closes an open port."
  [port]
  (.close (:raw-port port)))

(defn open-port
  "Returns an opened serial port.

  (port \"/dev/ttyUSB0\")"
  [path]
  (let [port-id (first (filter #(= path (.getName %)) (port-ids)))
        raw-port  (.open port-id "monome" PORT-OPEN-TIMEOUT)
        out       (.getOutputStream raw-port)
        in        (.getInputStream  raw-port)
        _         (.setSerialPortParams raw-port 115200
                                        SerialPort/DATABITS_8
                                        SerialPort/STOPBITS_1
                                        SerialPort/PARITY_NONE)]

    (Port. raw-port out in)))

(defn write
  "Write a byte array to a port"
  [port bytes]
  (.write (:out-stream port) bytes))

;;TODO make send action an agent
(defn send-coord [port mesg-id x y]
  (let [coords (.byteValue (bit-or (bit-shift-left x 4) y))
        mesg   (.byteValue mesg-id)
        bytes  (byte-array 3 [mesg coords])]
       (write (:out-stream port) bytes)))

(defn send-frame [port row1 row2 row3 row4 row5 row6 row7 row8]
  (let [mesg  (bit-or (bit-shift-left 8 4) 128)
        bytes (byte-array 9 [(.byteValue mesg)
                             (.byteValue row1)
                             (.byteValue row2)
                             (.byteValue row3)
                             (.byteValue row4)
                             (.byteValue row5)
                             (.byteValue row6)
                             (.byteValue row7)
                             (.byteValue row8)])]
    (write port bytes)))

(defn- input-handler [m event]
  (when (= SerialPortEvent/DATA_AVAILABLE (.getEventType event))
    (let [port (.getInputStream (.getSource event))]
      (while (pos? (.available port))
        (let [op  (.read port)
              xy  (.read port)
              op (cond
                   (= 0 op)  :down
                   (= 16 op) :up
                   :else     :unknown)
              x (bit-shift-right xy 4)
              y (bit-shift-right (.byteValue (bit-shift-left xy 4)) 4)]
          (doseq [[_ handler] @(:handlers m)]
            (handler op x y)))))))

(defn listen [monome]
  (let [listener (proxy [SerialPortEventListener] []
                   (serialEvent [event] (input-handler monome event)))
        port     (get-in monome [:port :raw-port])]
    (.addEventListener port listener)
    (.notifyOnDataAvailable port true)))
