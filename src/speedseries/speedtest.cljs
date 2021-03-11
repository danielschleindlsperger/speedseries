(ns speedseries.speedtest
  (:require ["child_process" :as cp]
            [promesa.core :as p]
            [goog.object :as go]
            [speedseries.model :refer [insert-result insert-server]]))

(defn- speedtest-cli! []
  (p/create (fn [resolve reject]
              (cp/exec "speedtest --format=json"
                       (fn [error stdout stderr]
                         (cond stdout (resolve stdout)
                               error (reject error)
                               stderr (reject (new js/Error stderr))))))))

(defn- parse-result [result]
  {:result #js {"id"                       (go/getValueByKeys result "result" "id")
                "timestamp"                (-> (go/getValueByKeys result "timestamp") (js/Date.) (.getTime))
                "url"                      (go/getValueByKeys result "result" "url")
                "download_bandwidth_bytes" (go/getValueByKeys result "download" "bandwidth")
                "upload_bandwidth_bytes"   (go/getValueByKeys result "upload" "bandwidth")
                "latency_ms"               (go/getValueByKeys result "ping" "latency")
                "jitter"                   (go/getValueByKeys result "ping" "jitter")
                "server_id"                (go/getValueByKeys result "server" "id")}
   :server #js {"id"       (go/getValueByKeys result "server" "id")
                "name"     (go/getValueByKeys result "server" "name")
                "country"  (go/getValueByKeys result "server" "country")
                "location" (go/getValueByKeys result "server" "location")}})

(defn run-speedtest! [db]
  (println "RUNNING SPEEDTEST!")
  (p/chain (speedtest-cli!)
           #(.parse js/JSON %)
           parse-result
           #(do (insert-result db (:result %)) %)
           #(do (insert-server db (:server %)) %)
           #(println "Successfully ran speedtest and persisted results")))
