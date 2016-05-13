{:title "Dashboard, part 2"
 :layout :post
 :tags  ["clojure" "dashboard" "core.async" "websocket"]}

After a break for more important projects,
I'm back on the dashboard project.
Since the first version was working fine,
I decided to redo everything,
according to a well-known principle in application development...

I replaced vanilla JavaScript by ClojureScript,
and Ajax calls on `Compojure` routes (to retrieve data)
by `core.async` on a `websocket`.

I had previously used on another project
websockets with `httpkit` and `webbitserver`.
It was working on two different ports,
and management (and building) of websocket
brought a lot of boilerplate.

It was also necessary to convert data to json
with `cheshire` on the server side
and Javascript functions on the front side.

For websockets I started using [sente][1],
which seemed to meet my needs perfectly.
This library is from the same author
than the excellent logging library `timbre`.
Alas, a version conflicts and a recalcitrant macro
prevented me from using it.

So I focused my attention on [chord][2].
Same core functionality than `sente`.
The merge with the Compojure routes is done with the
`wrap-websocket-handler` wrapper; the ` transit` format is default,
so everything is perfect.

There's no need anymore to worry about the
`onOpen`, `onClose` and `onMessage` methods of the websocket,
everything is automatic.
It lets the user focus on data to transit the ws-channel,
with `core.async` `get` and `put`.
And of course all is done without having
to explicitly convert the data to json.

On the front side, I use [domina][3],
which allows me to easily manipulate
the elements of the page as well as events.

[1]: https://github.com/ptaoussanis/sente|target=_blank
[2]: https://github.com/james-henderson/chord|target=_blank
[3]: https://github.com/levand/domina|target=_blank
