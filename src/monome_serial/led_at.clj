(ns monome-serial.led-at
  (:use [monome-serial.comms    :only [send-bytes]]
        [monome-serial.led-util :only [bin-list->int]]
        monome-serial.at-at)

  (:require [monome-serial.series-protocol :as protocol]))

(defn led-on-at
  ([m time coords] (apply led-on-at m time coords))
  ([m time x y]
     (let [msg (protocol/led-on-mesg x y)]
       (apply-at send-bytes time m msg))))

(defn led-off-at
  ([m time coords] (apply led-off-at m time coords))
  ([m time x y]
     (let [msg (protocol/led-off-mesg x y)]
       (apply-at send-bytes time m msg))))

(defn row-at
  ([m time idx col0 col1 col2 col3 col4 col5 col6 col7]
     (apply-at send-bytes time m (protocol/row-mesg idx (bin-list->int col0 col1 col2 col3 col4 col5 col6 col7))))
  ([m time idx col0 col1 col2 col3 col4 col5 col6 col7 col8 col9 col10 col11 col12 col13 col14 col15]
     (apply-at send-bytes time m (protocol/row-mesg idx
                                                    (bin-list->int col0 col1 col2 col3 col4 col5 col6 col7)
                                                    (bin-list->int col8 col9 col10 col11 col12 col13 col14 col15))))
  ([m time idx pattern]
     (apply-at send-bytes time m (protocol/row-mesg idx pattern)))
  ([m time idx pattern pattern2]
     (apply-at send-bytes time m (protocol/row-mesg idx pattern pattern2))))

(defn col-at
  ([m time idx row0 row1 row2 row3 row4 row5 row6 row7]
     (apply-at send-bytes time m (protocol/col-mesg idx (bin-list->int row0 row1 row2 row3 row4 row5 row6 row7))))
  ([m time idx row0 row1 row2 row3 row4 row5 row6 row7 row8 row9 row10 row11 row12 row13 row14 row15]
     (apply-at send-bytes time m (protocol/col-mesg idx
                                      (bin-list->int row0 row1 row2 row3 row4 row5 row6 row7)
                                      (bin-list->int row8 row9 row10 row11 row12 row13 row14 row15))))
  ([m time idx pattern]
     (apply-at send-bytes time m (protocol/col-mesg idx pattern)))
  ([m time idx pattern pattern2]
     (apply-at send-bytes time m (protocol/col-mesg idx pattern pattern2))))

(defn frame-at
  "Send a complete frame (8x8) to the monome. Frame is a sequence of 8 integers representing bit arrays for each row of the monome. You may specify an index if your monome consists of more than one e.g. 0-3 for a 256 monome."
  ([m time row0 row1 row2 row3 row4 row5 row6 row7]
     (apply-at send-bytes time m (protocol/frame-mesg 0
                                        (apply bin-list->int row0)
                                        (apply bin-list->int row1)
                                        (apply bin-list->int row2)
                                        (apply bin-list->int row3)
                                        (apply bin-list->int row4)
                                        (apply bin-list->int row5)
                                        (apply bin-list->int row6)
                                        (apply bin-list->int row7))))
  ([m time idx row0 row1 row2 row3 row4 row5 row6 row7]
     (apply-at send-bytes time m (protocol/frame-mesg idx
                                        (apply bin-list->int row0)
                                        (apply bin-list->int row1)
                                        (apply bin-list->int row2)
                                        (apply bin-list->int row3)
                                        (apply bin-list->int row4)
                                        (apply bin-list->int row5)
                                        (apply bin-list->int row6))))
  ([m time frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (apply-at send-bytes time m (protocol/frame-mesg 0 row1 row2 row3 row4 row5 row6 row7 row8))))
  ([m time idx frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (apply-at send-bytes time m (protocol/frame-mesg idx row1 row2 row3 row4 row5 row6 row7 row8)))))

(defn clear-at
  [m time]
  (apply-at send-bytes time m protocol/clear-mesg))

(defn all-at
  [m time]
   (apply-at send-bytes time m protocol/all-mesg))
