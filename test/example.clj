(ns example
  (:require [monome-serial.connector :as monome]))

(monome/list-ports)

;(print "Enter port index: ")
;(flush)
;(def idx (read))
;(println "opening: " (monome/port-at idx))

(def idx 0)

(def m (monome/open (monome/port-at idx)))
(monome/clear m)

(defn intro []
  ; blink all
  (monome/fill m)
  (Thread/sleep 200)
  (monome/clear m)
  (Thread/sleep 200)

  ; fill and clear one at a time
  (doseq [x (range 16)
          y (range 8)]
    (monome/led-on m x y)
    (Thread/sleep 5))
  (doseq [x (range 16)
          y (range 8)]
    (monome/led-off m x y)
    (Thread/sleep 5)))

(defn fly [[x y] m xs ys]
  (monome/led-off m x y)
  (let [x (first xs)
        y (first ys)]
    (when (and x y)
      (monome/led-on m x y)
      (Thread/sleep 500)
      (recur [x y] m (next xs) (next ys)))))

(defn shoot [m x y xs ys] 
  (send-off (agent [x y]) fly m xs ys))

; Todo: make this work :-)
; The idea is to shoot out animated lights from anywhere we emit
(defn emit [m x y]
  (shoot m x y (repeat x) (range y 0 -1))      ; up
  (shoot m x y (range x 16 1) (range y 0 -1))  ; up-right
  (shoot m x y (range x 16 1) (repeat y))      ; right
  (shoot m x y (range x 16 1) (range y 8 1))   ; right-down
  (shoot m x y (repeat x) (range y 8 1))       ; down
  (shoot m x y (range x 0 -1) (range y 8 1))   ; down-left
  (shoot m x y (range x 0 -1) (repeat y))      ;left
  (shoot m x y (range x 0 -1) (range y 0 -1))) ; left-up

(defn demo []
  (let [finished? (promise)]

    ; Add a handler for button events
    ; args: op (:up, :down), x, y
    (monome/add-handler m (fn [op x y]
             (println op x y)
             (cond
             (and (= 0 x) (= 0 y)) (deliver finished? :done)
             (= :down op)  (monome/led-on m x y)
             (= :up op)   (monome/led-off m x y))))

    (println "Press buton (0,0) to exit (top left corner with cord facing away).")

    ; Play the intro
    (intro)

    ; Block reading on a promise until a value gets delivered by pressing 0,0
    @finished?

    ; close and exit
    (monome/close m)
    (System/exit 0)))

(demo)
