(ns monome-serial.core
  (:use [overtone.device.grid])
  (:require [serial-port :as serial]
            [monome-serial.animations :as animations]
            [monome-serial.series-protocol :as protocol])
  (:import (java.util.concurrent LinkedBlockingQueue)))

(defrecord Monome [send close handlers open? queue thread])

(defn connect
  "Connect to a monome with a given port identifier.

  Optional takes a protocol id:
  Supported:
    * :070903 (default)
    * :110626
"
  ([port-name & [protocol-id]]
     (case (or protocol-id :070903)
       :110626 (protocol/load-110626!)
       :070903 (protocol/load-070903!)
       (throw (Exception. "Unknown protocol!")))

     (let [port     (serial/open port-name protocol/port-refresh-rate)
           send-fn  (fn [bytes] (serial/write port bytes))
           close    (fn []      (serial/close port))
           handlers (ref {})
           open?    (ref true)
           queue    (LinkedBlockingQueue.)
           worker   (Thread. #(loop [bytes (.take queue)]
                                (apply send-fn [bytes])
                                (recur (.take queue))))
           monome   (Monome. send-fn close handlers open? queue worker)
           parse-bytes (fn [& [bytes]]
                         (let [[action x y] (apply protocol/bytes->key-press bytes)
                               grouped-handlers @handlers
                               all-handlers (flatten (for [[_ group] grouped-handlers] (for [[_ handler] group] handler)))]

                           (doseq [handler all-handlers]
                             (handler action x y))))]

       (serial/on-n-bytes port protocol/key-press-bytes parse-bytes)
       (.start worker)
       (animations/intromation monome)

       monome)))

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
