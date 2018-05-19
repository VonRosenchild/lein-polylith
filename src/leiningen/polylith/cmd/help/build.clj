(ns leiningen.polylith.cmd.help.build)

(defn help []
  (println "  Build artifacts.")
  (println)
  (println "  lein polylith build PREFIX [ARG] [SKIP]")
  (println "    PREFIX = (omitted) -> Uses time.local.edn")
  (println "             prefix    -> Uses given prefix as time.PREFIX.edn.")
  (println)
  (println "    ARG = (omitted) -> Since last successful build, stored in bookmark")
  (println "                       :last-successful-build in WS-ROOT/.polylith/time.local.edn.")
  (println "          timestamp -> Since the given timestamp (milliseconds since 1970).")
  (println "          bookmark  -> Since the timestamp for the given bookmark in WS-ROOT/.polylith/time.local.edn.")
  (println)
  (println "    SKIP = (omitted) -> Compiles, tests, builds, and sets :last-successful-build")
  (println "           -compile -> Skips compilation step")
  (println "           -test    -> Skips test step")
  (println "           -success -> Skips success step")
  (println)
  (println "  examples:")
  (println "    lein polylith build")
  (println "    lein polylith build -compile")
  (println "    lein polylith build local")
  (println "    lein polylith build remote")
  (println "    lein polylith build local 1523649477000")
  (println "    lein polylith build local mybookmark")
  (println "    lein polylith build local 1523649477000 -compile -test"))

