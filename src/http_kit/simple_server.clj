(ns http-kit.simple-server
  (:require [org.httpkit.server :refer :all]))

(defn app [_]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello http-kit!"})

(run-server app {:port 8081})
