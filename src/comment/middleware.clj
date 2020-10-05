(ns comment.middleware)

(def db
  {:name    ::db
   :compile (fn [{:keys [db] :as route-data}
                route-ops]
              (fn [handler]
                (fn [request]
                  (handler (assoc request :db db)))))})
