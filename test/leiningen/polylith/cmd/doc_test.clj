(ns leiningen.polylith.cmd.doc-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pp]
            [leiningen.polylith.file :as file]
            [leiningen.polylith.cmd.test-helper :as helper]
            [leiningen.polylith :as polylith]
            [clojure.string :as str]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest polylith-doc--with-an-empty-workspace--do-nothing
  (with-redefs [file/current-path (fn [] @helper/root-dir)]
    (let [ws-dir (str @helper/root-dir "/ws1")
          project (helper/settings ws-dir "")
          output (with-out-str
                   (polylith/polylith nil "create" "w" "ws1" "" "-git")
                   (polylith/polylith project "doc" "-browse"))]

      (is (= [""]
             (helper/split-lines output))))))

(deftest polylith-doc--with-system--print-table
  (with-redefs [file/current-path (fn [] @helper/root-dir)]
    (let [ws-dir (str @helper/root-dir "/ws1")
          project (helper/settings ws-dir "")
          sys1-content ["(ns system1.core"
                        "  (:require [comp-one.interface :as comp-one]"
                        "            [interface1.interface :as component2]"
                        "            [logger.interface :as logger]"
                        "            [notadded.interface :as notadded])"
                        "  (:gen-class))"
                        "(defn -main [& args]"
                        "  (comp-one/add-two 10)"
                        "  (component2/add-two 10)"
                        "  (logger/add-two 10)"
                        "  (notadded/add-two 10)"
                        "  (println \"Hello world!\"))"]

          comp1-content ["(ns comp-one.core"
                         "  (:require [logger.interface :as logger]))"
                         "(defn add-two [x]\n  (logger/add-two x))"]]
      (polylith/polylith nil "create" "w" "ws1" "" "-git")
      (polylith/polylith project "create" "s" "system1")
      (polylith/polylith project "create" "c" "comp-one")
      (polylith/polylith project "create" "c" "component2" "interface1")
      (polylith/polylith project "create" "c" "logger")
      (polylith/polylith project "create" "c" "email")
      (polylith/polylith project "create" "c" "notadded")
      (polylith/polylith project "add" "comp-one" "system1")
      (polylith/polylith project "add" "component2" "system1")
      (polylith/polylith project "add" "logger" "system1")
      (polylith/polylith project "add" "email" "system1")
      (file/replace-file! (str ws-dir "/systems/system1/src/system1/core.clj") sys1-content)
      (file/replace-file! (str ws-dir "/components/comp-one/src/comp_one/core.clj") comp1-content)
      (polylith/polylith project "doc" "-browse")

      ;(pp/pprint (helper/split-lines (slurp (str ws-dir "/doc/workspace.html"))))

      (is (= [""
              ""
              "<!DOCTYPE html>"
              "<html>"
              "<head>"
              "<title>ws1 (workspace)</title>"
              ""
              "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">"
              ""
              "</head>"
              "<body>"
              ""
              "<script>"
              "function viewSmallTree(system) {"
              "    document.getElementById(system + \"-medium\").style.display = \"none\";"
              "    document.getElementById(system + \"-large\").style.display = \"none\";"
              "    document.getElementById(system + \"-small\").style.display = \"block\";"
              "    document.getElementById(system + \"-small-ref\").style.fontWeight = \"bold\";"
              "    document.getElementById(system + \"-medium-ref\").style.fontWeight = \"normal\";"
              "    document.getElementById(system + \"-large-ref\").style.fontWeight = \"normal\";"
              "}"
              ""
              "function viewMediumTree(system) {"
              "    document.getElementById(system + \"-small\").style.display = \"none\";"
              "    document.getElementById(system + \"-large\").style.display = \"none\";"
              "    document.getElementById(system + \"-medium\").style.display = \"block\";"
              "    document.getElementById(system + \"-small-ref\").style.fontWeight = \"normal\";"
              "    document.getElementById(system + \"-medium-ref\").style.fontWeight = \"bold\";"
              "    document.getElementById(system + \"-large-ref\").style.fontWeight = \"normal\";"
              "}"
              ""
              "function viewLargeTree(system) {"
              "    document.getElementById(system + \"-small\").style.display = \"none\";"
              "    document.getElementById(system + \"-medium\").style.display = \"none\";"
              "    document.getElementById(system + \"-large\").style.display = \"block\";"
              "    document.getElementById(system + \"-small-ref\").style.fontWeight = \"normal\";"
              "    document.getElementById(system + \"-medium-ref\").style.fontWeight = \"normal\";"
              "    document.getElementById(system + \"-large-ref\").style.fontWeight = \"bold\";"
              "}"
              "</script>"
              ""
              "<img src=\"../logo.png\" alt=\"Polylith\" style=\"width:200px;\">"
              ""
              "<p class=\"clear\"/>"
              "    <h3>ws1</h3>"
              "  <div style=\"margin-left: 10px;\">A Polylith workspace.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              ""
              "<h1>Libraries</h1>"
              "<table class=\"entity-table\">"
              "  <tr>"
              "    <td></td>"
              "    <td class=\"library-header\"><span class=\"vertical-text\">org.clojure/clojure&nbsp;&nbsp;1.9.0</div></td>"
              "  </tr>"
              "    <tr>"
              "      <td class=\"component-header\" title=\"A comp-one component.\">comp&#8209;one</td>"
              "      <td class=\"center component-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"component-header\" title=\"A component2 component.\">component2</td>"
              "      <td class=\"center component-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"component-header\" title=\"A email component.\">email</td>"
              "      <td class=\"center component-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"component-header\" title=\"A logger component.\">logger</td>"
              "      <td class=\"center component-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"component-header\" title=\"A notadded component.\">notadded</td>"
              "      <td class=\"center component-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"base-header\" title=\"A system1 base.\">system1</td>"
              "      <td class=\"center base-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"environment-header\" title=\"The main development environment.\">development</td>"
              "      <td class=\"center environment-row\">&#10003;"
              "</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"system-header\" title=\"A system1 system.\">system1</td>"
              "      <td class=\"center system-row\">&#10003;"
              "</td>"
              "    </tr>"
              "</table>"
              ""
              "<h1>Components & bases</h1>"
              ""
              "<table class=\"entity-table\">"
              "  <tr>"
              "    <td></td>"
              "    <td class=\"environment-header\" title=\"The main development environment.\"><span class=\"vertical-text\">development</span></td>"
              "    <td class=\"system-header\" title=\"A system1 system.\"><span class=\"vertical-text\">system1</span></td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"component-header\" title=\"A comp-one component.\">comp-one</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"component-header\" title=\"A component2 component.\">component2</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"component-header\" title=\"A email component.\">email</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "    <td class=\"center component-row\">"
              "</td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"component-header\" title=\"A logger component.\">logger</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"component-header\" title=\"A notadded component.\">notadded</td>"
              "    <td class=\"center component-row\">&#10003;"
              "</td>"
              "    <td class=\"center component-row\">"
              "</td>"
              "  </tr>"
              "  <tr>"
              "    <td class=\"base-header\" title=\"A system1 base.\">system1</td>"
              "    <td class=\"center base-row\">&#10003;"
              "</td>"
              "    <td class=\"center base-row\">&#10003;"
              "</td>"
              "  </tr>"
              "</table>"
              ""
              "<h3>Environments</h3>"
              "<div class=\"environments\">"
              "    <h3>development</h3>"
              "  <div style=\"margin-left: 10px;\">The main development environment.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='#comp-one-component';\""
              ">comp-one</div>"
              "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='#comp-one-interface';\""
              ">&nbsp;</div>"
              "  </div>"
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='#component2-component';\""
              ">component2</div>"
              "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='#component2-interface';\""
              ">interface1</div>"
              "  </div>"
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='#email-component';\""
              ">email</div>"
              "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='#email-interface';\""
              ">&nbsp;</div>"
              "  </div>"
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='#logger-component';\""
              ">logger</div>"
              "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</div>"
              "  </div>"
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='#notadded-component';\""
              ">notadded</div>"
              "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='#notadded-interface';\""
              ">&nbsp;</div>"
              "  </div>"
              "    <div class=\"base\" onclick=\"window.location='#system1-base';\""
              ">system1</div>"
              "  <p class=\"clear\"/>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "</div>"
              ""
              "<h3>Systems</h3>"
              "<div class=\"systems\">"
              "    <h3>system1</h3>"
              "  <div style=\"margin-left: 10px;\">A system1 system.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              ""
              "  <p class=\"clear\"/>"
              "  <a nohref id=\"system1-small-ref\" style=\"cursor:pointer;color:blue;margin-left:10px;\" onClick=\"viewSmallTree('system1')\">S</a>"
              "  <a nohref id=\"system1-medium-ref\" style=\"cursor:pointer;color:blue;margin-left:5px;font-weight:bold;\" onClick=\"viewMediumTree('system1')\">M</a>"
              "  <a nohref id=\"system1-large-ref\" style=\"cursor:pointer;color:blue;margin-left:5px;\" onClick=\"viewLargeTree('system1')\">L</a>"
              "  <p class=\"tiny-clear\"/>"
              ""
              "  <div class=\"component\">"
              "    <div class=\"component-impl\" title=\"The component 'email' was added to 'system1' but has no references to it in the source code.\" onclick=\"window.location='#email-component';\""
              ">email</div>"
              "    <div class=\"component-ifc\" title=\"The component 'email' was added to 'system1' but has no references to it in the source code.\" onclick=\"window.location='#email-interface';\""
              ">&nbsp;</div>"
              "  </div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"system1-small\" class=\"system-table\" style=\"display:none\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#comp-one-component';\""
              ">comp-one</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#component2-component';\""
              ">component2</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface\" onclick=\"window.location='#comp-one-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#interface1-interface';\""
              ">interface1</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#notadded-interface';\""
              ">notadded</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tbase\" colspan=7 onclick=\"window.location='#system1-base';\""
              ">system1</td>"
              "    </tr>"
              "  </table>"
              "  <table id=\"system1-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#comp-one-component';\""
              ">comp-one</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#component2-component';\""
              ">component2</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface\" onclick=\"window.location='#comp-one-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#interface1-interface';\""
              ">interface1</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#notadded-interface';\""
              ">notadded</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tbase\" colspan=7 onclick=\"window.location='#system1-base';\""
              ">system1</td>"
              "    </tr>"
              "  </table>"
              "  <table id=\"system1-large\" class=\"system-table\" style=\"display:none\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#comp-one-component';\""
              ">comp-one</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#component2-component';\""
              ">component2</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#-component';\""
              "></td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface\" onclick=\"window.location='#comp-one-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#interface1-interface';\""
              ">interface1</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#notadded-interface';\""
              ">notadded</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tbase\" colspan=7 onclick=\"window.location='#system1-base';\""
              ">system1</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "</div>"
              ""
              "<h2>Interfaces</h1>"
              "  <a id=\"comp-one-interface\"/>"
              "  <h3>comp-one</h3>"
              "  <div class=\"interface\">comp-one</div>"
              "  <p class=\"clear\"/>"
              "  <a id=\"email-interface\"/>"
              "  <h3>email</h3>"
              "  <div class=\"interface\">email</div>"
              "  <p class=\"clear\"/>"
              "  <a id=\"interface1-interface\"/>"
              "  <h3>interface1</h3>"
              "  <div class=\"interface\">interface1</div>"
              "  <p class=\"clear\"/>"
              "  <a id=\"logger-interface\"/>"
              "  <h3>logger</h3>"
              "  <div class=\"interface\">logger</div>"
              "  <p class=\"clear\"/>"
              "  <a id=\"notadded-interface\"/>"
              "  <h3>notadded</h3>"
              "  <div class=\"interface\">notadded</div>"
              "  <p class=\"clear\"/>"
              ""
              "<h2>Components</h2>"
              "  <a id=\"comp-one-component\"/>"
              "    <h3>comp-one</h3>"
              "  <div style=\"margin-left: 10px;\">A comp-one component.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"comp-one-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#logger-interface';\""
              ">logger</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tcomponent\" onclick=\"window.location='#comp-one-component';\""
              ">comp-one</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface-bottom\" onclick=\"window.location='#comp-one-interface';\""
              ">&nbsp;</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              "  <a id=\"component2-component\"/>"
              "    <h3>component2</h3>"
              "  <div style=\"margin-left: 10px;\">A component2 component.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"component2-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#component2-component';\""
              ">component2</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface-bottom\" onclick=\"window.location='#interface1-interface';\""
              ">interface1</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              "  <a id=\"email-component\"/>"
              "    <h3>email</h3>"
              "  <div style=\"margin-left: 10px;\">A email component.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"email-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#email-component';\""
              ">email</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface-bottom\" onclick=\"window.location='#email-interface';\""
              ">&nbsp;</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              "  <a id=\"logger-component\"/>"
              "    <h3>logger</h3>"
              "  <div style=\"margin-left: 10px;\">A logger component.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"logger-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#logger-component';\""
              ">logger</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface-bottom\" onclick=\"window.location='#logger-interface';\""
              ">&nbsp;</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              "  <a id=\"notadded-component\"/>"
              "    <h3>notadded</h3>"
              "  <div style=\"margin-left: 10px;\">A notadded component.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"notadded-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tcomponent top\" onclick=\"window.location='#notadded-component';\""
              ">notadded</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tinterface-bottom\" onclick=\"window.location='#notadded-interface';\""
              ">&nbsp;</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              ""
              "<h2>Bases</h2>"
              "  <a id=\"system1-base\"/>"
              "    <h3>system1</h3>"
              "  <div style=\"margin-left: 10px;\">A system1 base.<br></div>"
              "  <p class=\"tiny-clear\"/>"
              "  <table id=\"system1-medium\" class=\"system-table\">"
              "    <tr>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#notadded-interface';\""
              ">notadded</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#comp-one-interface';\""
              ">comp-one</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#interface1-interface';\""
              ">interface1</td>"
              "      <td class=\"spc\"></td>"
              "      <td class=\"tinterface-top\" onclick=\"window.location='#logger-interface';\""
              ">logger</td>"
              "    </tr>"
              "    <tr>"
              "      <td class=\"tbase\" colspan=7 onclick=\"window.location='#system1-base';\""
              ">system1</td>"
              "    </tr>"
              "  </table>"
              "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
              "<p class=\"clear\"/>"
              "  <p class=\"tiny-clear\"/>"
              ""
              "</body>"
              "</html>"]
             (helper/split-lines (slurp (str ws-dir "/doc/workspace.html"))))))))
