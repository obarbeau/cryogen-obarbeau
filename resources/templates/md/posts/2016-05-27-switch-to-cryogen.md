{:title "Switch to Cryogen"
:layout :post
:excerpt "from Jekyll to Cryogen"
:tags  ["clojure" "cryogen"]
:toc false
:draft? false}

My blog was previously served by Jekyll,
with a theme adapted from [So Simple][1].
This suited me very well.
Elegant layout, satisfactory responsive design, fast processing of pages.

But then, every time I wanted to write a quick post,
at the time of starting Jenkins to heat up the receptacle of my prose,
bing! a mismatch is detected between
coffeescript / the Jekyll engine / its plugins / gem / bundle or whatever...

[1]: https://github.com/mmistakes/so-simple-theme|target=_blank

And here I am compelled to spend 20 minutes trying
to update all of these elements
instead of improving my English writing -- and my grammar
deserves a good upgrade...

For a while I used a Docker container wrapping Jekyll and its plugins.
It's an interesting experience,
which has the advantage to freeze versions of tools required for the blog,
while letting those of workstation freely evolve.

And then I have wondered: this is a blog about Clojure,
why not use a blog engine that is made of it,
and enables hosting on Github?
And I quickly came across [Cryogen][2], a very good static website generator.

[2]: https://github.com/cryogen-project/cryogen|target=_blank

Everything is fine, however, I made two small changes to the `core` project.

For this I use `intern` function,
which allows to change the root binding of a var in another namespace.
This is a handy feature when you want
to change a single function without cloning the entire project.

1. The first in the `compiler` namespace, on the `add-prev-next` function,
  where I reversed the order of the previous and next pages
  because I find it more logical to have the historical on the left.
```clojure
(intern 'cryogen-core.compiler
        (with-meta #_1 'add-prev-next
                   {:doc (str "Adds a :prev and :next key to the page/post"
                              " data containing the title and uri of the "
                              "prev/next post/page if it exists")})
        (fn
          [pages]
          (map (fn [[next target prev]] ; before: [prev target next]
                 (assoc target
                   :prev (if prev (select-keys prev [:title :uri]) nil)
                   :next (if next (select-keys next [:title :uri]) nil)))
               (partition 3 1 (flatten [nil pages nil])))))
```
  &#9312; `with-meta` keeps the original docstring and attach it
  to the function,
  because it is not possible to do this in the `fn block []`.

2.The second in the `markup` namespace, on the `rewrite-hrefs` function,
  where I open a new tab when `target` is specified at the end of the link.
  A little regex makes the substitution.

  <script src="https://gist.github.com/obarbeau/65d7f487797d8041b6a79c15b532604f.js"></script>

Finally, the day you use `intern` to define a macro, please remember:
- to add the meta `:macro true` in the name of var
- to add two arguments to your function, `&form` and ` env`
  (which are not required with `defmacro`)
  otherwise you might look for a moment why
  the substitutions do not behave as expected...
  <i class="fa fa-smile-o" aria-hidden="true"></i>

```clojure
(intern 'monger.query (with-meta 'with-collection {:macro true})
        (fn [&form &env ^DB db ^String coll & body]
          `(let [coll# ~coll
                 db# ~db
                 db-coll# (if (string? coll#)
                            (.getCollection ^DB db# ^String coll#)
                            coll#)
                 query# (-> (empty-query db-coll#) ~@body)]
             (exec query#))))
```

