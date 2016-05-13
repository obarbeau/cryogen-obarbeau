{:title "Clojure - Plugins & tools"
:layout :post
:excerpt "Plugins and tools for Clojure development and leiningen environment"
:toc true}

This is my modest contribution to the [plugins universe][1]
of Leiningen. I synthesized here some of my observations and comments,
as others have already done [here][2]
and [there][3].

I highly recommand to try and adopt the following plugins and tools.

[1]: https://github.com/technomancy/leiningen/wiki/Plugins|target=_blank
[2]: http://jakemccrary.com/blog/2015/01/11/overview-of-my-leiningen-profiles-dot-clj/|target=_blank
[3]: http://www.corfield.org/blog/post.cfm/insanely-useful-leiningen-plugins|target=_blank

## Plugins

There are two ways to add a plugin:

1. globally in `~/.lein/profiles.clj`.
  The plugin will be available for all projects.
1. For a particular project, in its `project.clj`.

Keep in mind that all plugins specified in `profiles.clj`
and thus loaded at the same time than the REPL
may interfere with it,
because they affect the initial classpath,
which is different from the project classpath.
{: .notice}

&#x2799; I recommend to add these plugins directly in projects (`project.clj`)
when required.

### Check out updates

* `xsc/lein-ancient`

Check for outdated dependencies and plugins.

It can also update the `project.clj` and `~/.lein/profiles.clj` files,
automatically or interactively.

```clojure
lein ancient [upgrade :interactive]

lein ancient [upgrade-]profiles
```

<div class="alert alert-warning">
<i class="fa fa-warning"></i> Warning!
The plugin does not manage utf8 correctly, so data can be corrupted,
for example if you have a special REPL prompt.
</div>

### Tests & benchmarks

* `criterium`

Benchmarks.

#### 'Static code analyzers' & 'Clojure lint tools'

* `jonase/kibit`, `lein-bikeshed`, `jonase/eastwood`

`Kibit` is written with `core.logic`.

A plugin for LightTable is also available: `danielribeiro/LightTableKibit`

* `lein-expectations`

Leiningen plugin for running tests written using the expectations library.

* `pedandic`

A Leiningen plugin to reject dependency graphs with common user surprises.
I can't get the v0.0.5 to work.

### Documentation

#### Consult

* `clj-ns-browser.sdoc`

Displays in an external browser available docs for namespaces, functions, ...

Can be injected in the REPL with `im.chit/vinyasa`.

#### Generate

* `gdeer81/lein-marginalia` et `michaelblume/lein-marginalia` (clj 1.7+)

Use `lein marg <options>` in the project's root directory.
Generates `docs/uberdoc.html`.
On the left of the page, text from comments and docstrings,
on the right the Clojure code.

Markdown and asciidoc formats can be used in docstrings
and in standards comments (must put two `;` to enable).

You can also insert mathematical formulas.
Put in the beginning of the clojure source:

```html
;; <script type="text/javascript"
;;  src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML">
;; </script>
```

Then use one of the two notations (inline or not):

```clojure
\\(r^2 = x^2 + y^2\\)
or
$$r^2 = x^2 + y^2$$
```

This plugin does not work when the project's description is written as
```clojure
:description (str "xx"
                  "yy")
```

* `codox`

Generating API documentation from Clojure source code.
The default css is somewhat austere, blue text on gray background.

*Cross Reference All the Things* with `"Document [[module.submodule/var]]"`

*Hide Auto-Generated Record Constructors* eg for records,
with `MyRecord`, we automatically get `->MyRecord & map->MyRecord`.
`(alter-meta! #'->RecordName assoc :no-doc true)`

* `lein-autodoc`

Generates documentation for a project.
The v0.9 does not work well with Leiningen 2 and the v1.0 is not yet published.

### Other plugins

* `clj-ns-browser`

Can be injected in the REPL with `im.chit/vinyasa`.
Launch a complete 'explorer' of functions and var available in the namespaces.

* `clj-stacktrace` and `io.aviso/pretty`

