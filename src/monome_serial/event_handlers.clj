(ns monome-serial.event-handlers
  (:use [clojure.contrib.core :only [dissoc-in]]))

(defn on-action
  "Add an event handler function f to monome m within the specified group.

  The supplied function should have an arity of 3: action x y
  Where action is either :press or :release and x and y are the coords of the button that generated the event

  The specified handler group is created if it doesn't previously exist"

  ([m f group] (on-action m f group f))
  ([m f group name]
     (dosync (alter (:handlers m) assoc-in [group name] f))
     m))

(defn on-press
  "Add an event handler function f to monome m within the specified group. This handler then only handles key press events.

  The supplied function should have an arity of 2: x y
  Where x and y are the coords of the button that was pressed

  The specified handler group is created if it doesn't previously exist"

  ([m f group] (on-press m f group f))
  ([m f group name]
     (on-action m (fn [op x y] (if (= op :press) (apply f [x y]))) group name)
     m))

(defn on-release
  "Add an event handler function f to monome m within the specified group. This handler only handles key release events.

  The supplied function should have an arity of 2: x y
  Where x and y are the coords of the button that was released

  The specified handler group is created if it doesn't previously exist."

  ([m f group] (on-release m f group f))
  ([m f group name]
     (on-action m (fn [op x y] (if (= op :release) (apply f [x y]))) group name)
     m))

(defn remove-handler
  "Remove the given handler function belonging to the specified group and having the specified name from monome m."
  [m group name]
  (dosync (alter (:handlers m) dissoc-in [group name]))
  m)

(defn remove-group-handlers
  "Remove all handlers from the specified group from monome m."
  [m group]
  (dosync (alter (:handlers m) dissoc group)))

(defn remove-all-handlers
  "Removes all of the given monome's handlers."
  [m]
  (dosync (ref-set (:handlers m) {}))
  m)
