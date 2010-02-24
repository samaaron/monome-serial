(ns monome-serial.connector
  (:refer-clojure :exclude [test])
  (:import 
     (java.io DataInputStream DataOutputStream
              BufferedInputStream BufferedOutputStream
              ByteArrayOutputStream ByteArrayInputStream)
     (gnu.io CommPortIdentifier CommPort SerialPort SerialPortEventListener
             SerialPortEvent))
  (:use byte-spec))

(def PORT-OPEN-TIMEOUT 2000)

(defn ports [] (enumeration-seq (CommPortIdentifier/getPortIdentifiers)))

(defn port 
  "Returns an opened serial port by name.
  
  (port \"/dev/ttyUSB0\")
  "
  [pname]
  (let [port-id (first (filter #(= pname (.getName %)) (ports)))
        port (.open port-id "monome" PORT-OPEN-TIMEOUT)]
    port))

(defn port-at
  "Returns the name of the serial port at index."
  [idx]
  (.getName (nth (ports) idx)))

(defn list-ports 
  "Print out the available ports with an index number for future reference 
  with (port-at <i>)."
  ([] (list-ports (ports) 0))
  ([ports i] 
   (when ports
     (println i ":" (.getName (first ports)))
     (recur (next ports) (inc i)))))

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
              y (bit-shift-right (byte (bit-shift-left xy 4)) 4)]
          (doseq [[_ handler] @(:handlers m)]
            (handler op x y)))))))

(defn- listen [m]
  (let [listener (proxy [SerialPortEventListener] []
                   (serialEvent [event] (input-handler m event)))]
  (.addEventListener (:port m) listener)
  (.notifyOnDataAvailable (:port m) true)))

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

(defn open [& [port-name]]
  (let [port-name (or port-name "/dev/ttyUSB0")
        port (port port-name)
        os (.getOutputStream port)
        is (.getInputStream port)
        bs (ByteArrayOutputStream.)
        ds (DataOutputStream. bs)
        handlers (ref {})
        _  (.setSerialPortParams port 115200 
                                 SerialPort/DATABITS_8 
                                 SerialPort/STOPBITS_1 
                                 SerialPort/PARITY_NONE)
        m (with-meta {:port port
                      :handlers handlers
                      :in is
                      :out os
                      :bs bs
                      :ds ds}
                     {:type ::monome})]
    (listen m)
    m))

(defn close [m]
  (.close (:ds m))
  (.close (:port m)))

(defn send-long [m id x y]
  (.reset (:bs m))
  (.writeByte (:ds m) id)
  (.writeByte (:ds m) (bit-or (bit-shift-left x 4) y))
  (.write (:out m) (.toByteArray (:bs m))))

(defn send-short [m b]
  (.write (:out m) (byte-array 1 [(byte b)])))

(defn led-on [m x y]
  (send-long m 32 x y))

(defn led-off [m x y]
  (send-long m 48 x y))

(defn clear [m]
  (send-short m 144))

(defn fill [m]
  (send-short m 145))
  
(defn brightness [m level]
  (send-short m (+ 160 level)))

(defn test [m]
  (send-short m 177))

(defn shutdown [m]
  (send-short m 178))
