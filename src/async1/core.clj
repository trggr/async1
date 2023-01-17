(ns async1.core
  (:require [clojure.core.async :as a
             :refer [chan >!! >! <! close! go-loop]]
            [async1.modeler :as modeler]
            [async1.lib :as lib]))

(defn say-to [modeler req]
  (>!! modeler req))

(defn start-data-modeler
  "Returns a new channel and spawns a background process to
   listen on it. The requesters can communicate to the data modeler
   by sending the requests (a map with keys :request, :args)
   to the channel.
   To stop the data modeler, send a request {:request :shutdown}"
  [handler-fn]
  (let [in (chan)]
    (println "Modeler is available on channel " in)
    (go-loop []
      (let [req (<! in)
            resp (handler-fn req)]
        (if-not resp
          (do
            (println "Modeler is going home, and closing the channel " in)
            (close! in))
          (recur))))
    in))


(comment
  ; (def tim (start-data-modeler modeler/basic))
  ; (def tim (start-data-modeler modeler/echo))
  (def tim (start-data-modeler modeler/thorough))

  (say-to tim (modeler/request-change {:from "Jason", :subject "New table STG_CES1", :release "Feb 2023"}))
  (say-to tim (modeler/request-change {:from "Lisa", :subject "New table STG_DM_1", :release "May 2023"}))
  (say-to tim {:request "I don't know what I want"})
  (say-to tim (modeler/request-change {:from "Steven", :subject "New table STG_ARCA_1"}))
  (say-to tim (modeler/request-change {:from "Caroline", :subject "New table STG_MLR"}))
  (say-to tim (modeler/request-shutdown))
)

(defn -main [] )

