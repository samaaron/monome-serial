(ns monome-serial.connector
  (:import 
     (java.io DataInputStream DataOutputStream
              BufferedInputStream BufferedOutputStream
              ByteArrayOutputStream ByteArrayInputStream)
     (gnu.io CommPortIdentifier CommPort SerialPort))
  (:use byte-spec))

(def PORT-OPEN-TIMEOUT 2000)

(defn ports [] (enumeration-seq (CommPortIdentifier/getPortIdentifiers)))

(defn list-ports []
  (doseq [port (ports)] 
    (println "PORT: " (.getName port))))

(defn get-port [pname]
  (let [port-id (first (filter #(= pname (.getName %)) (ports)))
        port (.open port-id "monome" PORT-OPEN-TIMEOUT)]
    port))

(defn monome []
  (let [port (get-port "/dev/ttyUSB0")
        os (.getOutputStream port)
        is (.getInputStream port)
        bs (ByteArrayOutputStream.)
        ds (DataOutputStream. bs)]
    (.setSerialPortParams port 115200 SerialPort/DATABITS_8 SerialPort/STOPBITS_1 SerialPort/PARITY_NONE)
    (with-meta {:port port
                :in is
                :out os
                :bs bs
                :ds ds}
               {:type ::monome})))

(defn monome-close [m]
  (.close (:ds m))
  (.close (:port m)))

(defn monome-listener [m f]
  (let [port (:port m)
        listener (proxy [SerialPortEventListener] []
                   (serialEvent [event] (f event)))]
  (.addEventListener (:port m) listener)
  (.notifyOnDataAvailable port true)))

(defn led-on [m x y]
  (.reset (:bs m))
  (.writeByte (:ds m) (bit-shift-left 2 4))
  (.writeByte (:ds m) (bit-or (bit-shift-left x 4) y))
  (.write (:out m) (.toByteArray (:bs m))))

(defn led-off [m x y]
  (.reset (:bs m))
  (.writeByte (:ds m) (bit-shift-left 4 4))
  (.writeByte (:ds m) (bit-or (bit-shift-left x 4) y))
  (.write (:out m) (.toByteArray (:bs m))))

