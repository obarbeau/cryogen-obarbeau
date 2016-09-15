{:title "Notes on Onyx tutorial"
:layout :post
:excerpt "tl;dr: ***"
:tags  ["clojure"]
:toc true
:draft? true}

## Context

j'ai fini le tutorial Sans avoir besoin de tricher, Ce qui,
compte tenu de l'impatience qui caractérise tout bon parisien,
même d'adoption comme moi, prouve non pas que je suis très intelligent,
mais que le tutoriel est très bien construit et suffisamment progressif,
comme la musique qui me fait headbanger.

Le workflow se définit très simplement,
et de la même manière que les graphes Graphviz/dot, tels qu'ils sont écrits
avec l'outil [dorothy][1].

maps at each steps = segments ~= data flowing through cluster.
catalog = {} in, out, fns ie set up the context for fns that will execute on
  cluster.

<https://github.com/onyx-platform/onyx-dashboard>

```bash
# 1) avec docker image. NE fonctionne PAS car les JS ne sont pas vus...
sudo systemctl start docker.service
docker run -p 3000:3000 onyx/onyx-dashboard:latest "<IP-host-vue-par-docker0>:2188"

# 2) from sources
to.clj.tools; cd onyx-dashboard
# build onyx dashboard
# virer les trucs qui coincent dans project.clj :clean-targets
lein uberjar
# java -server -jar target/onyx-dashboard.jar "127.0.0.1:2188"
java -server -jar /opt/jad/onyx-dashboard.jar "127.0.0.1:2188"
```

```clojure
(reset)
(ns onyx.test-helper)
(require '[workshop.workshop-utils :as u])
(def ote (map->OnyxTestEnv {:n-peers 3 :env-config (u/load-env-config #uuid "d2afdc5a-d8e3-476f-b6de-c214ef0234ad") :peer-config (u/load-peer-config #uuid "d2afdc5a-d8e3-476f-b6de-c214ef0234ad")}))
(component/start ote)
(require '[workshop.challenge-0-0 :as c])
(def lc (c/build-lifecycles))
(onyx.api/submit-job (u/load-peer-config #uuid "d2afdc5a-d8e3-476f-b6de-c214ef0234ad") {:workflow c/workflow :catalog (c/build-catalog) :lifecycles lc :task-scheduler :onyx.task-scheduler/balanced})
(u/bind-inputs! lc {:read-segments [{:sentence "Getting started with Onyx is easy"}]})
(u/collect-outputs! lc [:write-segments])
(component/stop ote)
```

```clojure
; dans learn-onyx
(require '[workshop.challenge-0-0 :as c] '[workshop.workshop-utils :as u]
  '[com.stuartsierra.component :as component] '[onyx.test-helper :refer [map->OnyxTestEnv]])

(def cluster-id (java.util.UUID/randomUUID))
(def env-config (u/load-env-config cluster-id))
(def peer-config (u/load-peer-config cluster-id))
(def catalog (c/build-catalog))
(def lifecycles (c/build-lifecycles))
        
(def onyx (component/start (map->OnyxTestEnv {:n-peers (u/n-peers catalog c/workflow)
  :env-config env-config :peer-config peer-config})))

(def job {:workflow c/workflow :catalog catalog :lifecycles lifecycles
                 :task-scheduler :onyx.task-scheduler/balanced})

(onyx.api/submit-job peer-config job)

(def input
  [{:sentence "Getting started with Onyx is easy"}
   {:sentence "This is a segment"}
   {:sentence "Segments are Clojure maps"}
   {:sentence "Sample inputs are easy to fabricate"}])

(u/bind-inputs! lifecycles {:read-segments input})
(def results (u/collect-outputs! lifecycles [:write-segments]))

(component/stop onyx)
```

Dans le onyx-starter, il y a la version 'not in tests' de la définition et du démarrage du component OnyxEnv...





No need to install [rocksdb][2], because embedded, par contre export var env
pour config tmp dir executable.

[1]: https://github.com/daveray/dorothy
[2]: https://github.com/facebook/rocksdb.git
