(ns monome-serial.series-protocol)

(defn- compose-byte
  [left-int right-int]
  (bit-or (bit-shift-left left-int 4)
          right-int))

(defn- compose-byte-array [bytes]
  (byte-array (count bytes) (map #(.byteValue %) bytes)))

(defn led-on-mesg [x y]
  (compose-byte-array [(compose-byte 2 0)
                       (compose-byte x y)]))

(defn led-off-mesg [x y]
  (compose-byte-array [(compose-byte 3 0)
                       (compose-byte x y)]))

(defn frame-mesg [idx row0 row1 row2 row3 row4 row5 row6 row7]
  (compose-byte-array [(compose-byte 8 idx) row0 row1 row2 row3 row4 row5 row6 row7]))

(defn intensity-mesg [intensity]
  (compose-byte-array [(compose-byte 10 intensity)]))

(defn row-mesg
  ([row pattern]
     (compose-byte-array [(compose-byte 4 row) pattern]))
  ([row pattern1 pattern2]
     (compose-byte-array [(compose-byte 6 row) pattern1 pattern2])))

(defn col-mesg
  ([col pattern]
     (compose-byte-array [(compose-byte 5 col) pattern]))
  ([col pattern1 pattern2]
     (compose-byte-array [(compose-byte 7 col) pattern1 pattern2])))

(def clear-mesg (compose-byte-array [(compose-byte 9 0)]))
(def all-mesg   (compose-byte-array [(compose-byte 9 1)]))
(def test-mode-all-mesg   (compose-byte-array [(compose-byte 11 1)]))
(def test-mode-clear-mesg (compose-byte-array [(compose-byte 11 2)]))
(def normal-mode-mesg     (compose-byte-array [(compose-byte 11 0)]))



;;       monmome serial protocol series 256/128/64
;;       brian crabtree - tehn@monome.org
;;
;;       revision: 070903
;;
;;
;;       from device:
;;
;;       message id:     (0) keydown
;;       bytes:          2
;;       format:         iiii.... xxxxyyyy
;;                               i (message id) = 0
;;                               x (x value) = 0-15 (four bits)
;;                               y (y value) = 0-15 (four bits)
;;       decode:         id match: byte 0 >> 4 == 0
;;                               x: byte 1 >> 4
;;                               y: byte 1 & 0x0f
;;
;;
;;       message id:     (1) keyup
;;       bytes:          2
;;       format:         iiii.... xxxxyyyy
;;                               i (message id) = 1
;;                               x (x value) = 0-15 (four bits)
;;                               y (y value) = 0-15 (four bits)
;;       decode:         id match: byte 0 >> 4 == 1
;;                               x: byte 1 >> 4
;;                               y: byte 1 & 0x0f
;;
;;
;;
;;       to device:
;;
;;       message id:     (2) led_on
;;       bytes:          2
;;       format:         iiii.... xxxxyyyy
;;                               i (message id) = 2
;;                               x (x value) = 0-15 (four bits)
;;                               y (y value) = 0-15 (four bits)
;;       encode:         byte 0 = (id) << 4 = 32
;;                               byte 1 = (x << 4) | y
;;
;;
;;       message id:     (3) led_off
;;       bytes:          2
;;       format:         iiii.... xxxxyyyy
;;                               i (message id) = 3
;;                               x (x value) = 0-15 (four bits)
;;                               y (y value) = 0-15 (four bits)
;;       encode:         byte 0 = (id) << 4 = 48
;;                               byte 1 = (x << 4) | y
;;
;;
;;       message id:     (4) led_row1
;;       bytes:          2
;;       format:         iiiiyyyy aaaaaaaa
;;                               i (message id) = 4
;;                               y (row to update) = 0-15 (4 bits)
;;                               a (row data 0-7) = 0-255 (8 bits)
;;       encode:         byte 0 = ((id) << 4) | y = 64 + y
;;                               byte 1 = a
;;
;;
;;       message id:     (5) led_col1
;;       bytes:          2
;;       format:         iiiixxxx aaaaaaaa
;;                               i (message id) = 5
;;                               x (col to update) = 0-15 (4 bits)
;;                               a (col data 0-7) = 0-255 (8 bits)
;;       encode:         byte 0 = ((id) << 4) | x = 80 + x
;;                               byte 1 = a
;;
;;
;;       message id:     (6) led_row2
;;       bytes:          3
;;       format:         iiiiyyyy aaaaaaaa bbbbbbbb
;;                               i (message id) = 6
;;                               y (row to update) = 0-15 (4 bits)
;;                               a (row data 0-7) = 0-255 (8 bits)
;;                               b (row data 8-15) = 0-255 (8 bits)
;;       encode:         byte 0 = ((id) << 4) | y = 96 + y
;;                               byte 1 = a
;;                               byte 2 = b
;;
;;
;;       message id:     (7) led_col2
;;       bytes:          3
;;       format:         iiiixxxx aaaaaaaa bbbbbbbb
;;                               i (message id) = 7
;;                               x (col to update) = 0-15 (4 bits)
;;                               a (col data 0-7) = 0-255 (8 bits)
;;                               b (col data 8-15) = 0-255 (8 bits)
;;       encode:         byte 0 = ((id) << 4) | x = 112 + x
;;                               byte 1 = a
;;                               byte 2 = b
;;
;;
;;       message id:     (8) led_frame
;;       bytes:          9
;;       format:         iiii..qq aaaaaaaa bbbbbbbb cccccccc dddddddd eeeeeeee ffffffff gggggggg hhhhhhhh
;;                               i (message id) = 8
;;                               q (quadrant) = 0-3 (2 bits)
;;                               a-h (row data 0-7, per row) = 0-255 (8 bits)
;;       encode:         byte 0 = ((id) << 4) | q = 128 + q
;;                               byte 1,2,3,4,5,6,7,8 = a,b,c,d,e,f,g,h
;;       note:           quadrants are from top left to bottom right, as shown:
;;                               0 1
;;                               2 3
;;
;;       message id:     (9) clear
;;       bytes:          1
;;       format:         iiii---c
;;                               i (message id) = 9
;;                               c (clear state) = 0-1 (1 bit)
;;       encode:         byte 0 = ((id) << 4) | c = 144 + c
;;       note:           clear state of 0 turns off all leds.
;;                               clear state of 1 turns on all leds.
;;
;;
;;       message id:     (10) intensity
;;       bytes:          1
;;       format:         iiiibbbb
;;                               i (message id) = 10
;;                               b (brightness) = 0-15 (4 bits)
;;       encode:         byte 0 = ((id) << 4) | b = 160 + b
;;
;;
;;       message id:     (11) mode
;;       bytes:          1
;;       format:         iiii..mm
;;                               i (message id) = 11
;;                               m (mode) = 0-3 (2 bits)
;;       encode:         byte 0 = ((id) << 4) | m = 176 + m
;;       note:           mode = 0 : normal
;;                               mode = 1 : test (all leds on)
;;                               mode = 2 : shutdown (all leds off)
;;
;;
;;
;;
;;
;;       auxiliary ports
;;
;;       to device:
;;
;;       message id:     (12) activate port
;;       bytes:          1
;;       format:         iiiiaaaa
;;                               i (message id) = 12
;;                               a (which port) = 0-15 (four bits)
;;       encode:         byte 0 = (id) << 4 = 192 + a
;;
;;
;;       message id:     (13) deactivate port
;;       bytes:          1
;;       format:         iiiiaaaa
;;                               i (message id) = 13
;;                               a (which port) = 0-15 (four bits)
;;       encode:         byte 0 = (id) << 4 = 208 + a
;;
;;
;;       from device:
;;
;;       message id:     (14) auxiliary input
;;       bytes:          2
;;       format:         iiiiaaaa dddddddd
;;                               i (message id) = 14
;;                               a (port number) = 0-15 (four bits)
;;                               d (data) = 0-255 (eight bits)
;;       decode:         id match: byte 0 >> 4 == 1
;;                               a: byte 0 & 0x0f
;;                               d: byte 1
