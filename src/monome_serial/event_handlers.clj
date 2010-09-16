(ns monome-serial.event-handlers)

(defn on-action
  "Add an event handler function f to monome m.
  The function takes 3 args: action x y
  Where action is either :press or :release and x and y are the coords of the button that generated the event"
  ([m f] (on-action m f f))
  ([m f name]
     (dosync (alter (:handlers m) assoc name f))
     m))

(defn on-press
  "Add an event handler function f to monome m that only handles key press events
  The function takes 2 args: x y
  Where x and y are the coords of the button that was pressed"
  ([m f] (on-press m f f))
  ([m f name]
     (on-action m (fn [op x y] (if (= op :press) (apply f [x y]))) name)
     m))

(defn on-release
  "Add an event handler function f to monome m that only handles key release events
  The function takes 2 args: x y
  Where x and y are the coords of the button that was released"
  ([m f] (on-release m f f))
  ([m f name]
     (on-action m (fn [op x y] (if (= op :release) (apply f [x y]))) name)
     m))

(defn remove-handler
  "Remove the given handler function with name from monome m."
  [m name]
  (dosync (alter (:handlers m) dissoc name))
  m)

(defn remove-all-handlers
  "Removes all of the given monome's handlers for both press and release events"
  [m]
  (dosync (ref-set (:handlers m) {}))
  m)
