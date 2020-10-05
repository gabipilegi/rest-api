(ns user
  (:require [integrant.repl]
            [comment.system :as system]))

(integrant.repl/set-prep! (fn [] system/system-config))

(def go integrant.repl/go)
(def halt integrant.repl/halt)
(def reset integrant.repl/reset)
(def reset-all integrant.repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
