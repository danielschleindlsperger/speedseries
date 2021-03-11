(ns speedseries.dashboard
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime]
            [speedseries.http :refer [p-handler]]
            [speedseries.model :refer [all-speedtest-results all-servers]]))

(defn clj->json [x] (.stringify js/JSON (clj->js x)))

(defn- dashboard-page [results servers]
  (str "<!DOCTYPE html>"
       (html [:html {:lang "en"}
              [:head
               [:meta {:charset "utf-8"}]
               [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
               [:title "SpeedSeries"]]
              [:body {:style "display:flex;flex-direction:column;height:100vh;"}
               [:h1 "SpeedSeries Dashboard"]
               [:section {:id "chart" :style "flex-grow:1;position:relative;"}]
               [:script {:type "application/json"
                         :id   "chart-data"}
                (let [json (clj->json {:results results :servers servers})]
                  ;; For some reason setting the inner html actually consumes the element itself.
                  ;; We use a dummy div to counteract this.
                  [:div {:dangerously-set-inner-HTML {:__html json}}])]
               [:script {:src "https://cdn.jsdelivr.net/npm/apexcharts"}]
               [:script {:src "/assets/browser-app.js"}]]])))

(defn dashboard-handler [deps]
  (p-handler (fn [req]
               (let [db (get-in req [:deps :db])
                     results (all-speedtest-results db)
                     servers (all-servers db)]
                 {:status  200
                  :body    (dashboard-page results servers)
                  :headers {"content-type" "text/html"}}))
             deps))
