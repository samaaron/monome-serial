(ns monome-serial.led
  (:use [monome-serial.comms    :only (send-bytes)]
        [monome-serial.led-util :only (bin-list->int)])
  (:require [monome-serial.series-protocol :as protocol]))

(defn led-on
  "Turn a specific monome led on for given set of x y coords"
  ([m coords] (apply led-on m coords))
  ([m x y]
     (send-bytes m (protocol/led-on-mesg x y))))

(defn led-off
  "Turn a specific monome led off for given set of x y coords"
  ([m coords] (apply led-off m coords))
  ([m x y]
     (send-bytes m (protocol/led-off-mesg x y))))

(defn clear
  "Turn all the monome leds off"
  [m]
  (send-bytes m protocol/clear-mesg))

(defn all
  "Turn all the monome leds on"
  [m]
  (send-bytes m protocol/all-mesg))

(defn row
  ([m idx col0 col1 col2 col3 col4 col5 col6 col7]
     (send-bytes m (protocol/row-mesg idx (bin-list->int col0 col1 col2 col3 col4 col5 col6 col7))))
  ([m idx col0 col1 col2 col3 col4 col5 col6 col7 col8 col9 col10 col11 col12 col13 col14 col15]
     (send-bytes m (protocol/row-mesg idx
                                      (bin-list->int col0 col1 col2 col3 col4 col5 col6 col7)
                                      (bin-list->int col8 col9 col10 col11 col12 col13 col14 col15))))
  ([m idx pattern]
     (send-bytes m (protocol/row-mesg idx pattern)))
  ([m idx pattern pattern2]
     (send-bytes m (protocol/row-mesg idx pattern pattern2))))

(defn col
  ([m idx row0 row1 row2 row3 row4 row5 row6 row7]
     (send-bytes m (protocol/col-mesg idx (bin-list->int row0 row1 row2 row3 row4 row5 row6 row7))))
  ([m idx row0 row1 row2 row3 row4 row5 row6 row7 row8 row9 row10 row11 row12 row13 row14 row15]
     (send-bytes m (protocol/col-mesg idx
                                      (bin-list->int row0 row1 row2 row3 row4 row5 row6 row7)
                                      (bin-list->int row8 row9 row10 row11 row12 row13 row14 row15))))
  ([m idx pattern]
     (send-bytes m (protocol/col-mesg idx pattern)))
  ([m idx pattern pattern2]
     (send-bytes m (protocol/col-mesg idx pattern pattern2))))

(defn frame
  "Send a complete frame (8x8) to the monome. Frame is a sequence of 8 integers representing bit arrays for each row of the monome. You may specify an index if your monome consists of more than one e.g. 0-3 for a 256 monome."
  ([m row0 row1 row2 row3 row4 row5 row6 row7]
     (send-bytes m (protocol/frame-mesg 0
                                        (apply bin-list->int row0)
                                        (apply bin-list->int row1)
                                        (apply bin-list->int row2)
                                        (apply bin-list->int row3)
                                        (apply bin-list->int row4)
                                        (apply bin-list->int row5)
                                        (apply bin-list->int row6)
                                        (apply bin-list->int row7))))
  ([m idx row0 row1 row2 row3 row4 row5 row6 row7]
     (send-bytes m (protocol/frame-mesg idx
                                        (apply bin-list->int row0)
                                        (apply bin-list->int row1)
                                        (apply bin-list->int row2)
                                        (apply bin-list->int row3)
                                        (apply bin-list->int row4)
                                        (apply bin-list->int row5)
                                        (apply bin-list->int row6))))
  ([m frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (send-bytes m (protocol/frame-mesg 0 row1 row2 row3 row4 row5 row6 row7 row8))))
  ([m idx frame]
     (let [[row1 row2 row3 row4 row5 row6 row7 row8] frame]
       (send-bytes m (protocol/frame-mesg idx row1 row2 row3 row4 row5 row6 row7 row8)))))
