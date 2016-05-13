{:title "Clojure - Cheat Sheet"
:layout :post
:excerpt "Notes on classic functions."
:tags  ["clojure" "leiningen"]
:toc true}

separation of concerns=responsabilité et dépendances entre composants
clairement définies.

```clojure
(def jay {:name "jay fields" :employer "drw"})
(def mike {:name "mike jones" :employer "forward"})
(def john {:name "john dydo" :employer "drw"})

(clojure.set/index [jay mike john] [:employer])

{{:employer "forward"} #{{:name "mike jones", :employer "forward"}},
{:employer "drw"} #{{:name "john dydo", :employer "drw"}
{:name "jay fields", :employer "drw"}}}

(def jay {:fname "jay" :lname "fields" :employer "drw"})
(def mike {:fname "mike" :lname "jones" :employer "forward"})
(def john {:fname "john" :lname "dydo" :employer "drw"})

(clojure.set/project [jay mike john] [:fname :lname])
#{{:lname "fields", :fname "jay"}
{:lname "dydo", :fname "john"}
{:lname "jones", :fname "mike"}}

(clojure.set/rename [jay mike john] {:fname :first-name :lname :last-name})
#{{:last-name "jones", :first-name "mike", :employer "forward"}
{:last-name "dydo", :first-name "john", :employer "drw"}
{:last-name "fields", :first-name "jay", :employer "drw"}}
```

* Table of Contents

<table>
<tr>
  <td>

```clojure
'a ⇔ (quote a)
[syntax quote] `a ⇔ (quote user/a)
;; Examples
(read-string "`{:a 100}")
`[:a ~(+ 1 1) ~'c d ~`e]
; [:a 2 c user/d user/e]
```
  </td>
  <td>
<blockquote><p>« There is no syntax quote »</p></blockquote>
"It's just code once its been read by the Clojure reader" @nandanbagchee
Syntax quoting apporte gensym optionnel (si # à
la fin du nom. Utile pour les macros qui créent
des var. locales comme let ou loop) + namespace
qualification des symbol selon l'environnement
en cours. produces code to reproduce the form.
On peut unquoter dans un sntax-quoted form
avec le tilde.
  </td>
</tr>

<tr>
  <td>

```clojure
and arg (.method arg)
```
  </td>
  <td>
return nil if arg is nil, otherwise execute `method` on arg.
Thus avoids nil check.
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
Evaluates ƒ (must not be a macro) on xn arguments prepended to the collection.
Has similarities with `unquote splicing` ~@
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
<ul><li>applied on a map, returns a map of the same type (hashed/sorted)
containing (or substituting) k/v of map and (by) those specified.</li>
<li>applied on a vector, replace the element at specified index or add it at
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
returns the same type of associative structure, with v the value of nested key
reached by k1 .. kn. If any kx level does not exist, hash-maps are created.
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
returns a map with all getters of the java object.
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
returns a function with undefined arity, applying fx (from right to left)
on arguments.
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
returns a sequence including all xk elements. Does not flatten nested colls.
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
returns a function with undefined arity, that always results in x.
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
returns true if the key k is present in the indexed collection (map and set),
o if the index exists in a vector. Do not use with lists.
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
optionally with implementation of protocols.
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
opposite of assoc. Returns an associative structure
of the same type than `map` but without the nested key
reached by k1 .. kn.
  </td>
</tr>

<tr>
  <td>

```clojure
doseq dorun doall

;; Examples

```
  </td>
  <td>
Force evaluation of lazy seqs (side effects).
unlike for, doseq never returns a value but nil.
doall retains the head and returns it.
dorun does not retain the head and returns nil.
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
runs body n times, from 0 to n-1
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
* if map, returns the map entry for key k or nil if not found.
* if vector, returns entry for index idx or nil if not found.
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
flattens the nested sequences
  </td>
</tr>

<tr>
  <td>

```clojure
fnil f x1 ... xn

;; Examples

```
  </td>
  <td>
returns a function that calls ƒ with xk
as an argument if the original argument is nil.
The arity of ƒ must be ≥ n.
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

;; Examples

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
returns with a map that indicates, for each separate element of `col`
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
returns a map of `col` elements, sorted by the return value of f applied
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
retourne une séquence contenant le premier
élément de chaque c x, puis le second, ...

returns a sequence containing the first element of each cx, then the second,

  </td>
</tr>

<tr>
  <td>

```clojure
interpose sep coll

;; Examples

```
  </td>
  <td>
returns a sequence of elements of the collection separated by the
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
returns a collection of the same type than `to`, appending all elements of
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
returns a function that returns a vector whose elements are the application
of ƒ on the argument.
  </td>
</tr>

<tr>
  <td>

```clojure
keep f coll
keep-indexed f coll

