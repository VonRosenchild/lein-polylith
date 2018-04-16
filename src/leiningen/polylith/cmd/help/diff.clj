(ns leiningen.polylith.cmd.help.diff)

(defn help []
  (println "  List all files and directories that has been changed in the workspace")
  (println "  since a specific point in time.")
  (println)
  (println "  lein polylith diff [ARG] [FLAG]")
  (println "    ARG = (omitted) -> Since last successful build, stored in bookmark")
  (println "                       :last-successful-build in WS-ROOT/.polylith/local.time.")
  (println "          timestamp -> Since the given timestamp (milliseconds since 1970).")
  (println "          bookmark  -> Since the timestamp for the given bookmark in WS-ROOT/.polylith/local.time.")
  (println "    FLAG = +        -> Show time information.")
  (println "                       (the + sign may occur in any order in the argument list).")
  (println)
  (println "   'lein polylith diff 0' can be used to list all files in the workspace.")
  (println)
  (println "  example:")
  (println "    lein polylith diff")
  (println "    lein polylith diff +")
  (println "    lein polylith diff + 1523649477000")
  (println "    lein polylith diff 1523649477000")
  (println "    lein polylith diff 1523649477000 +")
  (println "    lein polylith diff mybookmark"))