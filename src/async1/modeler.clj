;; ------ types of data modelers
(ns async1.modeler
  (:require [async1.lib :as lib]))

;; the requests which the data modeler understands
(defn request-change [request]
  (assoc request
         :request :change
         :date (lib/to-char (lib/systimestamp))))

(defn request-shutdown []
  {:request :shutdown
   :date (lib/to-char (lib/systimestamp))})

(defn basic
  "Takes map with at least one key :request, performs actions,
   and returns true, if the can continue handling requests after this one."
  [{:keys [request]}]
  (case request
    :shutdown
    false

    :change
    (do (println "Change")
        true)

    (do
      (println "Unsupported request " request)
      true)))

(defn echo
  [{:keys [request] :as mp}]
  (case request
    :shutdown
    false

    :change
    (do (println "New request" mp)
        true)
    (do
      (println "Unsupported request " request)
      true)))

(defn thorough
  [{:keys [request release] :as mp}]
  (case request
    :shutdown
    false

    :change
    (do (println
         (if release
           "Thank you for providing the release info."
           "Please provide the release info"))
        true)

    (do
      (println "Unsupported request " request)
      true)))

