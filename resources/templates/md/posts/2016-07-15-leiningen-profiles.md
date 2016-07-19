{:title "Leiningen profiles and REPL enrichment"
:layout :post
:excerpt "tl;dr: Quick overview of Leiningen profiles,
and those I use with lein tasks, more notably `lein repl`."
:tags  ["clojure" "leiningen" "profiles" "repl"]
:toc true
:draft? false}

## Context

What do you do in Paris in July?

Sip a beer with my friend Wilfried at the terrace of a café,
looking the rain falling, and waiting for the summer really begins.

Serious and less serious subjects follow each other...

There also arises the question of a good development environment for Clojure,
as a continuation of our discussion when we were at [Paris's Clojure meetup][3].

In addition to the excellent pair IntelliJ Idea + Cursive,
some profiles for Leiningen can greatly enhance the experience of REPL.

## Official documentation 

The Leiningen doc about profiles is very complete and clear.
It is available [here][1].

The [sample `project.clj`][2] file of a standard Clojure project
can also give you tips about options you can configure or add
in your own profiles.

## Overview

Profiles are simply maps of options, that will be applied to lein tasks.
Leiningen provides few defaults and allows to complete with your own needs.

### Default profiles

* `leiningen/default` is a compound profile, initially made of these profiles:
```text
:base      brings base dependencies `org.clojure/tools.nrepl`
           and `clojure-complete` (completion with the `tab` key)
:user      will be shared amongst all clojure projects for logged user
:system    same as `:user` but applies system-wide
:provided  if you understand Maven's scope it should be the same here...
:dev       should be defined in every `project.clj`
```
  It automatically loads all the specified profiles when you launch a lein task.
  Without any additional custom configuration, the last 4 will be empty.

* `leiningen/test` injects some hooks for tests.

* `update`, `offline` and `debug` are three profiles that set
  the corresponding 'predicate-keyword' (eg `:debug?`)
  to `true` (ou `:always` for `update`). They should be merged whenever needed.

### Where to define profiles?

They are retrieved in the following order, and override
or complement each other.

1. `/etc/leiningen/profiles.clj` (system-wide)
2. `~/.lein/profiles.clj` (user specific)
3. `project.clj` (in project's root)
4. `profiles.clj` (in project's root)

### How to use them?

To use only `default` and `prod` profiles for the `<task>` task:

```bash
   lein with-profile default,prod <task>
```

To use the specified profile(s) (order matters)
in addition to the default one for the `<task>` task,
use the `'+'` character:

```bash
   lein with-profile +prod,+offline,+mongodb <task>
```

This second writing is best in order to always keep `leiningen/default` active.
<div class="alert alert-warning">
<i class="fa fa-warning"></i>
Beware however, this one is automatically disabled during the 
`pom`, `jar` and `uberjar` tasks,
so as not to 'pollute' dependencies and compilation.
</div>

All dependencies and plugins brought in by an activated profile
can indeed have an impact the final project's classpath and/or REPL invocation,
so pay attention to the clashes.

### Merge profiles

The merge is performed either from the command line, as seen above,
or in the definition of the profile.

For example, to permanently merge `offline` and `mongodb` profiles
in the `prod` one, just define `prod` as a vector that references others,
as follows:

```clojure
:prod [:offline
       :mongodb
       {; now the prod profile definition
        :license ...
       }]
```

### Custom user profile

This is an annotated part of my `user` profile,
located in the `~/.lein/profiles.clj` file,
that is shared amongst all clojure projects.

```clojure
 :user
 {; if you are not fortunate enough to have an SSD in your PC,
  ; you will save time using a ramdisk for compiling classes.
  ; in this case, you will make a symlink from the 'target' directory
  ; to the ramdisk.
  ; Yet paths outside the project root cannot be accessed by `lein clean`,
  ; except if we 'deprotect' them like show: 
  :clean-targets ^{:protect false} [:target-path]

  ; it is possible to configure any options for a tool in this profile
  ; without importing the corresponding plugin, which will be done only
  ; in projects that use it (in `project.clj`).
  ; for example, codox is a documentation tool, we set its overall options
  ; here but don't import the plugin.
  :codox {:defaults {:doc/format :markdown}
          :writer codox.writer.html/write-docs
          ; source links
          :src-dir-uri "http://github.com/obarbeau/XXX/blob/master/"
          :src-linenum-anchor-prefix "L"}

  ; `lein release` will work only if deploy repository is named `releases`
  :deploy-repositories
  [["releases"
    {:url "http://ip:8081/nexus/content/repositories/clj-releases"
     ; sign releases? (it's not about signing git commits here)
     :sign-releases true
     ; `sign` does not work if you must enter a pasword for the gpg key
     ; so override system's default gpg key with one that does not require
     ; a password
     :signing {:gpg-key "XXXXXX"}}]]

  :global-vars { *print-length* 30 }

  ; shared options for compilation
  :javac-options ["-target" "1.8" "source" "1.8" "-Xlint:-options"]

  ; for debugging an external REPL with eclipse or IntelliJ 
  ;:jvm-opts [(str "-agentlib:jdwp=transport=dt_socket,server=y,"
  ;           "suspend=n,address=5005")]
  
  ; common options for the JVM
  :jvm-opts ["-XX:+AggressiveOpts" "-XX:+UseCompressedOops"
             "-XX:+OptimizeStringConcat"
             "-XX:+UseFastAccessorMethods" "-server"
             "-Duser.timezone=Europe/Paris"]

  ; in this `user` profile, use plugins with the less dependencies
  ; as possible
  :plugins [; executing Clojure scripts (in two words)
            [lein-exec                        "0.3.6"]
            ; Add leiningen dependencies quickly
            [lein-plz                         "0.3.0"]
            ; Pretty-print a representation of the project map
            [lein-pprint                      "1.1.2"] ; no dependencies
            ; trying out new libraries without creating a project
            [lein-try                         "0.4.3"]] ; no dependencies

  ; avoid polluting root directory with an unnecessary pom
  :pom-location "target/"

  ; a colored prompt
  :repl-options {:prompt (fn [ns] (str "[35m[[34m" ns "[35m][33m λ:[m "))}}
```

Remember the `dev` profile should not be defined in the `profiles.clj` file,
but for each project.

I also use a `local` profile, mainly when I launch a REPL,
`lein with-profile +local repl`,
with the following content:

```clojure
 :local
 {; set your composite command line aliases here
  :aliases {; cf. the post 2015-02-18-clojure-plugins-tools
            "slamhound" ["run" "-m" "slam.hound"]}

  ; dependencies used by injections (see below)
  :dependencies [; awesome print for datastructure
                 [aprint                           "0.1.3"]
                 ; injections in the REPL see below
                 [im.chit/vinyasa                  "0.4.3"]
                 [leiningen #=(leiningen.core.main/leiningen-version)]
                 ; pretty print test outputs
                 [pjstadig/humane-test-output      "0.8.0"]
                 ; see https://github.com/obarbeau/repl-tasks
                 [repl-tasks                       "3.2.0"]
                 ; trace-oriented debugging tools
                 [spyscope                         "0.1.5"]
                 ; extra documentation for core functions
                 [thalia                           "0.1.0"]]

  ; The following code will be evaluated once at the beginning
  ; of every lein task (including `repl`, but excluding `jar` and `uberjar`)
  ; So this is like an 'init' stuff.
  :injections [(require '[aprint.core]
                        '[io.aviso.ansi :as ansi]
                        'pjstadig.humane-test-output
                        '[repl-tasks.core]
                        'spyscope.core
                        '[thalia.doc]
                        '[vinyasa.inject :as inject])
               ; injects in the `clojure.core` ns (so it will be available
               ; from any other namespace)
               ; the following functions, from various specified namespaces.
               ; These functions will be callable with the '>' prefix.
               ; eg. (>doc f) will be translated to (clojure.repl/doc f)
               ; (>sh "ls") will be translated to
               ;   (clojure.java.shell/sh "ls")
               (inject/in
                clojure.core >
                [aprint.core aprint]
                [clojure.repl apropos dir doc find-doc pst source]
                [clojure.pprint pprint pp]
                [clojure.java.shell sh]
                [repl-tasks.core
                 check-kibit dependencies goto
                 lein-classpath lein-deploy lein-deps
                 lein-install lein-midje lein-midje-auto
                 lein-pprint lein-release lein-run lein-uberjar sdoc]
                [vinyasa.lein lein]
                [vinyasa.reflection .> .? .* .% .%>])

               ; other init stuff
               (repl-tasks.core/check-kibit)
               (repl-tasks.core/check-cuttle)
               ; activate humane-test-output
               (pjstadig.humane-test-output/activate!)
               (println
                (str ansi/green-font "∙ [done] injections vinyasa."
                     ansi/reset-font))
               ; register extra clojure documentation
               (thalia.doc/add-extra-docs! :language "en_US")
               (println
                (str ansi/green-font "∙ [done] extra doc.\n"
                     ansi/reset-font))]

  ; useful plugins
  :plugins [[io.aviso/pretty                  "0.1.26"]
            [venantius/ultra                   "0.4.1"]]

  :repl-options {:timeout 150000}

  ; sign commits and tags
  :signing {:gpg-key "YYYYY"}

  ; additional source paths
  :source-paths ["dev/clj" "dev/cljs"]}
```

And finally, some checks on code quality are always welcomed.

Profile:
```clojure
:new-checks {:dependencies [[repetition-hunter               "1.0.0"]]
             :plugins [[jonase/eastwood                "0.2.3" :scope "test"]
                       [lein-bikeshed                  "0.2.0" :scope "test"]
                       [lein-cloverage                 "1.0.6" :scope "test"]
                       [lein-kibit                     "0.1.2" :scope "test"]
                       [lein-repetition-hunter         "0.1.0-SNAPSHOT"]]}
```

Usage:
```bash
PROFILES="with-profile +new-checks"
lein ${PROFILES} compile
lein ${PROFILES} bikeshed -m 80 &>> /tmp/bikeshed.txt
lein ${PROFILES} kibit --reporter markdown $(find . -iname '*.clj*') &>> /tmp/kibit.md
lein ${PROFILES} eastwood '{:namespaces [:source-paths]}' | grep -v 'jar:file:/opt/m2_repo' &>> /tmp/eastwood.txt
lein ${PROFILES} repetition-hunter &>> /tmp/hunter.txt
lein ${PROFILES} cloverage
WORK_DIR=$(pwd)
# open coverage results in browser
google-chrome --incognito "file://${WORK_DIR}/target/coverage/index.html"
# open reports in Sublime-text
sublime-text /tmp/bikeshed.txt /tmp/kibit.md /tmp/eastwood.txt /tmp/hunter.txt
```

Your productivity is now multiplied by 3.14

[1]: https://github.com/technomancy/leiningen/blob/master/doc/PROFILES.md|target=_blank
[2]: https://github.com/technomancy/leiningen/blob/master/sample.project.clj|target=_blank
[3]: http://clojure.paris/|target=_blank