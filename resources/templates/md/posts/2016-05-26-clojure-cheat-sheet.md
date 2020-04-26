{:title "Clojure core functions - Cheat Sheet"
:layout :post
:excerpt "My notes on classic Clojure functions."
:tags  ["clojure"]
:toc true
:draft? false}

Here are some useful Clojure core functions along with examples
 and MathML notation for fun.

Nothing new under the sun, but these notes had been dragging on
for a long time in my drafts, so it was time to share them!

<table style="border-spacing: 7px; border-collapse: separate;">
<tr>
  <td>

```clojure
'a ⇔ (quote a)
[syntax quote] `a ⇔ (quote user/a)

;; Examples
(read-string "`{:a 100}")
; => {:a 100}
`[:a ~(+ 1 1) ~'c d ~`e]
; => [:a 2 c user/d user/e]
```
  </td>
  <td>
`Syntax quoting` brings optional `gensym` (if `#` at the end of var name.
 Useful for macros that create local vars, eg in `let` or `loop`) + namespace
 qualification of symbols. Produces code to reproduce the form.
 One can unquote inside a syntax-quoted form with the tilde `~`.
  </td>
</tr>

<tr>
  <td>

```clojure
and arg (.method arg)
```
  </td>
  <td>
Returns `nil` if arg is `nil`, otherwise execute `method` on arg.
 Thus avoids `nil` checks.
  </td>
</tr>

<tr>
  <td>

```clojure
apply f x1 ... xn c
→ (f x1 x2 ... c1 c2 ... cn)

;; Examples
(apply + 5 6 '()) ; 11
(apply max [1 2 3]) ; 3
; but
(max [1 2 3]) ; [1 2 3]
(+ [1 2 3]) ; error
(apply + [1 2 3]) ; 6
```
  </td>
  <td>
Evaluates ƒ (must not be a macro) on \\( x_n \\) arguments prepended
 to the collection.
 Has similarities with `unquote splicing` `~@`.
  </td>
</tr>

<tr>
  <td>

```clojure
assoc map k 1 v1 ... k n v n
assoc vec idx1 v1 ... idxn vn

;; Examples
(assoc [1 2 4] 3 10 0 12)
; [12 2 4 10]
(assoc {:k1 "old v1" :k2 "v2"}
       :k1 "newv1" :k3 "v3")
; {:k3 "v3", :k1 "newv1", :k2 "v2"}
```
  </td>
  <td>
<ul><li>applied on a `map`, returns a `map` of the same type (hashed/sorted)
 containing (or substituting) k/v of `map` and (by) those specified.</li>
<li>applied on a `vector`, replace the element at specified index or add it at
 the last position.</li></ul>
  </td>
</tr>

<tr>
  <td>

```clojure
assoc-in map [k1 ... kn] v
assoc-in vec [k1 ... kn] v

;; Examples
(assoc-in [{:k1 "v1"} {:k2 "v2"}]
          [1 :k2] "nv2")
; [{:k1 "v1"} {:k2 "nv2"}]
(assoc-in {} [:k1 :k2 :k3] "nv")
; {:k1 {:k2 {:k3 "nv"}}}
```
  </td>
  <td>
Returns the same type of associative structure, with v the value of nested key
 reached by \\( k1 .. kn \\). If any \\( k_x \\) level does not exist,
 hash-maps are created.
  </td>
</tr>

<tr>
  <td>

```clojure
bean java-object

;; Examples
(bean java.awt.Color/RED)
; {:red 255:transparency : 1 ...}
```
  </td>
  <td>
Returns a `map` with all getters of the java object.
  </td>
</tr>

<tr>
  <td>

```clojure
comp f1 f2 ... fn
→ (f 1 (f2 ... (fn _)))

;; Examples
((comp str +) 8 8 8) ; « 24 »

```
  </td>
  <td>
Returns a function with undefined arity, applying \\( f_x \\)
 (from right to left) on arguments.
  </td>
</tr>

<tr>
  <td>

```clojure
concat x1 ... xn

;; Examples
(concat [:a :b] nil [1 [2 3] 4])
; (:a :b 1 [2 3] 4)

```
  </td>
  <td>
Returns a sequence including all \\( x_k \\) elements.
 Does not flatten nested colls.
  </td>
</tr>

<tr>
  <td>

```clojure
conj

;; Examples
; new element at the front
(conj '(1 2 3) :a) ; (:a 1 2 3)
; new element at the back
(conj [1 2 3] :a) ; [1 2 3 :a]
```
  </td>
  <td>

  </td>
