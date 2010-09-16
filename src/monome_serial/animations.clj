(ns monome-serial.animations
  (:require [monome-serial.led  :as led]
            [monome-serial.util :as util]))

(defn intromation
  [m]
  (led/clear m)
  (util/brightness m 0)
  (led/all m)
  (let [sleep-times [ 0, 0.072, 0.1030, 0.1159, 0.1236, 0.1287, 0.1324, 0.1352, 0.1373, 0.1390, 0.1404, 0.1416, 0.1426, 0.1435, 0.1442, 0.1448, 0.1278 ]]

    (dotimes [i 16] (Thread/sleep (* 250 (nth sleep-times i))) (util/brightness m i))
    (dotimes [i 16] (Thread/sleep (* 500 (nth sleep-times i))) (util/brightness m (- 15 i))))
  (led/clear m)
  (util/brightness m 15)
  m)
