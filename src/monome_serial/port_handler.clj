(ns monome-serial.port-handler
    (:import
     (java.io DataInputStream
              DataOutputStream
              BufferedInputStream
              BufferedOutputStream
              ByteArrayOutputStream
              ByteArrayInputStream)

     (gnu.io CommPortIdentifier
             CommPort
             SerialPort
             SerialPortEventListener
             SerialPortEvent)))

(def PORT-OPEN-TIMEOUT 2000)

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
  ([ports i]
   (when ports
     (println i ":" (.getName (first ports)))
     (recur (next ports) (inc i)))))

(defn close
  "Closes an open port."
  [port]

  (.close (:data port)
  (.close (:port port))))

(defn open
  "Returns an opened serial port.

  (port \"/dev/ttyUSB0\")"
  [path]
  (let [port-id (first (filter #(= path (.getName %)) (port-ids)))
        port  (.open port-id "monome" PORT-OPEN-TIMEOUT)
        out   (.getOutputStream port)
        in    (.getInputStream port)
        bytes (ByteArrayOutputStream.)
        data  (DataOutputStream. bytes)
        _  (.setSerialPortParams port 115200
                                 SerialPort/DATABITS_8
                                 SerialPort/STOPBITS_1
                                 SerialPort/PARITY_NONE)
        opened-port (with-meta {:port port
                                :in in
                                :out out
                                :bytes bytes
                                :data data}
                      {:type ::open-port})]
    opened-port))



