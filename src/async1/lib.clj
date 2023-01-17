(ns async1.lib)

(defn systimestamp []
  (java.time.LocalDateTime/now))

(defn to-char
  ([date]
   (to-char date "MM/dd/yyyy HH:mm:ss"))
  ([date format]
   (.format date (java.time.format.DateTimeFormatter/ofPattern format))))

