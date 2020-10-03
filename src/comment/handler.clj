(ns comment.handler
  (:require [muuntaja.core :as m]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]))

(def routes
  [["/api"
    {:swagger {:tags ["API"]}}
    ["/ping"
     {:get {:handler (fn [request] {:status 200 :body "Pong"})
            :swagger {:title "Ping"
                      :description "Ping"}}}]]
   ["/swagger.json"
    {:get {:handler (swagger/create-swagger-handler)
           :no-doc  true
           :swagger {:title "Comment system API"
                     :description "Comment system API"}}}]])


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
