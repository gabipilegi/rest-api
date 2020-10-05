(ns comment.handler
  (:require comment.middleware
            [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(defn ok [{:keys [db] :as request}]
  (println db)
  {:status 200
   :body   "ok"})

(def routes
  [["/swagger.json"
    {:get {:handler (swagger/create-swagger-handler)
           :no-doc  true
           :swagger {:info {:title "Comment system API"}}}}]
   ["/comments"
    {:swagger {:tags ["comments"]}}
    [""
     {:get  {:summary "Get all comments"
             :handler ok}
      :post {:summary    "Create a new comment"
             :handler    ok
             :parameters {:body {:name              string?
                                 :slug              string?
                                 :text              string?
                                 :parent-comment-id int?}}
             :responses  {200 {:body string?}}}}]
    ["/slug/:slug"
     {:get {:summary    "Get comments by slug"
            :handler    ok
            :parameters {:path {:slug string?}}}}]
    ["/id/:id"
     {:put    {:summary    "Update a comment by the moderator"
               :parameters {:path {:id int?}
                            :body {:name              string?
                                   :slug              string?
                                   :text              string?
                                   :parent-comment-id int?}}
               :handler    ok}
      :delete {:summary "Delete a comment by the moderator"
               :handler ok}}]]])

(defn create-app
  [db]
  (ring/ring-handler (ring/router routes
                                  {:data {:db         db
                                          :muuntaja   m/instance
                                          :coercion   coercion.spec/coercion
                                          :middleware [swagger/swagger-feature
                                                       exception/exception-middleware
                                                       muuntaja/format-negotiate-middleware
                                                       muuntaja/format-response-middleware
                                                       muuntaja/format-request-middleware
                                                       coercion/coerce-request-middleware
                                                       coercion/coerce-response-middleware
                                                       comment.middleware/db]}})
                     (ring/routes (swagger-ui/create-swagger-ui-handler
                                   {:path "/"}))))
