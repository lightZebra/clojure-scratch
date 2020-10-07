(ns http-kit.async-response
  (:require [org.httpkit.server :refer :all]
            [clojure.core.async :as async])
  (:import (java.io ByteArrayInputStream InputStream)))

(defn generate-string [len]
  (apply str (repeatedly len #(char (+ (rand 26) 65)))))

(defn sending-loop [channel]
  (loop []
    (send! channel
           {:headers {"Content-Type"        "text/csv"
                      "Content-Disposition" "attachment; filename=\"long-stream.txt\""}
            ;:body    (generate-string (+ (* 50 1024 1024) (rand 10000)))}
            :body
                     (let [stream (ByteArrayInputStream. (.getBytes (generate-string (+ (* 50 1024 1024) (rand 10000))) "UTF-8"))
                           counter (atom 0)
                           process-read (fn [f]
                                          (println "asd")
                                          (if (zero? (mod (swap! counter inc) 10))
                                            (throw (OutOfMemoryError. "test"))
                                            (f)))]
                       (proxy [InputStream] []
                         (read
                           ([] (process-read #(.read stream)))
                           ([^bytes bytes] (process-read #(.read stream bytes)))
                           ([^bytes bytes off len] (process-read #(.read stream bytes off len)))
                           )))}
           false)
    (Thread/sleep 3000)
    (recur)))

(defn handler [req]
  (with-channel req channel

                (on-close channel (fn [_] (println "channel closed")))

                (async/thread-call (fn [] (sending-loop channel)))))


(def server (run-server handler {:port 8291}))