(ns monome-serial.led-util)

(defn bin-list->int
  [a b c d e f g h]
  (bit-or (bit-shift-left h 7)
          (bit-or (bit-shift-left g 6)
                  (bit-or (bit-shift-left f 5)
                          (bit-or (bit-shift-left e 4)
                                  (bit-or (bit-shift-left d 3)
                                          (bit-or (bit-shift-left c 2)
                                                  (bit-or (bit-shift-left b 1)
                                                          a))))))))


