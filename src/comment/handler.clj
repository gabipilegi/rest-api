(ns comment.handler
  (:require [muuntaja.core :as m]
            [reitit.core :as r]
            [reitit.ring :as ring]
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
      :post {:summary "Create a new comment"
             :handler ok}}]
    ["/slug"
     {:get {:summary "Get comments by slug"
            :handler ok}}]
    ["/id/:id"
     {:put    {:summary "Update a comment by the moderator"
               :handler ok}
      :delete {:summary "Delete a comment by yhe moderator"
               :handler ok}}]]])


(def router
  (ring/router routes
               {:data {:muuntaja m/instance
                       :middleware [muuntaja/format-middleware]}}))

(def app
  (ring/ring-handler router
                     (ring/routes (swagger-ui/create-swagger-ui-handler
                                   {:path "/"}))))


(defn start []
  (jetty/run-jetty #'app {:port 3000 :join? false})
  (println "Server running on port 3000"))

(comment
  (start)
  (app {:request-method :get :uri "/pring"})
  (r/match-by-path (r/router routes) "/api/")
  (r/match-by-path router "/api/")
  (r/routes router))
