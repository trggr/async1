(ns async1.example1
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))

(defn request [channel from subj]
  (>!! channel {"From:" from,
                "Date:" (System/currentTimeMillis),
                "Subject:" subj}))

(defn stop-work [channel]
  (>!! channel "end of the day"))

(defn start-work [channel]
  (println "Data modeler is open for business")
  (go (loop [req (<!! channel)]
        (if (= req "end of the day")
          (do (println "Data modeler is going home")
              :done)
          (do
            (println "New request" req)
            (recur (<!! channel)))))))

(comment
  (def dmr (chan 3))

  (start-work dmr)

  (request dmr "Jason" "New table STG_CES_DIR_OPEX")
  (request dmr "Lisa" "New table STG_DM_CLIENT_BEN")
  (request dmr "Steven" "New table STG_ARCA_1")
  (request dmr "Caroline" "New table STG_MLR")

  (stop-work dmr)

)