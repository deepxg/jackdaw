(ns jackdaw.test.transports.identity
  (:require
   [clojure.tools.logging :as log]
   [manifold.stream :as s]
   [jackdaw.test.transports :as t :refer [deftransport]]))

(defn identity-consumer
  [stream]
  (let [started? (promise)]
    {:started? started?
     :messages stream}))

(defn identity-producer
  []
  (let [messages (s/stream 1)]
    {:messages messages}))

(deftransport :identity
  [{:keys [topics]}]
  (let [ch (s/stream 1)
        test-consumer (identity-consumer ch)
        test-producer (identity-producer)]

    (s/connect (:messages test-producer)
               (:messages test-consumer))

    {:consumer test-consumer
     :producer test-producer
     :topics topics
     :exit-hooks [(fn []
                    (s/close! (:messages test-producer)))]}))
