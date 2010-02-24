(ns example
  (:require [monome-serial.connector :as monome]))

(def m (monome/open))

(println m)

;(monome/add-handler m #(println %1 %2 %3))
;(monome/add-handler m #(when (= :down %1) (monome/led-on m %2 %3)))
;(monome/add-handler m #(when (= :up %1) (monome/led-off m %2 %3)))

(println m)

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

(intro)

(def finished? (promise))

(monome/add-handler m (fn [op x y]
                        (println "Got event: " op x y)
                        (cond
                          (and (= 0 x) (= 0 y)) (deliver finished? :done)
                          (= :down op) (monome/led-on m x y)
                          (= :up op)   (monome/led-off m x y))))

@finished?
(monome/close m)
(System/exit 0)
