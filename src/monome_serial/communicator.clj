(ns monome-serial.communicator
  (:import
     (gnu.io CommPortIdentifier
             SerialPort
             SerialPortEventListener
             SerialPortEvent)
     (java.io OutputStream
              InputStream)))

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

;;TODO fixme
(defn close-port
  "Closes an open port."
  [port]
  (let [raw-port (:raw-port port)]
    (.removeEventListener raw-port)
    (.close raw-port)))

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
  (.write ^OutputStream (:out-stream port) ^bytes bytes))

(defn- event-handler [^InputStream in-stream ^SerialPortEvent event handlers]
  (when (= SerialPortEvent/DATA_AVAILABLE (.getEventType event))
    (while (pos? (.available in-stream))
        (let [op     (.read in-stream)
              xy     (.read in-stream)
              action (cond
                      (= 0 op)  :press
                      (= 16 op) :release
                      :else     :unknown)
              x      (bit-shift-right xy 4)
              y      (bit-shift-right ^Integer (.byteValue ^Integer (bit-shift-left xy 4)) 4)]
          (doseq [[_ handler] @handlers]
            (handler action x y))))))

(defn stop-listening [port]
  (.removeEventListener (:raw-port port)))

(defn listen [port handlers]
  (let [raw-port  (:raw-port port)
        in-stream (:in-stream port)
        listener  (reify SerialPortEventListener
                    (serialEvent [_ event] (event-handler in-stream event handlers)))]
    (.addEventListener raw-port listener)
    (.notifyOnDataAvailable raw-port true)))
