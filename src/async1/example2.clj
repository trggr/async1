(ns async1.example2
  (:require [clojure.core.async
             :as a
             :refer [>!! <!! chan close! go-loop]]))

;; ----- data modeler understands the requests
(defn new-dm-request [from subj]
  {:command :request
   :args {"From:" from,
          "Date:" (System/currentTimeMillis),
          "Subject:" subj}})

(defn shutdown []
  {:command :shutdown})

;; ------ how data modeler handles requests
(defn bus-logic [{:keys [command args]}]
  (case command
    :shutdown :shutdown
    :request (println "New request" args)
    :else (println "Unsupported command " command)))

;; ----------- channel logic is confined here -----------
(defn say-to [data-modeler req]
  (>!! data-modeler req))

(defn new-data-modeler
  "Returns a new channel and spawns a background process to
   listen on it. To shutdown the process, send {:command :shutdown}"
  []
  (let [in (chan)]
    (println "Data modeler is open for business on channel " in)
    (go-loop [req (<!! in)]
      (let [resp (bus-logic req)]
        (if (= resp :shutdown)
          (do
            (println "Data modeler is going home, and closing the channel " in)
            (close! in)
            true)
          (recur (<!! in)))))
    in))

(comment
  (def tim (new-data-modeler))
  (say-to tim (new-dm-request "Jason" "New table STG_CES1"))
  (say-to tim (new-dm-request "Lisa" "New table STG_DM_1"))
  (say-to tim (new-dm-request "Steven" "New table STG_ARCA_1"))
  (say-to tim (new-dm-request "Caroline" "New table STG_MLR"))
  (say-to tim (shutdown))
  )