Nice exceptions with colorful stack trace.
The stack from `aviso` is easier to read than the one from `clj-stacktrace`.
The original `(pst)` command can be overrided in a profile's injections.

```clojure
(clojure.stacktrace/print-stack-trace *e)
or
(io.aviso.exception/write-exception *e)
or
(clj-stacktrace.repl/pst+)

to display last exception
```

* `im.chit/vinyasa`

Injects functions in namespaces.
See <http://z.caudate.me/a-more-refined-vinyasa-inject>.

* `lein-autoreload`

Reloads modified sources (and thus namespaces) in the REPL.
Does not work well with ClojureScript projects
whose sources are in `src/clj` and `src/cljs`.

* `emezeske/lein-cljsbuild`

Leiningen plugin to make ClojureScript development easy.

Mandatory for ClojureScript dev!

* `the-kenny/lein-deps-tree`

Prints a print a nicely formatted tree of a project's dependencies.

I do not really see the difference with the `lein deps: tree` command.
Maybe the way to retrieve dependencies (aether, other)?

* `lein-light-nrepl`

Allows LightTable to communicate with an external REPL.
Options can be specified (port, middleware, ...).
Very useful, because I find the evaluation inside LightTable
not very convenient for collections.

* `marick/lein-midje`

Runs both Midje and clojure.test tests.

* `lein-ns-dep-graph`

Shows the namespace dependencies of Clojure project sources as a graph.
Uses Graphviz.

* `lein-pdo`

Higher-order task to perform other tasks in parallel.

Several tasks can be launched with only one command, without interblocking.

* `xeqi/lein-pedantic`

Reject dependency graphs with common user surprises.

* `lein-plz`

Add Leiningen dependencies quickly to the `project.clj`.

The dependencies can possibly be grouped into logical blocks.

* `lein-pprint`

Pretty-print a representation of the project map.

* `weavejester/lein-ring`

Manage `ring` with command line: start and stop sever, generate uberwar, ...

```clojure
lein ring uberwar # standalone version, directly in shell
lein ring war # for use with an app server
```

Options in the `project.clj` file:

```clojure
:ring {:handler try-atw-om.core/app
       :init try-atw-om.core/init}
```

Then inside shell: `ring server-headless <port>` (does not start a browser)

A jar file for standalone deployment can be packaged and started with:

```bash
lein ring uberjar; java -jar <project>-<version>-standalone.jar
```

A war file that will deploy into an existing tomcat with:

```bash
lein ring war
or
lein ring uberwar ; all dependencies included
```

* `lein-try`

To try libs without even creating a project.

* `LonoCloud/lein-voom`

Helps you clean up your dependency tree.

Especially useful when you have snapshot versions.

* `org.timmc/nephila`

Show a graph of your Clojure namespaces.

* `slamhound`

(Re)compute automatically requires and imports.

The `slamhound` alias is available for the shell
but this tools can also be launched within a REPL:

```clojure
(require '[slam.hound])
(slam.hound/-main "src/my/namespace.clj")
(slam.hound/-main "src")
```

## Tools

* `aprint`

Improved `print` display.

* `taoensso.timbre`

Excellent logging library.
Use `timbre` rather than `(def ppr #'clojure.pprint/pprint)`.
The timbre config is shared amongst namespaces.

* `daveray/seesaw`

`Seesaw` easily create everything you need for a Swing application.

## Standalone Scripts

* `lein-exec`

Add this profile the the `:user` profile in your `~/.lein/profiles.clj`
config file.

```clojure
{:plugins [[lein-exec "0.3.1"]]}
```

Then `lein exec standalone.clj`.

With mini scripts `lein-exec(-p)` it is possible to use
`#!/bin/bash lein-exec(-p)` directly in file's header.

## Boot

```bash
wget https://clojars.org/repo/tailrecursion/boot/1.1.1/boot-1.1.1.jar
mv boot-1.1.1.jar boot
chmod a+x boot
mv boot ~/bin/boot
```

Immutable File System -> boot task -> boot task2 -> ...
