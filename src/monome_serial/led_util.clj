(ns monome-serial.led-util)

(defn bin-list->int
  [a b c d e f g h]
  (bit-or (bit-shift-left a 1)
          (bit-or (bit-shift-left b 2)
                  (bit-or (bit-shift-left c 3)
                          (bit-or (bit-shift-left d 4)
                                  (bit-or (bit-shift-left e 5)
                                          (bit-or (bit-shift-left f 6)
                                                  (bit-or (bit-shift-left g 7)
                                                          h))))))))
