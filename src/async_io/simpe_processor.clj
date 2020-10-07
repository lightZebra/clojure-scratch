(ns async-io.simpe-processor
  (:require [clojure.core.async :as async]))

(def input-ch (async/chan 3))
(def terminate-ch (async/chan))

(defn consumer [input-ch terminate-ch]
  (async/thread
    (loop []
      (println "get from channels")
      (let [[v ch] (async/alts!! [input-ch terminate-ch])]
        (when (identical? ch input-ch)
          (println "channel element: " v)
          (Thread/sleep 10000)
          (recur))))))

(defn go-consumer [name input-ch terminate-ch]
  (async/go-loop []
    (println name "consumer get from channel" (Thread/currentThread))
    (let [[v ch] (async/alts! [input-ch terminate-ch])]
      (when (identical? ch input-ch)
        (println name "go-consumer channel element: " v "thread:" (Thread/currentThread))
        (Thread/sleep 10000000)
        (recur)))))


(comment
  (dotimes [i 2] (async/go (async/>! channel i)))
  (dotimes [i 2] (async/>!! input-ch i))

  (async/>!! input-ch -1)
  (async/<!! input-ch)

  (async/alts!! [input-ch (async/timeout 20)])

  (consumer input-ch terminate-ch)

  (go-consumer (str "go-" 100) input-ch terminate-ch)
  (dotimes [i 10] (go-consumer (str "go-" i) input-ch terminate-ch))

  )
