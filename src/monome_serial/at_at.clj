;;TODO extract into external Clojar (and potentially merge with improcess/at-at when stable

(ns monome-serial.at-at
  (:import (java.util.concurrent ScheduledThreadPoolExecutor TimeUnit
                                 PriorityBlockingQueue)))

(def NUM-PLAYER-THREADS 10)
(def *player-pool* (ScheduledThreadPoolExecutor. NUM-PLAYER-THREADS))

(defn now []
  (System/currentTimeMillis))

(defn schedule
  "Schedules fun to be executed after ms-delay milliseconds."
  [fun ms-delay]
  (.schedule *player-pool* fun (long ms-delay) TimeUnit/MILLISECONDS))

(defn stop-players [& [now]]
  (if now
    (.shutdownNow *player-pool*)
    (.shutdown *player-pool*))
  (def *player-pool* (ScheduledThreadPoolExecutor. NUM-PLAYER-THREADS)))

(defn stop-player [player & [now]]
  (.cancel player (or now false)))

(def *APPLY-AHEAD* 250)

(defn apply-at [func ms-time & args]
  (let [delay-time (- ms-time (now))]
    (if (< delay-time 0)
      (apply func args)
      (schedule #(apply func args) delay-time))))

(defn apply-before [func ms-time & args]
  (apply apply-at func (- ms-time *APPLY-AHEAD*) args))
