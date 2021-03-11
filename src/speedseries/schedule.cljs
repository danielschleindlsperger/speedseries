(ns speedseries.schedule)

(def ^:private hour-in-ms (* 1000 60 60))

(defn every-hour
  "Run function `f` initially and once every hour afterwards.

   Returns a function that stops the schedule when called with no arguments.
   If `f` was already called the execution cannot be stopped.

   Takes an optional parameter map as the second argument:

   * :run-initially? - default `true` - Whether to run `f` once immediately or wait for the interval.
   * :initial-delay - default `10000` - Delay in millis until the schedule is run for the first time."
  ([f] (every-hour f {}))
  ([f {:keys [initial-delay run-initially?], :or {initial-delay  10000
                                                  run-initially? true}}]
   (let [s (atom {:timeout nil :interval nil})
         stop-schedule #(do (js/clearTimeout (:timeout @s))
                            (js/clearInterval (:interval @s)))]
     (js/setTimeout (fn []
                      (when run-initially? (f))
                      (js/setInterval #(f) hour-in-ms))
                    initial-delay)
     stop-schedule)))