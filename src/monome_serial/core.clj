(ns monome-serial.core
  (:use [overtone.device.grid])
  (:require [serial-port :as port]
            [monome-serial.animations :as animations]
            [overtone.libs.handlers :as handlers]
            [monome-serial.led :as led]
            [monome-serial.series-protocol :as protocol])
  (:import (java.util.concurrent LinkedBlockingQueue)))

(defrecord Monome [send close handler-pool open? queue thread]
  Grid
  (on-action
    [this key f]
    (handlers/add-handler (:handler-pool this) "*" key (fn [em]
                                                         (f (:action em)
                                                            (:x em)
                                                            (:y em)))))
  (remove-action-handler
    [this key]
    (handlers/remove-handler (:handler-pool this) "*" key))
  (action-handlers
    [this]
    (let [p (:handler-pool this)
          syncs (get (:syncs p) "*")
          asyncs (get (:asyncs p) "*")]
      (into #{} (concat (keys syncs) (keys asyncs)))) )
  (led-set [this x y colour]
    (if (= 0 colour)
      (led/led-on this x y)
      (led/led-off this x y)))
  (led-set-all [this colour]
    (if (= 0 colour)
      (led/clear this)
      (led/all this)))
  (led-frame [this leds]
    ;;TODO implement me
    ;;deal with different monome sizes
    nil))

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

     (let [port     (port/open port-name)
           send-fn  (fn [bytes] (port/write port bytes))
           close    (fn []      (port/close port))
           handlers (handlers/mk-handler-pool (str "Monome Handlers - " port-name))
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
                               y      (bit-and 15 xy-byte)]
                           (handlers/event handlers "*" :action action :x x :y y)))]

       (port/on-n-bytes port 2 parse-bytes)
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
