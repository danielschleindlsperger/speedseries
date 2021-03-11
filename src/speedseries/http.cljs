(ns speedseries.http
  (:require [promesa.core :as p]))

(defn p-handler
  "Utility function to create an async-ring compatible handler function from a promise returning handler function.
  Takes the handler as an argument. The handler will receive the ring request as its only parameter and should return
  a promise that either resolves to a response map or rejects with an error.

  Also merges the given dependencies into the :deps key on the ring request for the handler to use."
  [handler deps]
  (fn [req respond raise]
    (p/handle (p/then (p/promise nil) #(handler (merge req {:deps deps})))
              (fn [result error]
                (if error
                  (raise error)
                  (respond result))))))
