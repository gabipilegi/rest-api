(ns comment.handler
  (:require [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]))

(def ok (constantly {:status 200 :body "ok"}))

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


(def router
  (ring/router routes
               {:data {:muuntaja   m/instance
                       :coercion   coercion.spec/coercion
                       :middleware [swagger/swagger-feature
                                    exception/exception-middleware
                                    muuntaja/format-negotiate-middleware
                                    muuntaja/format-response-middleware
                                    muuntaja/format-request-middleware
                                    coercion/coerce-request-middleware
                                    coercion/coerce-response-middleware]}}))

(def app
  (ring/ring-handler router
                     (ring/routes (swagger-ui/create-swagger-ui-handler
                                   {:path "/"}))))


(defn start []
  (jetty/run-jetty #'app {:port 3000 :join? false})
  (println "Server running on port 3000"))

(comment
  (start))
