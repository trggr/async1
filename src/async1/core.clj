(ns async1.core
  (:require [clojure.core.async :as a
             :refer [chan >! >!! <! close! go-loop go]]
            [async1.modeler :as modeler]))

(defn start-sa [to n from tables releases]
  (go-loop [n n]
    (if (zero? n)
      :done
      (do
        (Thread/sleep (* 1000 (rand-int 10)))
        (>! to (modeler/request-change
                {:from from,
                 :subject (str "Table " (rand-nth tables))
                 :release (rand-nth releases)}))
        (recur (dec n))))))

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

  (dotimes [_ 10]
    (say-to tim (modeler/request-change {:from "Nasoj", :subject "New table STG_CES1", :release "Feb 2023"}))
    (say-to tim (modeler/request-change {:from "Asil", :subject "New table STG_DM_1", :release "May 2023"}))
    (say-to tim {:request "I don't know what I want"})
    (say-to tim (modeler/request-change {:from "Nevets", :subject "New table STG_ARCA_1"}))
    (say-to tim (modeler/request-change {:from "Enilorac", :subject "New table STG_MLR"})))

  (say-to tim (modeler/request-shutdown))

  @modeler/request_cnt

  )

(defn -main []
  (let [tim (start-data-modeler modeler/thorough)]

    (start-sa tim 50 "Nasoj" ["STG_CES1", "STG_CES2", "STG_CES3"] ["Feb", "May"])
    (start-sa tim 50 " Asil" ["STG_DM1", "STG_DM2"] ["Jan", "Feb", "May"])
    (start-sa tim 30  "  Nevets" ["STG_ARCA1", "STG_ARCA2"] ["Feb"])
    (start-sa tim 20  "   Enilorac" ["STG_CHIRPS", "STG_MLR1", "STG_MLR2"] ["Feb", "May"])

    (Thread/sleep (* 10 60 1000))

    (println @modeler/request_cnt)))