</tr>

<tr>
  <td>

```clojure
constantly x

;; Examples
(constantly x) 1 2 3 → x
```
  </td>
  <td>
Returns a function with undefined arity, that always results in x.
  </td>
</tr>

<tr>
  <td>

```clojure
contains? coll k

;; Examples
(contains? [1 2 3 4] 4)
; false ; index outOfBounds
(contains? [1 2 3 4] 0) ; true
(contains? '(1 2 3 4) 2)
; IllegalArgumentException
```
  </td>
  <td>
Returns true if the **key** k is present in the **indexed** collection
 (`map` and `set`), or if the index exists in a `vector`.
 Do not use with `list`.
 Prefer `some` to query for a value.
  </td>
</tr>

<tr>
  <td>

```clojure
defrecord

;; Examples
(defrecord Foo [a b c]) ; user.Foo
(def f (Foo. 1 2 3)) ; #'user/f
(:b f) ; 2
(class f) ; user.Foo p
```
  </td>
  <td>
Optionally with implementation of protocols.
  </td>
</tr>

<tr>
  <td>

```clojure
dissoc map k1 ... kn

;; Examples
(dissoc {:fname "John" :lname "Doe"}
        :lname)
; {:fname "John"}
```
  </td>
  <td>
Opposite of `assoc`. Returns an associative structure
 of the same type than `map` but without the nested key reached
 by \\( k\_1 \cdots k\_n \\)
  </td>
</tr>

<tr>
  <td>

```clojure
doseq dorun doall
```
  </td>
  <td>
Force evaluation of lazy seqs (side effects).
 unlike `for`, `doseq` never returns a value but `nil`.
 `doall `retains the head and returns it.
 `dorun` does not retain the head and returns `nil`.
  </td>
</tr>

<tr>
  <td>

```clojure
dotimes bindings & body

;; Examples
(dotimes [n 5] (println "n is" n))
```
  </td>
  <td>
Runs body \\( n \\) times, from \\( 0 \text{ to } n-1 \\).
  </td>
</tr>

<tr>
  <td>

```clojure
find map k
find vec idx

;; Examples
(find {:b 2 :a 1 :c 3} :a) ; [:a 1]
(find [:a :b :c :d] 2) ; [2 :c]
```
  </td>
  <td>
- if `map`, returns the map entry for key \\( k \\) or `nil` if not found.
- if `vector`, returns entry for index \\( idx \\) or `nil` if not found.
  </td>
</tr>

<tr>
  <td>

```clojure
flatten

;; Examples
(flatten [1 [2 3 [4 5] 6]])
; (1 2 3 4 5 6)
```
  </td>
  <td>
Flattens the nested sequences.
  </td>
</tr>

<tr>
  <td>

```clojure
fnil f x1 ... xn
```
  </td>
  <td>
Returns a function that calls ƒ with \\( x_k \\)
 as an argument if the original argument is `nil`.
 The arity of ƒ must be \\( \geq n \\).
  </td>
</tr>

<tr>
  <td>

```clojure
for [x valx
y valy
...
:let [z valz ...]
:while test
:when test]
body
```
  </td>
  <td>
« List comprehension ».
 Returns a sequence containing the results of the execution of body.
 Not intended for side effects.
  </td>
</tr>

<tr>
  <td>

```clojure
frequencies coll

;; Examples
(frequencies ['a 'b 'a 'a])
; {a 3, b 1}
```
  </td>
  <td>
Returns with a `map` that indicates, for each separate element of `col`,
 the frequency at with which it appears.
  </td>
</tr>

<tr>
  <td>

```clojure
group-by f coll

;; Examples
(group-by #(.length %)
  ["some" "words" "with"
  "different" "lengths"])
; {4 ["some" "with"], 5 ["words"],
;  9 ["different"], 7 ["lengths"]}

(group-by #(< % 10) [1 2 20 21])
; {true [1 2] false [20 21]}
```
  </td>
  <td>
Returns a `map` of `col` elements, sorted by the return value of ƒ applied.
 to them.
  </td>
</tr>

<tr>
  <td>

```clojure
interleave c1 ... cn

;; Examples
(interleave [:a :b] (iterate inc 1))
; (:a 1 :b 2)
```
  </td>
  <td>
Returns a sequence containing the first element of each \\( c_x \\),
 then the second, ...
  </td>
</tr>

<tr>
  <td>

```clojure
interpose sep coll
```
  </td>
  <td>
Returns a sequence of elements of the collection separated by the
 `sep` separator.
  </td>
