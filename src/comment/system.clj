(ns comment.system
  (:require comment.handler
            [integrant.core :as integrant]
            [ring.adapter.jetty :as jetty]))

(def system-config
  {:comment/jetty   {:handler (integrant/ref :comment/handler)
                     :port 3000}
   :comment/handler {:sqlite (integrant/ref :comment/sqlite)}
   :comment/sqlite  nil})

(defmethod integrant/init-key :comment/jetty [_ {:keys [handler port]}]
  (println "Starting system at port" port)
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod integrant/init-key :comment/handler [_ {:keys [sqlite]}]
  (comment.handler/create-app sqlite))

(defmethod integrant/init-key :comment/sqlite [c _]
  {:no-db true})

(defmethod integrant/halt-key! :comment/jetty [_ jetty]
  (.stop jetty))

(defmethod integrant/halt-key! :comment/handler [_ b]
  (println "Halt" b))

(defmethod integrant/halt-key! :comment/sqlite [_ c]
  (println "Halt" c))

(defn -main []
  (integrant/init system-config))

(comment
  ;;initializes the component without dependence first and then goes up
  ;;halts the component with dependence then it halts it's dependencies
  (integrant/halt! system))
