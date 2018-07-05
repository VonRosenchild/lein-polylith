(ns leiningen.polylith.cmd.help.build)

(defn help []
  (println "  Build system artifacts.")
  (println)
  (println "  The following steps are performed:")
  (println "    - calculates what components and bases to build based on what has")
  (println "      changed since the last successful build.")
  (println "    - calls 'sync-deps' and makes sure that all dependencies in project.clj")
  (println "      files are in sync.")
  (println "    - AOT compile changed components, bases and systems to check that they compile")
  (println "      against the interfaces.")
  (println "    - run tests for all bases and components that have been affected by the changes.")
  (println "    - executes build.sh for all changed systems.")
  (println "    - if the entire build is successful, then execute the success command")
  (println "      that updates the time for last successful build.")
  (println)
  (println "  lein polylith build [ARG] [SKIP]")
  (println "    ARG = (omitted) -> Since last successful build, stored in bookmark")
  (println "                       :last-successful-build in WS-ROOT/.polylith/time.edn")
  (println "                       or :last-successful-build in WS-ROOT/.polylith/git.edn")
  (println "                       if you have CI variable set to something on the machine.")
  (println "          timestamp -> Since the given timestamp (milliseconds since 1970).")
  (println "          git-hash  -> Since the given git hash if the CI variable is set.")
  (println "          bookmark  -> Since the timestamp for the given bookmark in")
  (println "                       WS-ROOT/.polylith/time.edn or since the git hash")
  (println "                       for the given bookmark in WS-ROOT/.polylith/git.edn")
  (println "                       if the CI variable is set.")
  (println)
  (println "    SKIP = (omitted)  -> Compiles, tests, builds, and sets :last-successful-build")
  (println "           -sync-deps -> Skips dependency sync step")
  (println "           -compile   -> Skips compilation step")
  (println "           -test      -> Skips test step")
  (println "           -success   -> Skips success step")
  (println)
  (println "  examples:")
  (println "    lein polylith build")
  (println "    lein polylith build -compile")
  (println "    lein polylith build 1523649477000")
  (println "    lein polylith build 7d7fd132412aad0f8d3019edfccd1e9d92a5a8ae")
  (println "    lein polylith build mybookmark")
  (println "    lein polylith build 1523649477000 -compile -test"))

