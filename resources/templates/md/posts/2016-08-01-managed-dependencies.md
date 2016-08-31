{:title "Managed Dependencies"
:layout :post
:excerpt "tl;dr: ***"
:tags  ["clojure" "test" "dependencies" "managed-dependencies" "classpath" "repl"
  "profiles" "scope" "lein" "leiningen"]
:toc true
:draft? true}

## Context

CANCELLED parce que repl-tasks not used anymore

https://github.com/technomancy/leiningen/blob/stable/doc/MANAGED_DEPS.md

Plutôt que test scope avec lein,
utiliser les profiles (dev en l'occurrence) ?


Attention avec cette fonctionnalité, toutes les 
màj de librairies sont upgradées d'un coup lorsqu'on
change la version du parent,
donc il faut faire très gaffe aux impacts!

En plus, on perd les numéros de version qui ont permis la construction d'un
enfant lorsque le parent est upgradé, sauf à aller voir dans l'historique git
du parent.

+ màj de repl-tasks


```clojure
# parent project
(defproject acs/parent "1.0.0.0.0"
  :managed-dependencies [[midje/midje                            "1.8.3" :scope "test"]]
  )

# child

:parent-project [:coord [acs/parent "1.0.0.0.0"]
:inherits [:managed-dependencies]]
  :managed-dependencies [[midje/midje                            "1.8.3" :scope "test"]]  
```

```clojure
import dep:
                 [leiningen-core
                  #=(leiningen.core.main/leiningen-version)]

(defn rien []
  (let [pmap (leiningen.core.project/read "project.clj")
        ff (fn [dep] (as-> dep $$
                           (drop-while #(not= :scope %) $$)
                           (second $$)
                           (or $$ "")
                           (name $$)
                           (not= "test" $$)))]
    ; filter project's dependencies before getting classpath.
    (:dependencies (update pmap
                           :dependencies
                           (fn [deps mdeps]
                             (filter (fn [dep]
                                       ;(run! println (filter #(= (name (first #spy/d dep)) (name (first %))) mdeps))
                                       (and
                                         ; neither a managed-dep exists with this name and is test-scoped
                                         #spy/d (ff (first (filter #(= (first dep) (first %)) mdeps)))
                                         ; nor scope=test in 'standard' dependencies map
                                         #spy/d (ff dep)))
                                     deps))
                           (:managed-dependencies pmap)))))

(require '[leiningen.core.classpath :as lcc])
(require '[leiningen.core.main :refer [leiningen-version]])

(defn classpath-without-test-deps []
  (let [pmap (leiningen.core.project/read "project.clj")]
    (leiningen.core.classpath/get-classpath
      ; filter project's dependencies before getting classpath.
      (update pmap
              :dependencies
              (fn [deps]
                (filter (fn [dep]
                          (as-> dep $$
                                (drop-while #(not= :scope %) $$)
                                (second $$)
                                (or $$ "")
                                (name $$)
                                (not= "test" $$))) deps))))))
```                               