;; Examples
(keep #(if (odd? %) %) (range 10))
; (1 3 5 7 9)
(map #(if (odd? %) %) (range 10))
; (nil 1 nil 3 nil 5 nil 7 nil 9)

```
  </td>
  <td>
retourne une seq de tous les résultats non nil de
l'application de f sur chaque élement de col. Les
résultats false sont inclus.
returns a sequence *****************
ƒ must be a pure function.
keep-indexed utilise une fonction de type fn
[idx v]

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
retourne une liste contenant tous les
arguments x (évt. nil). Contrairement à la
notation literal list '(...), les éléments sont
évalués avant insertion.

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
retourne une liste contenant tous les
arguments x (évt. nil) ainsi que les éléments de
la séquence s (si non vide et non nulle).

  </td>
</tr>

<tr>
  <td>

```clojure
[p]map f c1 c2 ...
→ ((f c11 c21 ...)
   (f c12 c22 ...) ...)

[p]map f c ; ((f c1) (f c2) ...)

[p] :m means parallel

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
→ seq contenant résultats d'application de ƒ
aux premiers éléments de chaque collection,
puis 2nd, ...
ƒ doit avoir autant d'arguments qu'il y a de c x. Si
une collection a trop d'éléments ils seront
ignorés.
Avec une seule collection, applique la fonction
sur tous ses éléments.
mapv : la même chose mais retourne un vecteur
(donc non lazy)

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
Équivalent à (apply concat (map f c1 ... cn))
applique concat sur le résultat de l'application
de map sur ƒ et les collections.

  </td>
</tr>

<tr>
  <td>
```clojure
memoize f

;; Examples

```
  </td>
  <td>
retourne une version avec cache de ƒ, qui doit
être pure.

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
docstring alternative.
En règle générale les métadonnées n'affectent
pas l'égalité.

  </td>
</tr>

<tr>
  <td>

```clojure
or supplied-val default-val

;; Examples

```
  </td>
  <td>
retourne supplied-val si non nulle, default-val
sinon.

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
retourne une fonction qui prend n arguments
de moins que ce que requiert ƒ.

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
retourne une lazy seq de listes de n éléments
chacune. Si la dernière liste à moins de n
éléments elle n'est pas ajoutée. le 'step', qui vaut
n par défaut est le décalage pour la création de
chaque liste. 'pad' est une liste qui vise à
compléter la dernière si < n éléments.

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
comme partition, mais construit également la
dernière liste même s'il y a moins de 'n'
éléments.

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
comme partition, mais coupe la liste à chaque
fois que ƒ change de valeur

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

;; Examples

```

  </td>
  <td>
Sauf lorsque non utilisée ou indiqué, ƒ doit
proposer une arité avec 2 arguments.
Retourne l'accumulateur.

  </td>
</tr>

<tr>
  <td>

```clojure
reductions

;; Examples

```
  </td>
  <td>
renvoie une séquence des étapes intermédiaires
de reduce.

  </td>
</tr>

<tr>
  <td>

```clojure
repeatedly f → '(f f f ...)
repeatedly n f → '(f f f ... n)

;; Examples

```
  </td>
  <td>
ƒ est sans arguments, évt. impure.
Séquence infinie (ou de taille n) d'appels
successifs à f.

  </td>
</tr>

<tr>
  <td>

```clojure
repeat x → '(x x x ...)
repeat n x → '(x x x ...n)

;; Examples

```
  </td>
  <td>
Séquence infinie (ou de taille n) de x. Si c'est
une fonction, 1 seul appel est effectué.

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
retourne une séquence à partir de la collection
col. pour une collection nil ou vide, seq et
sequence se comportent différemment.

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
retourne une seq triée.

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
retourne un vecteur de [(take n coll) (drop n coll)]

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
retourne un vecteur de [(take-while pred coll)
(drop-while pred coll)]

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
prend le premier et tous les nièmes éléments de c.

  </td>
</tr>

<tr>
  <td>

```clojure
take-while pred coll

;; Examples

```
  </td>
  <td>
retourne les éléments de coll tant que pred est
vrai

  </td>
</tr>

<tr>
  <td>

```clojure
tranpoline f & args

;; Examples

```
  </td>
  <td>
pour la récursion mutuelle sans comsommation
de la stack. Effectue le ping pong tant que ce
qui est retourné est une fonction.

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
retourne une structure associative identique à
map mais avec la valeur de la nested clef
atteinte par k1 .. k n mise à jour par ƒ et ses
arguments optionnels.
Si un niveau kx n'existe pas, des hash-maps
seront créées.

  </td>
</tr>

<tr>
  <td>

```clojure
vec c → [c1 c2 ...]
vec nil → []

;; Examples

```
  </td>
  <td>
retourne un vecteur contenant les éléments de
c.

  </td>
</tr>

<tr>
  <td>

```clojure
vector x1 x2 ... → [x1 x2 ...]
vector nil → [nil]

;; Examples

```
  </td>
  <td>
retourne un vecteur contenant tous les
arguments x.

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
retourne un vecteur de type primitif (:int
:long :float :double :byte :short :char ou
:boolean) contenant les arguments optionnels
x.

  </td>
</tr>

<tr>
  <td>

```clojure
zipmap keys vals → (k1 v1 ... kn vn)
zipmap [k1 k2] [v1] → (k1 v1)
zipmap [k] [v1 v2] → (k1 v1)

;; Examples

```
  </td>
  <td>
retourne une map avec les clefs associées aux
valeurs.

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

;; Examples

```
  </td>
  <td>
keep = map + filter
  </td>
</tr>

</table>

**Predicates**

```clojure
(every? odd? [1 3 5]) ; true
(not-any? zero? [1 2 3]) ; true
(not-every? even? [2 3 4]) ; true
(some nil? [1 nil 2]) ; true
```

| a | b  | sequential? | associative? | sorted? | counted? | reversible? | coll? | seq? | vector? | list? | map? | set? |
|:-----|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|:----:|
| list | '() | ✔ | | | ✔ | ✔ | ✔ | | | ✔ | | |
| vector | [] | ✔ | ✔ | | ✔ | ✔ | ✔ | | ✔ | | | |
| map | {} | | ✔ | sorted-map | ✔ | | ✔ | | | | ✔ | |
| seq | | ✔ | | | | | | ✔ | | | | |
| struct, record | | | ✔ | | ✔ | | ✔ | | | | ✔ | |
| set | #{} | | | sorted-map | ✔ | | ✔ | | | | | ✔ |
