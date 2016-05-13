{:title "Uberjar with ClojureScript"
:layout :post
:tags  ["clojure" "clojureScript" "uberjar" "leiningen"]}

Today I wanted to deploy a `ring` project with a ClojureScript part.

Deployment is done with an uberjar, which I find effective.
I use an additional directory to separate the sources,
which are located in `src/clj/ns` and `src/cljs/ns`.

So, during the compilation, `lein` complains it could not locate the namespace:

```java
...FileNotFoundException: Could not locate ns/main__init.class ...
```

The reason is that the standard content of the `source-paths` option,
which is usually `src/ns`, is no more suitable.
So I changed the `uberjar` profile
in order to specify the different structure of the source directories.

Firstly, the `sources-level-down` profile shown below
replaces the standard content of the `source-paths`.
I found a tip on the Leiningen site to define
profiles overrides.
It therefore allows
to include this kind of source paths only for projects with ClojureScript:

Once, in the `~/.lein/profiles.clj` file:

```clojure
:sources-level-down {:source-paths ^:replace ["src/clj" "src/cljs"]}

:uberjar-common {:aot :all
                 <other options>}
:uberjar-additional {} ; nothing by default
:uberjar [:uberjar-common :uberjar-additional]
```

In every ClojureScript projects, in their `project.clj` file:

```clojure
; overrides profile to include new `source-path` definition
:profiles {:uberjar-additional [:sources-level-down]}
```

Remember that the JavaScript compilation will not be automatic
during the uberjar build.

No doubt a hook can be added to the workflow,
but in the meantime I recommend using [cuttle][1],
defined itself as an _User Interface for the ClojureScript Compiler_,
to keep an eye on the JS compilation status.

Happy uberjar!

[1]: https://github.com/oakmac/cuttle|target=_blank
