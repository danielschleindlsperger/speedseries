(ns speedseries.browser-app
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]
            [goog.object :as go]))

(defn- bytes->mbits [bytes] (/ (* bytes 8) 1024 1024))

(defn- parse-chart-data! []
  (when-let [data-node (.querySelector js/document "#chart-data")]
    (.parse js/JSON (go/get data-node "innerHTML"))))

(defn- bandwidth-result [results metric]
  (map (fn [result] [(go/get result "timestamp")
                     (.toFixed (bytes->mbits (go/get result metric)) 2)])
       results))

(defn- render-tooltip [])

(defn- render-chart! [chart-data]
  (let [chart-node (.querySelector js/document "#chart")
        results (go/get chart-data "results")
        servers (go/get chart-data "servers")
        options (clj->js {:series  [{:name "Download", :data (bandwidth-result results "download_bandwidth_bytes")}
                                    {:name "Upload", :data (bandwidth-result results "upload_bandwidth_bytes")}]
                          :yaxis   {:title {:text "Bandwidth (Mbit/s)"}
                                    :min   0}
                          :xaxis   {:type "datetime"}
                          :chart   {:type    "area"
                                    :stacked false
                                    :height  "500px"}
                          :title   {:text  "Bandwidth Speedtests"
                                    :align "center"}
                          :stroke  {:curve "stepline"}
                          :fill    {:type     "gradient"
                                    :gradient {:shadeIntensity 1
                                               :inverseColors  false
                                               :opacityFrom    0.6
                                               :opacityTo      0
                                               :stops          [0 90 100]}}
                          :tooltip {:custom (fn [arg]
                                              (let [result (aget results (go/get arg "seriesIndex"))]
                                                (js/console.log result)
                                                (html [:div {:style "display: flex;flex-direction:column;"}
                                                       [:span "Date: " (go/get result "timestamp")]
                                                       [:span "Download: " (go/get result "download_bandwidth_bytes")]
                                                       [:span "Upload: " (go/get result "upload_bandwidth_bytes")]
                                                       [:span "Jitter: " (go/get result "jitter")]
                                                       [:span "Latency: " (go/get result "latency_ms") "ms"]
                                                       [:span "Server" "TODO"]
                                                       [:span "ISP" "TODO"]])))}})
        chart (new js/window.ApexCharts chart-node options)]
    (.render chart)
    chart))

(defn- cleanup! [^js/window.ApexCharts chart]
  (.destroy chart))

(defonce state (atom {:chart-data nil :chart nil}))

(defn main! []
  (swap! state assoc :chart-data (parse-chart-data!))
  (swap! state assoc :chart (render-chart! (:chart-data @state))))

(main!)

(defn ^:dev/after-load reload! []
  (cleanup! (:chart @state))
  (swap! state assoc :chart (render-chart! (:chart-data @state)))
  (println "Reloaded system!"))
