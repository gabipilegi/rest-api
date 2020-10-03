(ns comment.handler
  (:require [reitit.core :as r]
            [reitit.ring :as ring]))

(def routes
  [["/ping" {:get (fn [request] {:status 200 :body "ok"})}]])

(def router
  (ring/router routes))

(def app
  (ring/ring-handler router))

(comment
  (app {:request-method :get :uri "/pring"})
  (r/match-by-path (r/router routes) "/api/")
  (r/match-by-path router "/api/")
  (r/routes router))
