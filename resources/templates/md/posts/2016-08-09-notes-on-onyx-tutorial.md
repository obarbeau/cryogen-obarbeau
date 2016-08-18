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

<https://github.com/onyx-platform/onyx-dashboard>

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

Need to install [rocksdb][2]

[1]: https://github.com/daveray/dorothy
[2]: https://github.com/facebook/rocksdb.git