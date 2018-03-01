(ns leiningen.polylith.cmd.create-component-test
  (:require [clojure.test :refer :all]
            [leiningen.polylith.cmd.diff]
            [leiningen.polylith.cmd.test-helper :as helper]
            [leiningen.polylith :as polylith]
            [leiningen.polylith.file :as file]))

(use-fixtures :each helper/test-setup-and-tear-down)

(defn interfaces-project-content [name]
  [['defproject name "1.0"
    :description "Component interfaces"
    :dependencies [['org.clojure/clojure "1.9.0"]
                   ['org.clojure/spec.alpha "0.1.143"]]
    :aot
    :all]])

(defn interfaces-interface-content [ns-name]
  [['ns ns-name]
   ['defn 'myfn ['x]]])

(defn src-core-content [ns-name]
  [['ns ns-name]
   ['defn 'myfn ['x]
    ['+ '2 'x]]])

(defn comp1-core-test [ns-name require-ns]
  [['ns ns-name
    [:require ['clojure.test :refer :all]
     [require-ns :as 'core]]]
   ['deftest 'test-myfn
    ['is ['= 42 ['core/myfn 40]]]]])

(defn src-interface-content [ns-name require-ns]
  [['ns ns-name
    [:require [require-ns :as 'core]]]
   ['defn 'myfn ['x]
    ['core/myfn 'x]]])

(defn development-project-content [ns-name]
  [['defproject ns-name "1.0"
    :description "The main development environment"
    :dependencies [['org.clojure/clojure "1.9.0"]
                   ['org.clojure/spec.alpha "0.1.143"]]]])

(defn workspace-project-content [ns-name top-ns top-dir]
  [['defproject ns-name "1.0"
    :description "The workspace"
    :plugins [['polylith/lein-polylith "0.0.35-alpha"]]
    :polylith {:build-tool           "leiningen"
               :clojure-spec-version "org.clojure/spec.alpha 0.1.143"
               :clojure-version      "1.9.0"
               :example-hash1        "2c851f3c6e7a5114cecf6bdd6e1c8c8aec8b32c1"
               :example-hash2        "58cd8b3106c942f372a40616fe9155c9d2efd122"
               :ignored-tests        []
               :top-ns               top-ns
               :top-dir              top-dir
               :vcs                  "git"}]])

(defn comp1-project-content [ns-name interfaces]
  [['defproject ns-name "0.1"
    :description "A comp1 component"
    :dependencies [[interfaces "1.0"]
                   ['org.clojure/clojure "1.9.0"]
                   ['org.clojure/spec.alpha "0.1.143"]]
    :aot
    :all]])

(deftest polylith-create--create-component--creates-component-with-namespace
  (with-redefs [file/current-path (fn [] @helper/root-dir)
                leiningen.polylith.cmd.diff/diff (fn [_ _ _] helper/diff)]
    (let [ws-dir (str @helper/root-dir "/ws1")]

      (polylith/polylith nil "create" "w" "ws1" "my.company")
      (polylith/polylith (helper/settings ws-dir "my.company" "my/company")
                         "create" "c" "comp1")

      (is (= ["interfaces/src/my/company/comp1/interface.clj"
              "interfaces/src/my/company/comp1"
              "interfaces/src/my/company"
              "interfaces/src/my"
              "interfaces/src"
              "interfaces/project.clj"
              "interfaces"
              "systems"
              "components/comp1/src/my/company/comp1/interface.clj"
              "components/comp1/src/my/company/comp1/core.clj"
              "components/comp1/src/my/company/comp1"
              "components/comp1/src/my/company"
              "components/comp1/src/my"
              "components/comp1/src"
              "components/comp1/Readme.md"
              "components/comp1/resources/comp1"
              "components/comp1/resources"
              "components/comp1/test/my/company/comp1/core_test.clj"
              "components/comp1/test/my/company/comp1"
              "components/comp1/test/my/company"
              "components/comp1/test/my"
              "components/comp1/test"
              "components/comp1/project.clj"
              "components/comp1"
              "components"
              "bases"
              "environments/development/src/my/company/comp1/interface.clj"
              "environments/development/src/my/company/comp1/core.clj"
              "environments/development/src/my/company/comp1"
              "environments/development/src/my/company"
              "environments/development/src/my"
              "environments/development/src"
              "environments/development/interfaces/my/company/comp1/interface.clj"
              "environments/development/interfaces/my/company/comp1"
              "environments/development/interfaces/my/company"
              "environments/development/interfaces/my"
              "environments/development/interfaces"
              "environments/development/docs/comp1-Readme.md"
              "environments/development/docs"
              "environments/development/project-files/workspace-project.clj"
              "environments/development/project-files/interfaces-project.clj"
              "environments/development/project-files/systems"
              "environments/development/project-files/components/comp1-project.clj"
              "environments/development/project-files/components"
              "environments/development/project-files/bases"
              "environments/development/project-files"
              "environments/development/resources/comp1"
              "environments/development/resources"
              "environments/development/test/my/company/comp1/core_test.clj"
              "environments/development/test/my/company/comp1"
              "environments/development/test/my/company"
              "environments/development/test/my"
              "environments/development/test"
              "environments/development/project.clj"
              "environments/development"
              "environments"
              "project.clj"]
             (file/files ws-dir)))

      (is (= (interfaces-interface-content 'my.company.comp1.interface)
             (helper/content ws-dir "interfaces/src/my/company/comp1/interface.clj")))

      (is (= (interfaces-project-content 'my.company/interfaces)
             (helper/content ws-dir "interfaces/project.clj")))

      (is (= (src-interface-content 'my.company.comp1.interface 'my.company.comp1.core)
             (helper/content ws-dir "components/comp1/src/my/company/comp1/interface.clj")))

      (is (= (src-core-content 'my.company.comp1.core)
             (helper/content ws-dir "components/comp1/src/my/company/comp1/core.clj")))

      (is (= (comp1-core-test 'my.company.comp1.core-test 'my.company.comp1.core)
             (helper/content ws-dir "components/comp1/test/my/company/comp1/core_test.clj")))

      (is (= (src-interface-content 'my.company.comp1.interface 'my.company.comp1.core)
             (helper/content ws-dir "environments/development/src/my/company/comp1/interface.clj")))

      (is (= (src-core-content 'my.company.comp1.core)
             (helper/content ws-dir "environments/development/src/my/company/comp1/core.clj")))

      (is (= (interfaces-interface-content 'my.company.comp1.interface)
             (helper/content ws-dir "environments/development/interfaces/my/company/comp1/interface.clj")))

      (is (= (workspace-project-content 'my.company/development "my.company" "my/company")
             (helper/content ws-dir "environments/development/project-files/workspace-project.clj")))

      (is (= (interfaces-project-content 'my.company/interfaces)
             (helper/content ws-dir "environments/development/project-files/interfaces-project.clj")))

      (is (= (comp1-project-content 'my.company/comp1 'my.company/interfaces)
             (helper/content ws-dir "environments/development/project-files/components/comp1-project.clj")))

      (is (= (comp1-core-test 'my.company.comp1.core-test 'my.company.comp1.core)
             (helper/content ws-dir "environments/development/test/my/company/comp1/core_test.clj")))

      (is (= (development-project-content 'my.company/development)
             (helper/content ws-dir "environments/development/project.clj")))

      (is (= (workspace-project-content 'my.company/development "my.company" "my/company")
             (helper/content ws-dir "project.clj"))))))

(deftest polylith-create--create-component--creates-component-without-namespace
  (with-redefs [file/current-path (fn [] @helper/root-dir)
                leiningen.polylith.cmd.diff/diff (fn [_ _ _] helper/diff)]
    (let [ws-dir (str @helper/root-dir "/ws1")]

      (polylith/polylith nil "create" "w" "ws1" "")
      (polylith/polylith (helper/settings ws-dir "" "") "create" "c" "comp1")

      (is (= ["interfaces/src/comp1/interface.clj"
              "interfaces/src/comp1"
              "interfaces/src"
              "interfaces/project.clj"
              "interfaces"
              "systems"
              "components/comp1/src/comp1/interface.clj"
              "components/comp1/src/comp1/core.clj"
              "components/comp1/src/comp1"
              "components/comp1/src"
              "components/comp1/Readme.md"
              "components/comp1/resources/comp1"
              "components/comp1/resources"
              "components/comp1/test/comp1/core_test.clj"
              "components/comp1/test/comp1"
              "components/comp1/test"
              "components/comp1/project.clj"
              "components/comp1"
              "components"
              "bases"
              "environments/development/src/comp1/interface.clj"
              "environments/development/src/comp1/core.clj"
              "environments/development/src/comp1"
              "environments/development/src"
              "environments/development/interfaces/comp1/interface.clj"
              "environments/development/interfaces/comp1"
              "environments/development/interfaces"
              "environments/development/docs/comp1-Readme.md"
              "environments/development/docs"
              "environments/development/project-files/workspace-project.clj"
              "environments/development/project-files/interfaces-project.clj"
              "environments/development/project-files/systems"
              "environments/development/project-files/components/comp1-project.clj"
              "environments/development/project-files/components"
              "environments/development/project-files/bases"
              "environments/development/project-files"
              "environments/development/resources/comp1"
              "environments/development/resources"
              "environments/development/test/comp1/core_test.clj"
              "environments/development/test/comp1"
              "environments/development/test"
              "environments/development/project.clj"
              "environments/development"
              "environments"
              "project.clj"]
             (file/files ws-dir)))

      (is (= (interfaces-interface-content 'comp1.interface)
             (helper/content ws-dir "interfaces/src/comp1/interface.clj")))

      (is (= (interfaces-project-content 'interfaces)
             (helper/content ws-dir "interfaces/project.clj")))

      (is (= (src-interface-content 'comp1.interface 'comp1.core)
             (helper/content ws-dir "components/comp1/src/comp1/interface.clj")))

      (is (= (src-core-content 'comp1.core)
             (helper/content ws-dir "components/comp1/src/comp1/core.clj")))

      (is (= (comp1-core-test 'comp1.core-test 'comp1.core)
             (helper/content ws-dir "components/comp1/test/comp1/core_test.clj")))

      (is (= (src-interface-content 'comp1.interface 'comp1.core)
             (helper/content ws-dir "environments/development/src/comp1/interface.clj")))

      (is (= (src-core-content 'comp1.core)
             (helper/content ws-dir "environments/development/src/comp1/core.clj")))

      (is (= (interfaces-interface-content 'comp1.interface)
             (helper/content ws-dir "environments/development/interfaces/comp1/interface.clj")))

      (is (= (workspace-project-content 'development "" "")
             (helper/content ws-dir "environments/development/project-files/workspace-project.clj")))

      (is (= (interfaces-project-content 'interfaces)
             (helper/content ws-dir "environments/development/project-files/interfaces-project.clj")))

      (is (= (comp1-project-content 'comp1 'interfaces)
             (helper/content ws-dir "environments/development/project-files/components/comp1-project.clj")))

      (is (= (comp1-core-test 'comp1.core-test 'comp1.core)
             (helper/content ws-dir "environments/development/test/comp1/core_test.clj")))

      (is (= (development-project-content 'development)
             (helper/content ws-dir "environments/development/project.clj")))

      (is (= (workspace-project-content 'development "" "")
             (helper/content ws-dir "project.clj"))))))
