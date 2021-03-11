(ns speedseries.model
  (:require [clojure.string :as str]
            ["better-sqlite3" :as sqlite]))

(defn- values [obj]
  (let [params (map #(str "@" %) (keys (js->clj obj)))]
    (str "(" (str/join ", " params) ")")))

(defn- insert
  "Insert js object `m` into table `tbl`.

   Example:
   (insert db :foo #js {:baz 123})"
  [^js/sqlite db tbl m]
  (-> db
      (.prepare (str "insert into " (name tbl) " values" (values m)))
      (.run m)))

(defn- select
  [^js/sqlite db tbl]
  (-> db
      (.prepare (str "select * from " (name tbl)))
      (.all)))

;;
;; Speedtest results
;;

(defn insert-result [db result]
  (insert db :result result))

(defn all-speedtest-results [db]
  (select db :result))

;;
;; Servers
;;

(defn insert-server [db server]
  (insert db :server server))

(defn all-servers [^js/sqlite db]
  (select db :server))
