(ns monome-serial.communicator
  (:refer-clojure :exclude [test])
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

(defn listen [m]
  (let [listener (proxy [SerialPortEventListener] []
                   (serialEvent [event] (input-handler m event)))
        port (get-in m [:port :port])]
    (.addEventListener port listener)
    (.notifyOnDataAvailable port true)))

(defn send-long [port id x y]
  (.reset (port :bytes))
  (.writeByte (port :data) id)
  (.writeByte (port :data) (bit-or (bit-shift-left x 4) y))
  (.write (port :out) (.toByteArray (port :bytes))))

(defn send-short [m b]
  (.write (:out m) (byte-array 1 [(byte b)])))

