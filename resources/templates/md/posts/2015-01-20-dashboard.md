{:title "Dashboard"
 :layout :post
 :tags  ["clojure" "compojure" "dashboard" "hiccup" "lightTable" "ring"]}

Since three days I work on a dashboard that
will display statistics in the form of tables and graphs.

I am using with great pleasure the following tools:

* [ring][1] for the server,
* [compojure][2] for the  routing,
* [hiccup][3] for dynamic generation of html pages,
* also [simple-time][4] for handling dates,
  I found it to be more lightweight and concise as
  [clj-time][5].

Thanks to the `ring.middleware.reload` middleware and the tip
which is to set in ring the compojure's routes as a var quote (`#'app-routes`),
every modification made in the code is taken into account
without the need to reload the namespace in the REPL.

I have not yet chosen between Dimple.js and Epoch
for the display part.

Finally, [`doric`][6] helps me to show my data in chart form in
the REPL, which is appreciable.

Of course the IDE I use is [Light Table][7], a must :-)

[1]: https://github.com/ring-clojure/ring|target=_blank
[2]: https://github.com/weavejester/compojure|target=_blank
[3]: https://github.com/weavejester/hiccup|target=_blank
[4]: https://github.com/mbossenbroek/simple-time|target=_blank
[5]: https://github.com/clj-time/clj-time|target=_blank
[6]: https://github.com/joegallo/doric|target=_blank
[7]: http://lighttable.com/|target=_blank
