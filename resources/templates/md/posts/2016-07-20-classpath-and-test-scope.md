{:title "Classpath and 'test' scope"
:layout :post
:excerpt "tl;dr: How to get a Clojure project's classpath without 
the 'test' dependencies, from the REPL."
:tags  ["clojure" "test" "dependencies" "managed-dependencies" "classpath" "repl"
  "profiles" "scope" "lein" "leiningen"]
:toc true
:draft? false}

## Context

Leiningen uses the Maven library under the hood, to manage dependencies.
As a consequence, the `dependency scope` mechanism
is used to affect the classpath.

The default, `compile`, is never explicitly specified.

The two mainly used scopes are `provided`
(for the famous `javax.servlet/servlet-api`
when working with Java EE servers...)
and `test`, in order to point out that the dependency is for tests only.

Neither is transitive (brings no other deps), and the `test` one
is not required for normal use of the application.

I'd like to get my project's classpath, the one required at runtime.

And I find annoying that 'test dependencies' are contained in this path.

## First try

I start a REPL with [custom profiles][1]:
`lein with-profiles +local,+cljs,+om repl`

Let's see what `(System/getProperty "java.class.path")` gives us.

omg! It returns all dependencies, including 'test' ones,
and also those of every profiles used to launch the REPL...

What a mess!

## Using Thread

Several solutions equivalent to this one hang out in internet:

`(seq (.. Thread currentThread getContextClassLoader getURLs))`

but with recent `lein` versions, it displays `nil`...

## Using lein

Outside of the REPL, there is a `lein classpath` task.

This task simply wraps a function located
in the `leiningen.core.classpath` namespace.

As with the other tries,
it also gives the full classpath with tests dependencies.
But it reads dependencies from the `project.clj` file,
so there is no interference with optional profiles nor plugins.

So, we'll filter dependencies before passing the project's map to this function.

### Step 1: leiningen-core

Add a dependency to your project's map file: `project.clj`,
If your `*read-eval*` is `true` you might use the following notation:

```clojure
   :dependencies [...
                 [leiningen-core #=(leiningen.core.main/leiningen-version)]]
```

Thus the same version of Leningen than the one used
to start the REPL will be used.

If that does not work, go back to basics:

```clojure
   :dependencies [...
                 [leiningen-core "2.6.1"]]
```

### Step 2: require

In the REPL session, type:
`(require '[leiningen.core.classpath :as lcc])`
to be able to use the `get-classpath` function.

Also `(require '[leiningen.core.main :refer [leiningen-version]])` 
to let the `leiningen-version` seen above return the correct number.

### Step 3: dependency notation

<div class="alert alert-success">
<i class="fa fa-info-circle"></i>
&nbsp;A dependency, as added to any project file, is a vector with at least
the artifact name (group-id/name coordinate) and its version
(version will become optional with managed-dependencies, see last paragraph).
Some modifiers might be added, such as
`:exclusions`, `:classifier`, or `:scope`, as pairs of key/value.
</div>

Examples:
```clojure
:dependencies [[org.clojure/clojure              "1.8.0"]

               [leiningen-core
                #=(leiningen.core.main/leiningen-version)]

               [clojurewerkz/propertied          "1.2.0"
                           :exclusions [[clojurewerkz/support]]]

               [midje/midje                      "1.8.3" :scope "test"]]
```

### Step 4: assembly

If the value of the `scope` modifier is `"test"`, we will remove the dependency.

```clojure
; read project's file and call get-classpath with updated dependencies
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

As a bonus, missing deps will be downloaded into your maven repo.

### Step 5: cleaning

It is not desirable to permanently change the dependencies of a project to add
`leiningen-core` for this very specific need.

Several solutions are possible:

1. add in the project file an entry of the type:
```clojure
:profiles {
  :dev {:dependencies [[leiningen-core "2.6.1"]]}
}
```
This will set a profile used only in development mode,
 which will not pollute the standard project dependencies.
 We can take the opportunity to add a few plugins also,
 like `michaelblume/lein-marginalia` to generate a nice project documentation.

2. create a profile common for all projects (user-wide or system-wide)
 in the `profiles.clj` configuration file.
 In this case, the profile should not be called `dev`.

3. use a packaged library such as `repl-tasks`, and inject
   useful functions in the `clojure-core` namespace. See [here][1].

## Managed dependencies

Leiningen 2.6.2 will bring a very interesting new feature: managed dependencies.
They allow to declare the version of some dependencies in a 'parent' project,
and to propagate automatically in child projects.
Those coming from Java and Maven's world remember about parent pom and cie.

Small problem, the scope should be set in these `managed-dependencies`.
So to benefit from this new feature, we'll have to look at both the project deps
and those of its optional parent to be able to detect a 'test' scope.

So stay tuned for an update to this post, as soon as lein 2.6.2 is available!

[1]: /posts/2016-07-15-leiningen-profiles|target=_blank
