(ns monome-serial.util
  (:use [monome-serial.comms :only (send-bytes)])
  (:require [monome-serial.series-protocol :as protocol]))

(defn test-mode-all
  "Enter monome test mode (does not lose led state from normal mode) and turn all leds on.
   Use normal-mode to restore led state"
  [m]
  (send-bytes m protocol/test-mode-all-mesg))

(defn test-mode-clear
  "Enter monome test mode (does not lose led state from normal mode) and turn all leds off
   Use normal-mode to restore led state"
  [m]
  (send-bytes m protocol/test-mode-clear-mesg))

(defn normal-mode
  "Exit monome test mode and restore led state as it was before test mode was entered"
  [m]
  (send-bytes m protocol/normal-mode-mesg))

(defn brightness
  "Set the brightness for all the leds on the monome. Specify an integer intensity in the range 0-15"
  [m intensity]
  (send-bytes m (protocol/intensity-mesg intensity)))
