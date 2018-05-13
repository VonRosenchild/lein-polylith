(ns leiningen.polylith.cmd.test
  (:require [clojure.string :as str]
            [leiningen.polylith.cmd.info :as info]
            [leiningen.polylith.file :as file]
            [leiningen.polylith.cmd.diff :as diff]
            [leiningen.polylith.time :as time]
            [leiningen.polylith.cmd.shared :as shared]))

(defn show-tests [tests]
  (if (empty? tests)
    (println "echo 'Nothing changed - no tests executed'")
    (println (str "lein test " (str/join " " tests)))))

(defn run-tests [test-namespaces ws-path]
  (if (zero? (count test-namespaces))
    (println "Nothing to test")
    (do
      (println "Start execution of tests in" (count test-namespaces) "namespaces:")
      (show-tests test-namespaces)
      (println (apply shared/sh (concat ["lein" "test"] test-namespaces [:dir (str ws-path "/environments/development")]))))))

(defn base-path [ws-path top-dir base]
  (let [tests-dir (str ws-path "/environments/development/tests/")
        test (str "test-" base)]
    (str (shared/base-source-path top-dir tests-dir test) "/" base)))

(defn component-path [ws-path top-dir component]
  (let [dir (shared/full-name top-dir "/" (shared/src-dir-name component))]
    (str ws-path "/environments/development/tests/test/" dir)))

(defn path->ns [path]
  (second (first (file/read-file path))))

(defn ->tests [ws-path top-dir bases base-or-component]
  (let [path (if (contains? bases base-or-component)
               (base-path ws-path top-dir base-or-component)
               (component-path ws-path top-dir base-or-component))
        paths (map second (file/paths-in-dir path))]
    (map path->ns paths)))

(defn tests [ws-path top-dir bases changed-entities]
  (mapcat #(->tests ws-path top-dir bases %) changed-entities))

(defn all-test-namespaces [ws-path top-dir timestamp]
  (let [bases (shared/all-bases ws-path)
        paths (mapv second (diff/do-diff ws-path timestamp))
        changed-bases (info/changed-bases ws-path paths)
        changed-components (info/changed-components ws-path paths)
        indirect-changed-entities (info/all-indirect-changes ws-path top-dir paths)
        changed-entities (set (concat changed-components changed-bases indirect-changed-entities))
        entity-tests (tests ws-path top-dir bases changed-entities)]
     (vec (sort (map str entity-tests)))))

(defn execute [ws-path top-dir args]
  (let [[_ timestamp] (time/parse-time-args ws-path args)
        tests (all-test-namespaces ws-path top-dir timestamp)]
    (run-tests tests ws-path)))
