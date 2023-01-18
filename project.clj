(defproject async1 "0.1.0-SNAPSHOT"
  :description "Learning core.async by modeling modeler's behavior"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.async "1.6.673"]]
  :main ^:skip-aot async1.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
