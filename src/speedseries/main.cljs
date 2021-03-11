(ns speedseries.main
  (:require [integrant.core :as ig]
            [macchiato.server :as server]
            [macchiato.middleware.resource :refer [wrap-resource]]
            ["better-sqlite3" :as sqlite]
            [speedseries.schedule :as schedule]
            [speedseries.speedtest :refer [run-speedtest!]]
            [speedseries.dashboard :refer [dashboard-handler]]))

(def system-config {:http/server        {:port 1337 :handler (ig/ref :handler/app)}
                    :handler/app        {:db (ig/ref :database/sqlite)}
                    :schedule/speedtest {:db (ig/ref :database/sqlite)}
                    :database/sqlite    {:db-url "db.sqlite3"}})

(defmethod ig/init-key :http/server [_ {:keys [handler port]}]
  (server/start
    {:handler    (wrap-resource handler "target/public")
     :port       port
     :on-success #(println (str "Server started on http://localhost:" port))}))

(defmethod ig/halt-key! :http/server [_ server]
  (.close server))


(defmethod ig/init-key :handler/app [_ {:keys [db]}]
  (dashboard-handler {:db db}))


(defmethod ig/init-key :schedule/speedtest [_ {:keys [db]}]
  (schedule/every-hour #(run-speedtest! db)))

(defmethod ig/halt-key! :schedule/speedtest [_ stop-schedule]
  (stop-schedule))


(defmethod ig/init-key :database/sqlite [_ {:keys [db-url]}]
  (new sqlite db-url))

(defmethod ig/halt-key! :database/sqlite [_ db]
  (.close db))

(defonce system (atom nil))

(defn main! []
  (reset! system (ig/init system-config))
  (println "App loaded!")
  (.on js/process "unhandledRejection" js/console.error))

(defn ^:dev/after-load reload! []
  (ig/halt! @system)
  (reset! system (ig/init system-config))
  (println "Reloaded system!"))
