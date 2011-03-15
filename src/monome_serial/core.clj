(ns monome-serial.core
  (:require [serial-port :as port]
            [monome-serial.animations   :as animations])
  (:import (java.util.concurrent LinkedBlockingQueue)))

(defrecord Monome [send close handlers open? queue thread])

(defn connect
  "Connect to a monome with a given port identifier"
  [port-name]
  (let [port     (port/open port-name)
        send-fn  (fn [bytes] (port/write port bytes))
        close    (fn []      (port/close port))
        handlers (ref {})
        open?    (ref true)
        queue    (LinkedBlockingQueue.)
        worker   (Thread. #(loop [bytes (.take queue)]
                             (apply send-fn [bytes])
                             (recur (.take queue))))
        monome   (Monome. send-fn close handlers open? queue worker)
        parse-bytes (fn   [[action-byte xy-byte]]
                      (let [action (cond
                                    (= 0 action-byte)  :press
                                    (= 16 action-byte) :release
                                    :else     :unknown)
                            x      (bit-shift-right xy-byte 4)
                            y      (bit-shift-right ^Integer (.byteValue ^Integer (bit-shift-left xy-byte 4)) 4)
                            grouped-handlers @handlers
                            all-handlers (flatten (for [[_ group] grouped-handlers] (for [[_ handler] group] handler)))]

    (doseq [handler all-handlers]
      (handler action x y))))]

    (port/on-n-bytes port 2 parse-bytes)
    (.start worker)
    (animations/intromation monome)

    monome))

(defn connected?
  "Determines whether a given monome is connected"
  [m]
  @(:open? m))

;;TODO fixme
(defn disconnect
  "Close the monome down."
  [monome]
  (dosync
   ((:close monome))
   (ref-set (:open? monome) false)))
