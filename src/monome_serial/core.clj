(ns monome-serial.core
  (:require [monome-serial.port-handler :as port-handler]
            [monome-serial.animations   :as animations])
  (:import (java.util.concurrent LinkedBlockingQueue)))

(defrecord Monome [send close handlers open? queue thread])

(defn connect
  "Connect to a monome with a given port identifier"
  [port-name]
  (let [port     (port-handler/open-port port-name)
        send-fn  (fn [bytes] (port-handler/write port bytes))
        close    (fn []      (port-handler/close-port port))
        handlers (ref {})
        open?    (ref true)
        queue    (LinkedBlockingQueue.)
        worker   (Thread. #(loop [bytes (.take queue)]
                             (apply send-fn [bytes])
                             (recur (.take queue))))
        _        (port-handler/listen port handlers)
        monome   (Monome. send-fn close handlers open? queue worker)]
    (.start worker)
    (animations/intromation monome)

    monome))

(defn connected?
  "Determines whether a given monome is connected"
  [m]
  @(:open? m))

;;TODO fixme
(defn disconnect
  "Close the monome down. Currently crashes the JVM due to a bug in the port-handler code"
  [monome]
  (dosync
   ((:close monome))
   (ref-set (:open? monome) false)))
