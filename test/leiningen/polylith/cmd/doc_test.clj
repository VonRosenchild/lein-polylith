(ns leiningen.polylith.cmd.doc-test
  (:require [clojure.test :refer :all]
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

      (let [lines (helper/split-lines (slurp (str ws-dir "/doc/workspace.html")))]

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
                "<h1>ws1</h1>"
                ""
                "<h3>Libraries</h3>"
                "<div class=\"library\" title=\"1.9.0\">org.clojure/clojure</div>"
                "<p class=\"clear\"/>"
                ""
                "<h3>Interfaces</h3>"
                "<div class=\"interface\" onclick=\"window.location='entities.html#comp-one-interface';\""
                ">comp-one</div>"
                "<div class=\"interface\" onclick=\"window.location='entities.html#email-interface';\""
                ">email</div>"
                "<div class=\"interface\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</div>"
                "<div class=\"interface\" onclick=\"window.location='entities.html#logger-interface';\""
                ">logger</div>"
                "<div class=\"interface\" onclick=\"window.location='entities.html#notadded-interface';\""
                ">notadded</div>"
                "<p class=\"clear\"/>"
                ""
                "<h3>Components</h3>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">interface1</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#email-component';\""
                ">email</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#notadded-component';\""
                ">notadded</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "<p class=\"clear\"/>"
                ""
                "<h3>Bases</h3>"
                "<div class=\"base\" onclick=\"window.location='entities.html#system1-base';\""
                ">system1</div>"
                "<p class=\"clear\"/>"
                ""]
               (take 100 lines)))

        (is (= ["<h3>Environments</h3>"
                "<div class=\"environments\">"
                "  <h4>development:</h4>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">interface1</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#email-component';\""
                ">email</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"\" onclick=\"window.location='entities.html#notadded-component';\""
                ">notadded</div>"
                "    <div class=\"component-ifc\" title=\"\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "    <div class=\"base\" onclick=\"window.location='entities.html#system1-base';\""
                ">system1</div>"
                "  <p class=\"clear\"/>"
                "</div>"
                ""
                "<h3>Systems</h3>"
                "<div class=\"systems\">"
                "  <h4 class=\"missing\">system1:</h4>"
                ""
                "  <a nohref id=\"system1-small-ref\" style=\"cursor:pointer;color:blue;margin-left:10px;\" onClick=\"viewSmallTree('system1')\">S</a>"
                "  <a nohref id=\"system1-medium-ref\" style=\"cursor:pointer;color:blue;margin-left:5px;font-weight:bold;\" onClick=\"viewMediumTree('system1')\">M</a>"
                "  <a nohref id=\"system1-large-ref\" style=\"cursor:pointer;color:blue;margin-left:5px;\" onClick=\"viewLargeTree('system1')\">L</a>"
                "  <p class=\"clear\"/>"
                ""
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"The component 'email' was added to 'system1' but has no references to it in the source code.\" onclick=\"window.location='entities.html#email-component';\""
                ">email</div>"
                "    <div class=\"component-ifc\" title=\"The component 'email' was added to 'system1' but has no references to it in the source code.\" onclick=\"window.location='entities.html#c-interface';\""
                ">&nbsp;</div>"
                "  </div>"
                "  <div class=\"component\">"
                "    <div class=\"component-impl\" title=\"The interface '&nbsp;' is referenced from 'system1' but a component that implements the '&nbsp;' interface also needs to be added to system1', otherwise it will not compile.\" onclick=\"window.location='entities.html#&nbsp;-component';\""
                ">&nbsp;</div>"
                "    <div class=\"component-ifc\" title=\"The interface '&nbsp;' is referenced from 'system1' but a component that implements the '&nbsp;' interface also needs to be added to system1', otherwise it will not compile.\" onclick=\"window.location='entities.html#c-interface';\""
                ">notadded</div>"
                "  </div>"
                "  <p class=\"clear\"/>"
                "  <table id=\"system1-small\" class=\"system-table\" style=\"display:none\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tbase\" colspan=5 onclick=\"window.location='entities.html#system1-base';\""
                ">system1</td>"
                "    </tr>"
                "  </table>"
                "  <table id=\"system1-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"]
               (take 100 (drop 100 lines))))

        (is (= ["      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tbase\" colspan=5 onclick=\"window.location='entities.html#system1-base';\""
                ">system1</td>"
                "    </tr>"
                "  </table>"
                "  <table id=\"system1-large\" class=\"system-table\" style=\"display:none\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#-component';\""
                "></td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tbase\" colspan=5 onclick=\"window.location='entities.html#system1-base';\""
                ">system1</td>"
                "    </tr>"
                "  </table>"
                "</div>"
                "</body>"
                "</html>"]
               (drop 200 lines)))

        (is (= [""
                ""
                "<!DOCTYPE html>"
                "<html>"
                "<head>"
                "<title>ws1 (entities)</title>"
                ""
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">"
                ""
                "</head>"
                "<body>"
                ""
                "<img src=\"../logo.png\" alt=\"Polylith\" style=\"width:200px;\">"
                ""
                "<h1>Interfaces</h1>"
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
                "<h1>Components</h1>"
                "  <a id=\"comp-one-component\"/>"
                "  <h3>comp-one</h3>"
                "  <table id=\"comp-one-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tinterface-top\" onclick=\"window.location='entities.html#logger-interface';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tcomponent\" onclick=\"window.location='entities.html#comp-one-component';\""
                ">comp-one</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface-bottom\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "  </table>"
                "  <a id=\"component2-component\"/>"
                "  <h3>component2</h3>"
                "  <table id=\"component2-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#component2-component';\""
                ">component2</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface-bottom\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</td>"
                "    </tr>"
                "  </table>"
                "  <a id=\"email-component\"/>"
                "  <h3>email</h3>"
                "  <table id=\"email-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#email-component';\""
                ">email</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface-bottom\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "  </table>"
                "  <a id=\"logger-component\"/>"
                "  <h3>logger</h3>"
                "  <table id=\"logger-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#logger-component';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface-bottom\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "  </table>"
                "  <a id=\"notadded-component\"/>"
                "  <h3>notadded</h3>"
                "  <table id=\"notadded-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tcomponent top\" onclick=\"window.location='entities.html#notadded-component';\""
                ">notadded</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tinterface-bottom\" onclick=\"window.location='entities.html#&nbsp;-interface';\""
                ">&nbsp;</td>"
                "    </tr>"
                "  </table>"
                ""
                "<h1>Bases</h1>"
                "  <a id=\"system1-base\"/>"
                "  <h3>system1</h3>"
                "  <table id=\"system1-medium\" class=\"system-table\">"
                "    <tr>"
                "      <td class=\"tinterface-top\" onclick=\"window.location='entities.html#notadded-interface';\""
                ">notadded</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface-top\" onclick=\"window.location='entities.html#comp-one-interface';\""
                ">comp-one</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface-top\" onclick=\"window.location='entities.html#interface1-interface';\""
                ">interface1</td>"
                "      <td class=\"spc\"></td>"
                "      <td class=\"tinterface-top\" onclick=\"window.location='entities.html#logger-interface';\""
                ">logger</td>"
                "    </tr>"
                "    <tr>"
                "      <td class=\"tbase\" colspan=7 onclick=\"window.location='entities.html#system1-base';\""
                ">system1</td>"
                "    </tr>"
                "  </table>"
                ""
                "</body>"
                "</html>"]
               (helper/split-lines (slurp (str ws-dir "/doc/entities.html")))))))))