</tr>

<tr>
  <td>

```clojure
into to from

;; Examples
(into {} [[1 2] [3 4]])
; {1 2, 3 4}
(into [] {1 2, 3 4})
; [[1 2] [3 4]]
(into (4) '(1 2 3))
; (3 2 1 4)
(into '(1 2 3) '(:a :b :c))
; (:c :b :a 1 2 3)
(into [1 2 3] [:a :b :c])
; [1 2 3 :a :b :c]
```
  </td>
  <td>
Returns a collection of the same type than `to`, appending all elements of
 collection `from`.
  </td>
</tr>

<tr>
  <td>

```clojure
iterate f x
→ (x (f x) (f (f x)) ...)

;; Examples
(iterate #(∗ 2) 2)
; (2 4 8 16 ...)
```
  </td>
  <td>
ƒ must be a pure function.
  </td>
</tr>

<tr>
  <td>

```clojure
juxt f 1 f2 ...
(juxt f1 f2 f3) x
→ [(f1 x) (f2 x) (f 3 x)]

;; Examples
(map (juxt second count)
     ['(2 3) '(5 6 9)])
; ([3 2] [6 3])
```
  </td>
  <td>
Returns a function that returns a `vector` whose elements are the application
 of ƒ on the argument.
  </td>
</tr>

<tr>
  <td>

```clojure
keep f coll
keep-indexed f coll

;; Examples
(keep #(when (odd? %) %) (range 10))
; (1 3 5 7 9)
(map #(when (odd? %) %) (range 10))
; (nil 1 nil 3 nil 5 nil 7 nil 9)
```
  </td>
  <td>
Returns a sequence made of non `nil` results of the application of ƒ
 on every `coll` elements. `false` results are included.
 ƒ must be a pure function.
`keep-indexed` uses a function like `fn [idx v]`.

  </td>
</tr>

<tr>
  <td>

```clojure
list x1 ... xn

;; Examples
'(a 2 3) ; (a 2 3)
(list a 2 3)
; Exception cannot resolve a
```
  </td>
  <td>
Returns a `list` containing all \\( x_n \\) args, possibly `nil`.
 Unlike literal notation list `'(...)`,
 the elements are evaluated before insertion.
  </td>
</tr>

<tr>
  <td>

```clojure
list* x1 ... xn s
→ (x1 ... xn s1 ... sn)
list* x1 ... xn nil
→ (x1 ... xn)
list* x1 ... xn ()
→ (x1 ... xn)

;; Examples
(list* 1 2 [3 4])
; (1 2 3 4)
(list 1 2 [3 4])
; (1 2 [3 4])
(list* nil [1 2]) ; (nil 1 2)
(list* 1 nil) ; (1)
```
  </td>
  <td>
Returns a `list` containing all \\( x_n \\) args, possibly `nil`, as well as
 all elements of sequence s (if not empty and not `nil`).
  </td>
</tr>

<tr>
  <td>

```clojure
[p]map f c1 c2 ...
→ ((f c11 c21 ...)
   (f c12 c22 ...) ...)

[p]map f c ; ((f c1) (f c2) ...)

[p] : means parallel

mapv f c1 c2 ...
→ [(f c1 1 c21 ...)
   (f c1 2 c22 ...) ...]

;; Examples
; 7 et 8 seront ignorés
map #(+ %%2%3) '(1 2 3)
    '(4 5 6 7 8) '(9 10 11)
; (14 17 20)
```
  </td>
  <td>
Returns a sequence containing results of the application of ƒ
 on each first elements of every collections \\( c_n \\)
 then on each second elements, ...
 ƒ should have as many args as the number of collections.
 If a collection has too many arguments they will be ignored.
 With a single collection, `map` applies the function on every elements.
 `mapv `: same thing but returns a `vector` and is not lazy.
  </td>
</tr>

<tr>
  <td>

```clojure
mapcat f c1 ... cn
→ (concat (f c1) (f c2) ...)

;; Examples
(mapcat reverse [[3 2 1 0] [6 5 4]
                 [9 8 7]])
; (0 1 2 3 4 5 6 7 8 9)
(mapcat list [:a :b :c] [1 2 3])
; (:a 1 :b 2 :c 3)
(mapcat (fn [x] (repeat x x)) [12 3])
; (1 2 2 3 3 3)
```
  </td>
  <td>
Equivalent to `(apply concat (map f c1 ... cn))`.
 Applies `concat` on the result of the application of `map`
 on ƒ and collections.
  </td>
</tr>

<tr>
  <td>

```clojure
memoize f
```
  </td>
  <td>
Returns a cached version of ƒ. ƒ must be pure.
  </td>
</tr>

<tr>
  <td>

```clojure
^{:doc ...}

;; Examples
(def ^{:doc "a var"} x 10)
```
  </td>
  <td>
Alternate docstring. Metadata does not affect equality.
  </td>
</tr>

<tr>
  <td>

```clojure
or supplied-val default-val
```
  </td>
  <td>
Returns `supplied-val` if not `nil`, otherwise `default-val`.
  </td>
</tr>

<tr>
  <td>

```clojure
partial f x1 ... xn

;; Examples
#(+ 1 %) ⇔ (partial + 1)
```
  </td>
  <td>
Returns a function that takes \\( n \\) less args that what ƒ requires.
  </td>
</tr>

<tr>
  <td>

```clojure
partition n coll
partition n step coll
partition n step pad coll

;; Examples
(partition 2 [1 2 3]) ; ((1 2))
(partition 2 1 (repeat 0) [1 2 3])
; ((1 2) (2 3) (3 0))
(partition 2 1 [1 2 3])
; ((1 2) (2 3))
```
  </td>
  <td>
Returns a lazy sequence containing lists of \\( n \\) elements each.
 If the final `list` has less than \\( n \\) elements, it is not added,
 except with `partition-all` (see below).
 The 'step', which is \\( n \\) by default, is the offset
 for the creation of each list.
 'Pad' is a list designed to complement the latest
 if less than \\( n \\) elements.
  </td>
</tr>

<tr>
  <td>

```clojure
partition-all n coll

;; Examples
(partition-all 2 [1 2 3])
; ((1 2) (3))
```
  </td>
  <td>
Similar to `partition`, but also builds the last `list` even if there
 are less than \\( n \\) elements.
  </td>
</tr>

<tr>
  <td>

```clojure
partition-by f coll

;; Examples
(partition-by even? [1 2 3])
; ((1) (2) (3))
(partition-by (partial < 10)
              [1 2 11 1])
; ((1 2) (11) (1))
```
  </td>
  <td>
Similar to `partition`, but cut the list each times ƒ change its value.
  </td>
</tr>

<tr>
  <td>

```clojure
reduce f c
→ (f (f (f c1 c2) c3) c4) ...
; ƒ should have an arity without args
reduce f '() → f
reduce f '(c1) → c1
reduce f val c
→ (f (f (f val c1) c2) c3) ...
reduce f val '() → val
```

  </td>
  <td>
ƒ should have a 2 args arity, except when not used or indicated.
 Returns the accumulator.
  </td>
</tr>

<tr>
  <td>

```clojure
reductions
```
  </td>
  <td>
Returns a sequence of intermediate steps of `reduce`.
  </td>
</tr>

<tr>
  <td>

```clojure
repeatedly f → '(f f f ...)
repeatedly n f → '(f f f ... n)
```
  </td>
  <td>
ƒ should have no args, possibly impure.
 Returns an infinite sequence (or size n) of successive calls to ƒ.
  </td>
</tr>

<tr>
  <td>

```clojure
repeat x → '(x x x ...)
repeat n x → '(x x x ...n)
```
  </td>
  <td>
Returns an infinite sequence (or size n) of value x.
 If x is a function, only one call is made.
  </td>
</tr>

<tr>
  <td>

```clojure
seq coll
sequencecoll

;; Examples
(seq {}) ou (seq nil) ; nil
(sequence {}) ou (sequence nil)
; ()
(sequence [1 2]) ou (seq [1 2])
; (1 2)
```
  </td>
  <td>
Returns a sequence from the collection `coll`.
 For an `nil` or empty collection, `seq` and `sequence` behave differently.
  </td>
</tr>

<tr>
  <td>

```clojure
sort coll
sort comp coll
sort-by keyfn coll
sort-by keyfn comp coll

;; Examples
(sort [1 56 2 23 45 34 6 43])
; (1 2 6 23 34 43 45 56)
(sort > [ 1 56 2 23 45 34 6 43])
; (56 45 43 34 23 6 2 1)
(sort-by #(.length %)
         ["the" "quick"
          "brown" "fox"])
; ("the" "fox" "quick" "brown")
```
  </td>
  <td>
Returns a sorted sequence.
  </td>
</tr>

<tr>
  <td>

```clojure
split-at n coll

;; Examples
(split-at 2 [:a :b :c :d :e])
; [(:a :b) (:c :d :e)]
```
  </td>
  <td>
Returns a vector of `[(take n coll) (drop n coll)]`.
  </td>
</tr>

<tr>
  <td>

```clojure
split-with pred coll

;; Examples
(split-with (partial >= 3)
; [1 2 3 4 5]) [(1 2 3) (4 5)]
```
  </td>
  <td>
Returns a vector of `[(take-while pred coll) (drop-while pred coll)]`.
  </td>
</tr>

<tr>
  <td>

```clojure
take-nth n c

;; Examples
(take-nth 3 '(2 5 9 6 8 9 10 11))
; (2 6 10)
```
  </td>
  <td>
Takes the first and every nth elements of `c`.
  </td>
</tr>

<tr>
  <td>

```clojure
take-while pred coll
```
  </td>
  <td>
Returns elements of `coll` as long as predicate `pred` is true.
  </td>
</tr>

<tr>
  <td>

```clojure
tranpoline f & args
```
  </td>
  <td>
Used for mutual recursion without consumming the stack.
 Performs the ping pong as long as what is returned is a function.
  </td>
</tr>

<tr>
  <td>

```clojure
update-in map [k1 .. kn] f & args

;; Examples
(def jdoe {:name "John Doe"
           : address {:zip 41,...}})
(update-in jdoe [:address :zip] inc)
; {:name "John Doe"
;  :address {:zip 42}}
```
  </td>
  <td>
Returns an associative structure identical to
 `map` but with the value of the nested key reached by \\( k\_1 \cdots k\_n \\)
  updated by ƒ (and its optional arguments).
  If the \\( k_x \\) level does not exist, hash-maps will be created.
  </td>
</tr>

<tr>
  <td>

```clojure
vec c → [c1 c2 ...]
vec nil → []
```
  </td>
  <td>
Returns a `vector` containing the elements of c.
  </td>
</tr>

<tr>
  <td>

```clojure
vector x1 x2 ... → [x1 x2 ...]
vector nil → [nil]
```
  </td>
  <td>
Returns a vector containing all arguments \\( x_\n \\).
  </td>
</tr>

<tr>
  <td>

```clojure
vector-of t
vector-of t & x1 ... xn

;; Examples
(conj (vector-of :int 4) 1 2 3)
; [4 1 2 3]
```
  </td>
  <td>
Returns a `vector` of primitive types (:int :long :float :double :byte
 :short :char or :boolean) containing all optional \\( x_\n \\) arguments.
  </td>
</tr>

<tr>
  <td>

```clojure
zipmap keys vals → (k1 v1 ... kn vn)
zipmap [k1 k2] [v1] → (k1 v1)
zipmap [k] [v1 v2] → (k1 v1)
```
  </td>
  <td>
return a `map` with the keys associated with values.
  </td>
</tr>

<tr>
  <td>

```clojure
(map first
     (filter (comp #{:a :b} first)
             [[:a] [:d]]))
;=> (:a)
(keep (comp #{:a} first) [[:a] [:b]])
;=> (:a)
```
  </td>
  <td>
keep = map + filter
  </td>
</tr>

</table>

Inspect a `map` :

```clojure
(require 'clojure.inspector)
(clojure.inspector/inspect-tree map)
```

And finally, some other useful functions, from the excellent [Jay Fields's blog][1].

[1]: http://blog.jayfields.com/2012/09/replacing-common-code-with-clojureset.html|target=_blank

```clojure
(def jay {:fname "jay" :lname "fields" :employer "drw"})
(def mike {:fname "mike" :lname "jones" :employer "forward"})
(def john {:fname "john" :lname "dydo" :employer "drw"})

; returns a map whose keys are every employers and values are people defined above
(clojure.set/index [jay mike john] [:employer])
; => {{:employer "drw"} #{{:employer "drw" :fname "jay" :lname "fields"}
;                         {:employer "drw" :fname "john" :lname "dydo"}}
; {:employer "forward"} #{{:employer "forward" :fname "mike" :lname "jones"}}}

; projection
(clojure.set/project [jay mike john] [:fname :lname])
; => #{{:lname "fields", :fname "jay"}
;      {:lname "dydo", :fname "john"}
;      {:lname "jones", :fname "mike"}}

; key rename
(clojure.set/rename [jay mike john] {:fname :first-name :lname :last-name})
; => #{{:last-name "jones", :first-name "mike", :employer "forward"}
;      {:last-name "dydo", :first-name "john", :employer "drw"}
;      {:last-name "fields", :first-name "jay", :employer "drw"}}
```