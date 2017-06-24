
val add3 = recNNNN(m1 :-> (m2 :-> add(m1)(m2) ))( n :-> (add3n :-> (m1 :-> (m2 :-> succ(add3n(m1)(m2)) )))) 
val mult = recNNN(m :-> zero)( n :-> (multn :-> (m :-> add(multn(m))(m) )))


import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNNN = NatInd.rec(Nat ->: Nat)
val addn = "add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val multn = "mult(n)" :: Nat ->: Nat
//val mult = recNNN(m :-> ???)(n :-> (multn :-> (m :-> ??? )))
val mult = recNNN(m :-> zero)( n :-> (multn :-> (m :-> add(multn(m))(m) )))
val powm = "pow(_, m)" :: Nat ->: Nat
val pow_flip = recNNN(n :-> one)(m :-> (powm :-> (n :-> mult(powm(n))(n) )))

val pow = n :-> (m :-> pow_flip(m)(n) )
pow(two)(three).fansi
pow(two)(three) == add(six)(two) 


val recNN = NatInd.rec(Nat)
val fact = recNN(one)(n :-> (m :-> mult(succ(n))(m)))


import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val Nat = "Nat" :: Type
val n = "n" :: Nat

val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
//val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
﻿val recNB = NatInd.rec(Bool)
val isZero = recNB(tru)(n :-> (b :-> fls))
﻿isZero(zero) == tru 
isZero(one) == fls ﻿


import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBB = BoolInd.rec(Bool)
val not = recBB(fls)(tru)
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val recNB = NatInd.rec(Bool)
val isEven = recNB(tru)(n :-> (b :-> not(b)))
val isOdd = recNB(fls)(n :-> (b :-> not(b)))
isEven(two) == tru
isEven(three) == fls
isOdd(two) == fls
isOdd(three) == tru


import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNN = NatInd.rec(Nat)
val pred = recNN(zero)(n :-> (m :-> n))
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val b1 = "b1" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recNB = NatInd.rec(Bool)
val isZero = recNB(tru)(n :-> (b :-> fls))
val recBBBB = BoolInd.rec(Bool ->: Bool ->: Bool)
val ifElse = recBBBB(b :-> (b1 :-> b))(b :-> (b1 :-> b1))
val recNNB = NatInd.rec(Nat ->: Bool)
val isEqualn = "isEqual(n)" :: Nat ->: Bool
//﻿val isEqual = recNNB(m :->
//  isZero(m)
//)(n :-> (isEqualn :-> (m :->
//  ifElse(isZero(m))(isZero(m))(isEqualn(n)﻿)
//)))

val isEqual = recNNB(m :-> 
  isZero(m)
  )(n :-> (isEqualn :-> (m :->
  ifElse(isZero(m))(fls)(isEqualn(pred(m)))
  ))) 

isEqual(two)(two).fansi 
isEqual(one)(three).fansi

import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNN = NatInd.rec(Nat)
val pred = recNN(zero)(n :-> (m :-> ???))
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val b1 = "b1" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBB = BoolInd.rec(Bool)
val not = recBB(fls)(tru)﻿
val recNB = NatInd.rec(Bool)
//val isZero = recNB(???)(n :-> (b :-> ???))
val recBBBB = BoolInd.rec(Bool ->: Bool ->: Bool)
//val ifElse = recBBBB(b :-> (b1 :-> ???))(b :-> (b1 :-> ???))
val recNNB = NatInd.rec(Nat ->: Bool)
val isLessn = "isLess(n)" :: Nat ->: Bool
val isLess = recNNB(m :->
  //???
  not(isZero(m))
)(n :-> (isLessn :-> (m :->
  ifElse(isZero(m))(tru)(isLessn(pred(m)))
)))


val isLess = recNNB(m :->
  not(isZero(m))
  )(n :-> (isLessn :-> (m :->
  ifElse(isZero(m))(fls)(isLessn(pred(m)))
  ))) 

val isGreatern = "isGreater(n)" :: Nat ->: Bool
val isGreater = recNNB(m :->
  //???
  not(isZero(m))
)(n :-> (isGreatern :-> (m :->
  //ifElse(isZero(m))(???)(???) ))
  ifElse(isZero(m))(fls)(isLessn(pred(m)))
)))

val isGreater = recNNB(m :->
  fls
  )(n :-> (isGreatern :-> (m :->
  ifElse(isZero(m))(tru)(isGreatern(pred(m)))
  ))) 


isLess﻿(two)(two).fansi 
//> res: String = "false"
isLess﻿(one)(three).fansi 
//> res: String = "true"
isLess(three)(one).fansi 
//> res: String = "false"﻿


import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val n = "n" :: Nat
val m = "m" :: Nat
val recNNN = NatInd.rec(Nat ->: Nat)
val addn = "add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val recNNNN = NatInd.rec(Nat ->: Nat ->: Nat)
val m1 = "fib(n)" :: Nat
val m2 = "fib(n+1)" :: Nat﻿
val fibn = "fib_aux(n,_,_)" :: Nat ->: Nat ->: Nat
//val fib_aux = recNNNN(m1 :-> (m2 :-> ???))(n :-> (fibn :-> (m1 :-> (m2 :-> ??? ))))
//val fib_aux = recNNNN(m1 :-> (m2 :-> m1))(n :-> (fibn :-> (m1 :-> (m2 :-> add(fibn(m1)(m2))(fibn(m1)(n)) ))))
val fib_aux = recNNNN(m1 :-> (m2 :-> m1))(n :-> (fibn :-> (m1 :-> (m2 :-> fibn(m2)(add(m1)(m2)) ))))
//TODO: my problem is the fibn function what does it do
//I need to write down the induction process step by step after this class
val fib = n :-> fib_aux(n)(zero)(one)
fib(zero).fansi 
//> res: String = "0"
fib(one).fansi 
//> res: String = "succ(0)"
fib(two).fansi 
//> res: String = "succ(0)"
fib(three).fansi 
//> res: String = "succ(succ(0))"
fib(four).fansi 
//> res: String = "succ(succ(succ(0)))"
fib(five).fansi 
//> res: String = "succ(succ(succ(succ(succ(0)))))"


import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val Bool = "Boolean" :: Type
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val b = "b" :: Bool
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNB = NatInd.rec(Bool)
//val isZero = recNB(???)(n :-> (b :-> ???))
val recNN = NatInd.rec(Nat)
//val pred = recNN(zero)(n :-> (m :-> ???))
val recBNNN = BoolInd.rec(Nat ->: Nat ->: Nat)
//val ifElse = recBNNN(n :-> (m :-> ???))(n :-> (m :-> ???))
val recNNN = NatInd.rec(Nat ->: Nat)
val subtractn = "subtract(n)" :: Nat ->: Nat
//val subtract = recNNN(m :-> zero)(n :-> (subtractn :-> (m :->
//ifElse(isZero(m))(n)(subtract(pred(m)))))
//val ifElse = recBNNN(b :-> (b1 :-> b))(b :-> (b1 :-> b1))
val ifElse = recBNNN(n :-> (m :-> n))(n :-> (m :-> m))
val subtract = recNNN(m :-> zero)(n :-> (subtractn :-> (m :->
  ifElse(isZero(m))(succ(n))(subtractn(pred(m))) ))) 
subtract(four)(two).fansi 
//> res: String = "succ(succ(0))"
subtract(four)(two) == two 
subtract(two)(two) == zero 
//> res: Boolean = true

import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNProdNN = NatInd.rec(ProdTyp(Nat, Nat))
val pair = "(half(n), half(n+1))" :: ProdTyp(Nat, Nat)
//val recNB = NatInd.rec(Bool)
//val isEven = recNB(tru)(n :-> (b :-> not(b)))
//val ifElse = recBNNN(n :-> (m :-> n))(n :-> (m :-> m))
//val halfpair = recNProdNN(PairTerm(???, ???))(n :-> (pair :-> PairTerm(???, ???) ))
val halfpair = recNProdNN(PairTerm(zero, zero))(n :-> (pair :->
PairTerm(ifElse(isEven(n))(pair.first)(succ(pair.first)), ifElse(isEven(n))(pair.first)(succ(pair.first)))
))

val halfpair = recNProdNN(PairTerm(zero, zero))(n :-> (pair :-> PairTerm(pair.second, succ(pair.first))))


val half = n :-> halfpair(n).first
half(six).fansi
//> res: String = "succ(succ(succ(0)))"
half(five).fansi
//> res: String = "succ(succ(0))"




import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNNN = NatInd.rec(Nat ->: Nat)
val addn = "add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val recNProdNN = NatInd.rec(ProdTyp(Nat, Nat))
val pair = "(fib(n), fib(n+1))" :: ProdTyp(Nat, Nat)
//val fibpair = recNProdNN(PairTerm(???, ???))(n :-> (pair :-> PairTerm(???, ??? ))
val fibpair = recNProdNN(PairTerm(zero, one))(n :-> (pair :-> PairTerm(pair.second, add(pair.first)(pair.second))))
val fib = n :-> fibpair(n).first
fib(six).fansi 
//> res: String = "succ(succ(succ(succ(succ(succ(succ(succ(0))))))))"




import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val recAAA = BoolInd.rec(A ~>: (A ->: A ->: A))
//val ifElse = recAAA(A :~> (a :-> (a1 :-> ???)))(A :~> (a :-> (a1 :-> ???)))
val ifElse = recAAA(A :~> (a :-> (a1 :-> a)))(A :~> (a :-> (a1 :-> a1)))
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
ifElse(tru)(Nat)(one)(two) == one
ifElse(fls)(Nat)(one)(two) == two
ifElse(tru)(Bool)(fls)(tru) == fls
ifElse(fls)(Bool)(fls)(tru) == tru

import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val recLA = ListAInd.rec(A)
val errorEl = "error" :: A
val head = recLA(errorEl)(a :-> (as :-> (a1 :-> a)))
//val head = recLA(errorEl)(a :-> (as :-> (a1 :-> a1)))
//TODO: what is a1 then ????
val list = cons(a)(cons(a1)(cons(a2)(nil)))
head(list) == a
//> res: Boolean = true

import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val as1 = "as1" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val recLL = ListAInd.rec(ListA)
val errorList = "error" :: ListA
val tail = recLL(errorList)(a :-> (as :-> (as1 :-> as)))
//val tail = recLL(errorList)(a :-> (as :-> (as1 :-> ???)))
val list = cons(a)(cons(a1)(cons(a2)(nil)))
tail(list).fansi
//> res: String = "cons(a1)(cons(a2)(nil))"



import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recLB = ListAInd.rec(Bool)
//val isNil = recLB(???)(a :-> (as :-> (b :-> ???)))
val isNil = recLB(tru)(a :-> (as :-> (b :-> fls)))
isNil(nil) == tru 
//> res: Boolean = true
isNil(cons(a)(nil)) == fls 
//> res: Boolean = true

import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val recLA = ListAInd.rec(A)
val errorEl = "error" :: A
val list = cons(a)(cons(a1)(cons(a2)(nil)))
val Bool = "Boolean" :: Type
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
﻿val recBAAA = BoolInd.rec(A ->: A ->: A)
val ifElse = recBAAA(a :-> (a1 :-> a))(a :-> (a1 :-> a1))
//val last = recLA(errorEl)(a :-> (as :-> (a1 :-> ﻿ifElse(isNil(???))(???)(???) ﻿)))
val last = recLA(errorEl)(a :-> (as :-> (a1 :-> ﻿ifElse(isNil(as))(a)(a1) ﻿)))
val last = recLA(errorEl)(a :-> (as :-> (a1 :-> ifElse(isNil(as))(a)(a1) )))
last(list) == a2 
//> ﻿res: Boolean = true


import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val as1 = "as1" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val list = cons(a)(cons(a1)(cons(a2)(nil)))
val errorList = "error" :: ListA
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBLLL = BoolInd.rec(ListA ->: ListA ->: ListA)
//val ifElse = recBLLL(as :-> (as1 :-> ???))(as :-> (as1 :-> ???))
val ifElse = recBLLL(as :-> (as1 :-> as))(as :-> (as1 :-> as1))
val recLB = ListAInd.rec(Bool)
//val isNil = recLB(???)(a :-> (as :-> (b :-> ???)))

//val init = recLL(errorList)(a :-> (as :-> (as1 :-> ifElse(isNil(???))(???)(???) )))
val init = recLL(errorList)(a :-> (as :-> (as1 :-> ifElse(isNil(as))(nil)(cons(a)(as1)) )))
init(list).fansi
//> res: String = "cons(a)(cons(a1)(nil))"


import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val recLAL = ListAInd.rec(A ->: ListA)
val appendas = "append(as)" :: A ->: ListA
//val append = recLAL(a1 :-> ???)(a :-> (as :-> (appendas :-> (a1 :-> ??? ))))
//TODO: have problems deducing the parameter types
val a3 = "a3" :: A
val list = cons(a)(cons(a1)(cons(a2)(nil)))

//@ recLAL.typ.fansi 
res739: String = "((A → List(A)) →      ((A → (List(A) →       ((A → List(A)) → (A   List(A))))) → (List(A) → (A → List(A)))))"
val append = recLAL(a1 :-> cons(a1)(nil))(a :-> (as :-> (appendas :-> (a1 :-> cons(a)(appendas(a1)) )))) 
append(list)(a3).fansi 

//> res: String = "cons(a)(cons(a1)(cons(a2)(cons(a3)(nil))))"

val append = recLAL(a1 :-> cons(a1)(nil))(a :-> (as :-> (appendas :-> (a1 :-> cons(a)(appendas(a1)) ))))



import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val as1 = "as1" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val recLAL = ListAInd.rec(A ->: ListA)
val appendas = "append(as)" :: A ->: ListA
//val append = recLAL(a1 :-> ???)(a :-> (as :-> (appendas :-> (a1 :-> ??? ))))
val append = recLAL(a1 :-> cons(a1)(nil))(a :-> (as :-> (appendas :-> (a1 :-> cons(a)(appendas(a1)) ))))
val recLL = ListAInd.rec(ListA)
//val revert = recLL(nil)(a :-> (as :-> (as1 :-> ??? )))
val revert = recLL(nil)(a :-> (as :-> (as1 :->  append(as1)(a) )))
val list = cons(a)(cons(a1)(cons(a2)(nil)))
revert(list).fansi
//> res: String = "cons(a2)(cons(a1)(cons(a)(nil)))"









import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val as1 = "as1" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val list = cons(a)(cons(a1)(cons(a2)(nil)))
val recLLL = ListAInd.rec(ListA ->: ListA)
val concatas = "concat(as)" :: ListA ->: ListA
val a3 = "a3" :: A
val a4 = "a4" :: A
val list1 = cons(a3)(cons(a4)(nil))

//val concat = recLLL(as1 :-> ???)(a :-> (as :-> (concatas :-> (as1 :-> ??? ))))
val concat = recLLL(as1 :-> as1)(a :-> (as :-> (concatas :-> (as1 :-> cons(a)(concatas(as1))    ))))
concat(list)(list1).fansi
//> res: String = "cons(a)(cons(a1)(cons(a2)(cons(a3)(cons(a4)(nil)))))"






import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val ListA = "List(A)" :: Type
val as = "as" :: ListA
val as1 = "as1" :: ListA
val ListAInd = ("nil" ::: ListA) |: ("cons" ::: A ->>: ListA -->>: ListA ) =: ListA
val nil :: cons :: HNil = ListAInd.intros
val Nat = "Nat" :: Type
val n = "n" :: Nat
val m = "m" :: Nat
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val recNN = NatInd.rec(Nat)
val pred = recNN(zero)(n :-> (m :-> n))
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recNB = NatInd.rec(Bool)
val isZero = recNB(tru)(n :-> (b :-> fls))
val recLNL = ListAInd.rec(Nat ->: ListA)
val takeas = "take(as)" :: Nat ->: ListA

val recBLLL = BoolInd.rec(ListA ->: ListA ->: ListA)
val ifElse = recBLLL(as :-> (as1 :-> as))(as :-> (as1 :-> as1))
//val take = recLNL(n :-> ???)(a :-> (as :-> (takeas :-> (n :-> ifElse(isZero(???))(???)(???) ))))
val list = cons(a)(cons(a1)(cons(a2)(nil)))

// solution 1 #
//val take = recLNL(n :-> nil)(a :-> (as :-> (takeas :-> (n :-> ifElse(isZero(n))(takeas(n))(      cons(a)(takeas(pred(n))) )      ))))
val take = recLNL(n :-> nil)(a :-> (as :-> (takeas :-> (n :-> ifElse(isZero(n))(nil)(      cons(a)(takeas(pred(n))) )      ))))
take(list)(two).fansi
//> res: String = "cons(a)(cons(a1)(nil))"
val dropas = "drop(as)" :: Nat ->: ListA
//val drop = recLNL(n :-> ???)(a :-> (as :-> (dropas :-> (n :-> ifElse(isZero(???))(???)(???) ))))

val drop = recLNL(n :-> nil)(a :-> (as :-> (dropas :-> (n :-> ifElse(isZero(n))(cons(a)(as))(dropas(pred(n)))             ))))
drop(list)(one).fansi
//> res: String = "cons(a1)(cons(a2)(nil))"




import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val B = "B" :: Type
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val indLAfLB = ListInd.induc(A :~> (as :-> (B ~>: ((A ->: B) ->: List(B) ))))
val f = "f" :: A ->: B
val mapas = "map(as)" :: B ~>: ((A ->: B) ->: List(B))
val list = cons(Nat)(zero)(cons(Nat)(one)(cons(Nat)(two)(nil(Nat))))

//val map = indLAfLB(A :~> (B :~> (f :-> ???)))(A :~> (a :-> (as :-> (mapas :-> (B :~> (f :-> ???                     ))))))
val map = indLAfLB(A :~> (B :~> (f :->   nil(B))))(A :~> (a :-> (as :-> (mapas :-> (B :~> (f :-> cons(B)(f(a))(mapas(B)(f)) ))))))
map(Nat)(list)(Nat)(succ).fansi 
//> res: String = "cons(Nat)(succ(0))(cons(Nat)(succ(succ(0)))(cons(Nat)(succ(succ(succ(0))))(nil(Nat))))"
map(Nat)(list)(Nat)(succ) == cons(Nat)(one)(cons(Nat)(two)(cons(Nat)(three)(nil(Nat))))
//> res: Boolean = true
//TODO



import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val B = "B" :: Type
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val Nat = "Nat" :: Type
val n = "n" :: Nat
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBB = BoolInd.rec(Bool)
val not = recBB(fls)(tru)
val recBoolAAA = BoolInd.rec(A ~>: (A ->: A ->: A))
//val ifElse = recBoolAAA(A :~> (a :-> (a1 :-> ???)))(A :~> (a :-> (a1 :-> ???)))
val ifElse = recBoolAAA(A :~> (a :-> (a1 :-> a)))(A :~> (a :-> (a1 :-> a1)))
val recNB = NatInd.rec(Bool)
//val isEven = recNB(???)(n :-> (b :-> ???))
val isEven = recNB(tru)(n :-> (b :-> not(b)))
val indLApLA = ListInd.induc(A :~> (as :-> ((A ->: Bool) ->: List(A) )))
val p = "p" :: A ->: Bool
val filteras = "filter(as)" :: (A ->: Bool) ->: List(A)
//val filter = indLApLA(A :~> (p :-> nil(A) ))(A :~> (a :-> (as :-> (filteras :-> (p :-> ??? )))))

val list = cons(Nat)(zero)(cons(Nat)(one)(cons(Nat)(two)(nil(Nat))))
val filter = indLApLA(A :~> (p :-> nil(A) ))(A :~> (a :-> (as :-> (filteras :-> (p :-> ifElse(p(a))(List(A))(cons(A)(a)(filteras(p)))(filteras(p)) )))))
filter(Nat)(list)(isEven).fansi 
//> res: String = "cons(Nat)(0)(cons(Nat)(succ(succ(0)))(nil(Nat)))"
filter(Nat)(list)(isEven) == cons(Nat)(zero)(cons(Nat)(two)(nil(Nat)))
//> res: Boolean = truey





import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val B = "B" :: Type
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val Nat = "Nat" :: Type
val n = "n" :: Nat
val m = "m" :: Nat
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val list = cons(Nat)(zero)(cons(Nat)(one)(cons(Nat)(two)(nil(Nat))))
val op = "op" :: B ->: A ->: B
val seed = "seed" :: B
val foldlas = "foldl(as)" :: B ~>: ((B ->: A ->: B) ->: B ->: B)
val indLAopBB = ListInd.induc(A :~> (as :-> (B ~>: ((B ->: A ->: B) ->: B ->: B))))
//val foldl = indLAopBB(A :~> (B :~> (op :-> (seed :-> seed))))(A :~> (a :-> (as :-> (foldlas :-> (B :~> (op :-> (seed :-> ??? )))))))
val foldl = indLAopBB(A :~> (B :~> (op :-> (seed :-> seed))))(A :~> (a :-> (as :-> (foldlas :-> (B :~> (op :-> (seed :-> foldlas(B)(op)(op(seed)(a)) )))))))
foldl(Nat)(list)(Nat)(add)(zero).fansi 
//> res: String = "succ(succ(succ(0)))"
foldl(Nat)(list)(Nat)(add)(zero) == three
//> res: Boolean = true



import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val B = "B" :: Type
val seed = "seed" :: B
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val Nat = "Nat" :: Type
val n = "n" :: Nat
val m = "m" :: Nat
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val list = cons(Nat)(zero)(cons(Nat)(one)(cons(Nat)(two)(nil(Nat))))
val indLAopBB = ListInd.induc(A :~> (as :-> (B ~>: ((A ->: B ->: B) ->: B ->: B))))
val op = "op" :: A ->: B ->: B
val foldras = "foldr(as)" :: B ~>: ((A ->: B ->: B) ->: B ->: B)
//val foldr = indLAopBB(A :~> (B :~> (op :-> (seed :-> seed))))(A :~> (a :-> (as :-> (foldras :-> (B :~> (op :-> (seed :-> ??? ))))))
//val foldr = indLAopBB(A :~> (B :~> (op :-> (seed :-> seed))))(A :~> (a :-> (as :-> (foldras :-> (B :~> (op :-> (seed :-> foldras(B)(op)(op(a)(seed))) ))))))
val foldr = indLAopBB(A :~> (B :~> (op :-> (seed :-> seed))))(A :~> (a :-> (as :-> (foldras :-> (B :~> (op :-> (seed :-> op(a)(foldras(B)(op)(seed))) ))))))
foldr(Nat)(list)(Nat)(add)(zero).fansi 
//> res: String = "succ(succ(succ(0)))"
foldr(Nat)(list)(Nat)(add)(zero) == three
//> Res: Boolean = true



import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val B = "B" :: Type
val b = "b" :: B
val b1 = "b1" :: B
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val as1 = "as1" :: List(A)
val bs = "bs" :: List(B)
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val Bool = "Boolean" :: Type
val bool = "bool" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBoolAAA = BoolInd.rec(A ~>: (A ->: A ->: A))
//val ifElse = recBoolAAA(A :~> (a :-> (a1 :-> ???)))(A :~> (a :-> (a1 :-> ???)))﻿
val ifElse = recBoolAAA(A :~> (a :-> (a1 :-> a)))(A :~> (a :-> (a1 :-> a1)))
val indLABool = ListInd.rec(Bool)
//val isNil = indLABool(A :~> ???)(A :~> (a :-> (as :-> (bool :-> ???))))
val isNil = indLABool(A :~> tru)(A :~> (a :-> (as :-> (bool :-> fls))))
val indLAA = ListInd.induc(A :~> (as :-> A))
val errorEl = "error" :: A
//val head = indLAA(A :~> errorEl)(A :~> (a :-> (as :-> (a1 :-> ???))))
val head = indLAA(A :~> errorEl)(A :~> (a :-> (as :-> (a1 :-> a))))
val indLALA = ListInd.induc(A :~> (as :-> List(A) ))
val errorList = "error" :: List(A)
//val tail = indLALA(A :~> errorList)(A :~> (a :-> (as :-> (as1 :-> ???))))
val tail = indLALA(A :~> errorList)(A :~> (a :-> (as :-> (as1 :-> as))))
val indLALBLAB = ListInd.induc(A :~> (as :-> (B ~>: (List(B) ->: List(ProdTyp(A, B))))))
val zipas = "zip(as)" :: B ~>: (List(B) ->: List(ProdTyp(A, B)))
val list = cons(Nat)(zero)(cons(Nat)(one)(cons(Nat)(two)(nil(Nat))))
val list1 = cons(Bool)(tru)(cons(Bool)(fls)(cons(Bool)(tru)(cons(Bool)(fls)(nil(Bool)))))



val zip = indLALBLAB(A :~> (B :~> (bs :->
  nil(ProdTyp(A,B))
  )))(A :~> (a :-> (as :-> (zipas :-> (B :~> (bs :->
  //ifElse(isNil(B)(bs))(List(ProdTyp(A, B)))(???)(???)
  ifElse(isNil(B)(bs))(List(ProdTyp(A, B)))(nil(ProdTyp(A,B)))(cons(ProdTyp(A,B))(PairTerm(a,head(B)(bs)))(zipas(B)(tail(B)(bs))))
  ))))))

zip(Nat)(list)(Bool)(list1).fansi
//>  res: String = "cons(Nat×Boolean)((0 , true))(cons(Nat×Boolean)((succ(0) , false))(cons(Nat×Boolean)((succ(succ(0)) , true))(nil(Nat×Boolean))))"﻿
//res162: String = "cons(Nat×Boolean)((0 , b))(cons(Nat×Boolean)((succ(0) , b))(cons(Nat×Boolean)((succ(succ(0)) , b))(nil(Nat×Boolean))))"

zip(Nat)(list)(Bool)(list1) == cons(ProdTyp(Nat, Bool))(
    PairTerm(zero, tru))(
    cons(ProdTyp(Nat, Bool))(
      PairTerm(one, fls))(
      cons(ProdTyp(Nat, Bool))(
        PairTerm(two, tru))(
        nil(ProdTyp(Nat, Bool))
      )
    )
  )
> res: Boolean = true















import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val List = "List" :: Type ->: Type
val ListInd = ("nil" ::: A ~>>: (List -> List(A))) |: ("cons" ::: A ~>>: (A ->>: (List :> List(A)) -->>: (List -> List(A)) )) =:: List
val nil :: cons :: HNil = ListInd.intros
val as = "as" :: List(A)
val as1 = "as1" :: List(A)
val Bool = "Boolean" :: Type
val bool = "bool" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBBB = BoolInd.rec(Bool ->: Bool)
val and = recBBB(bool :-> bool)(bool :-> fls)
val recBoolAAA = BoolInd.rec(A ~>: (A ->: A ->: A))
val ifElse = recBoolAAA(A :~> (a :-> (a1 :-> a)))(A :~> (a :-> (a1 :-> a1)))
val Nat = "Nat" :: Type
val n = "n" :: Nat
val m = "m" :: Nat
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val recNB = NatInd.rec(Bool)
val isZero = recNB(tru)(n :-> (bool :-> fls))
val recNN = NatInd.rec(Nat)
val pred = recNN(zero)(n :-> (m :-> n))
val recNNB = NatInd.rec(Nat ->: Bool)
val isEqualn = "isEqual(n)" :: Nat ->: Bool
val isEqualNat = recNNB(m :->
  isZero(m)
)(n :-> (isEqualn :-> (m :->
  //ifElse(isZero(m))(Bool)(???)(??? ))
  ifElse(isZero(m))(Bool)(fls)(isEqualn(pred(m)))))
)
//val isEqual = recNNB(m :-> 
//  isZero(m)
//  )(n :-> (isEqualn :-> (m :->
//  ifElse(isZero(m))(fls)(isEqualn(pred(m)))
//  ))) 
val recLABool = ListInd.rec(Bool)
val isNil = recLABool(A :~> tru)(A :~> (a :-> (as :-> (bool :-> fls))))
val indLAA = ListInd.induc(A :~> (as :-> A))
val errorEl = "error" :: A
val head = indLAA(A :~> errorEl)(A :~> (a :-> (as :-> (a1 :-> a))))
val indLALA = ListInd.induc(A :~> (as :-> List(A) ))
val errorList = "error" :: List(A)
val tail = indLALA(A :~> errorList)(A :~> (a :-> (as :-> (as1 :-> as))))

val indLALABool = ListInd.induc(A :~> (as :-> (List(A) ->: (A ->: A ->: Bool) ->: Bool)))
val isEqualEl = "isEqual_A" :: A ->: A ->: Bool
val isEqualas = "isEqual(as)" :: List(A) ->: (A ->: A ->: Bool) ->: Bool
val isEqual = indLALABool(A :~> (as1 :-> (isEqualEl :->
  //isNil(???)(???)
  isNil(A)(as1)
  )))(A :~> (a :-> (as :-> (isEqualas :-> (as1 :-> (isEqualEl :->
  //ifElse(isNil(A)(???))(Bool)(???)(???)
  ifElse(isNil(A)(as1))  (Bool)   (fls) (and (isEqualEl(a)(head(A)(as1))) (isEqualas(tail(A)(as1))(isEqualEl)))
  //ifElse(isNil(A)(as1))  (Bool)   (fls) (and (isEqualEl(head(A)(as1))(a)) (isEqualas(tail(A)(as1))(isEqualEl)))
  ))))))

val list = cons(Nat)(one)(cons(Nat)(two)(nil(Nat)))
val list1 = cons(Nat)(one)(cons(Nat)(two)(cons(Nat)(three)(nil(Nat))))
isEqual(Nat)(list)(list1)(isEqualNat) == fls
isEqual(Nat)(list)(list)(isEqualNat) == tru
isEqual(Nat)(list1)(list1)(isEqualNat) == tru
//TODOtodo
isEqual(A)(as)(as1)(isEqualEl).fansi.take(16) ++ " " ++ isEqual(A)(as)(as1)(isEqualEl).fansi.takeRight(204)
//"induc(List(A))($ ↦ error)((A : 𝒰 ) ↦ (a : A) ↦ (as : List(A)) ↦ (as1 : List(A)) ↦ as)(as1))(isEqual_A))))(as)(as1)(isEqual_A)" 




import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val a3 = "a3" :: A
val a4 = "a4" :: A

val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros

val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)

val n = "n" :: Nat
val m = "m" :: Nat

val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))

val Vec = "Vec" :: Nat ->: Type
val VecInd = ("nil" ::: (Vec -> Vec(zero))) |: { "cons" ::: n ~>>: (A ->>: (Vec :> Vec(n)) -->>: (Vec -> Vec(succ(n))))} =:: Vec
val vnil :: vcons :: HNil = VecInd.intros
val vn = "v_n" :: Vec(n)

val vect = vcons(one)(a)(vcons(zero)(a1)(vnil))

val indVAV = VecInd.induc(n :~> (vn :-> (A ->: Vec(succ(n)) )))
val appendvn = "append(v_n)" :: A ->: Vec(succ(n))
val append = indVAV(a :-> vcons(zero)(a)(vnil))(n :~> (a1 :-> (vn :-> (appendvn :-> (a :-> vcons(succ(n))(a1)(appendvn(a)) )))))
append(two)(vect)(a2).fansi 
//> res: String = "cons(succ(succ(0)))(a)(cons(succ(0))(a1)(cons(0)(a2)(nil)))"
append(two)(vect)(a2) == vcons(two)(a)(vcons(one)(a1)(vcons(zero)(a2)(vnil)))
//> res: Boolean = true

append(succ(succ(n))) (append (succ(n)) (append (n) (vn) (a1)) (a2)) (a3).typ﻿











import TLImplicits._
import shapeless._
val A = "A" :: Type
val a = "a" :: A
val a1 = "a1" :: A
val a2 = "a2" :: A
val a3 = "a3" :: A
val a4 = "a4" :: A

val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros

val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)

val n = "n" :: Nat
val m = "m" :: Nat

val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))

val Vec = "Vec" :: Nat ->: Type
val VecInd = ("nil" ::: (Vec -> Vec(zero))) |: { "cons" ::: n ~>>: (A ->>: (Vec :> Vec(n)) -->>: (Vec -> Vec(succ(n))))} =:: Vec
val vnil :: vcons :: HNil = VecInd.intros
val vn = "v_n" :: Vec(n)
val vm = "v_m" :: Vec(m)

val vect = vcons(one)(a)(vcons(zero)(a1)(vnil))
val vect1 = vcons(two)(a2)(vcons(one)(a3)(vcons(zero)(a4)(vnil)))

val indVVV = VecInd.induc(n :~> (vn :-> (m ~>: (Vec(m) ->: Vec(add(n)(m)) ))))
val concatVn = "concat(v_n)" :: (m ~>: (Vec(m) ->: Vec(add(n)(m)) ))
//val vconcat = indVVV(m :~> (vm :-> ???))(n :~> (a :-> (vn :-> (concatVn :-> (m :~> (vm :-> ??? ))))))
val vconcat = indVVV(m :~> (vm :-> vm))(n :~> (a :-> (vn :-> (concatVn :-> (m :~> (vm :->  vcons(add(n)(m))(a)(concatVn(m)(vm)) ))))))

vconcat(two)(vect)(three)(vect1).fansi 
//> res: String = "cons(succ(succ(succ(succ(0)))))(a)(cons(succ(succ(succ(0))))(a1)(cons(succ(succ(0)))(a2)(cons(succ(0))(a3)(cons(0)(a4)(nil)))))"

vconcat(two)(vect)(three)(vect1) == vcons(four)(a)(vcons(three)(a1)(vcons(two)(a2)(vcons(one)(a3)(vcons(zero)(a4)(vnil)))))
//> res: Boolean = true
//Enter below the output of command (after symbol = )
vconcat(m)(vm)(n)(vn).typ






import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros

val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)

val n = "n" :: Nat
val m = "m" :: Nat

val recNN = NatInd.rec(Nat)
val errorN = "error" :: Nat
//val pred = recNN(errorN)(n :-> (m :-> ???))
val pred = recNN(errorN)(n :-> (m :-> n))


val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))

val Vec = "Vec" :: Nat ->: Type
val VecInd = ("nil" ::: (Vec -> Vec(zero))) |: { "cons" ::: n ~>>: (Nat ->>: (Vec :> Vec(n)) -->>: (Vec -> Vec(succ(n))))} =:: Vec
val vnil :: vcons :: HNil = VecInd.intros

val vn = "v_n" :: Vec(n)
val wn = "w_n" :: Vec(n)
val vm = "v_m" :: Vec(m)

val a = "a" :: Nat
val a1 = "a1" :: Nat
val a2 = "a2" :: Nat
val a3 = "a3" :: Nat
val a4 = "a4" :: Nat
val vect = vcons(one)(a)(vcons(zero)(a1)(vnil))
val vect1 = vcons(two)(a2)(vcons(one)(a3)(vcons(zero)(a4)(vnil)))

val recVN = VecInd.rec(Nat)
val errorEl = "error" :: Nat
//val vhead = recVN(errorEl)(n :~> (a :-> (vn :-> (a1 :-> ???))))
val vhead = recVN(errorEl)(n :~> (a :-> (vn :-> (a1 :-> a))))
vhead(two)(vect) == a

val indVnVpn = VecInd.induc(n :~> (vn :-> Vec(pred(n)) ))
val errorList = "error" :: Vec(pred(zero))
val vpn = "v_(pred(n))" :: Vec(pred(n))
//val vtail = indVnVpn(errorList)(n :~> (a :-> (vn :-> (vpn :-> ???))))
val vtail = indVnVpn(errorList)(n :~> (a :-> (vn :-> (vpn :-> vn))))
vtail(two)(vect) == vcons(zero)(a1)(vnil)

val indNVnVnVn = NatInd.induc(n :-> (Vec(n) ->: (Vec(n) ->: Vec(n) )))
val v0 = "v_0" :: Vec(zero)
val w0 = "w_0" :: Vec(zero)
val vaddn = "vadd(n)" :: (Vec(n) ->: (Vec(n) ->: Vec(n) ))
val vsn = "v_(succ(n))" :: Vec(succ(n))
val wsn = "w_(succ(n))" :: Vec(succ(n))
//val vadd = indNVnVnVn(v0 :-> (w0 :-> ???))(n :~> (vaddn :-> (vsn :-> (wsn :-> ??? ))))
//val vadd = indNVnVnVn(v0 :-> (w0 :-> v0 ))(n :~> (vaddn :-> (vsn :-> (wsn :-> vcons (n) (add (vhead(succ(n))(vsn)) (vhead(succ(n))(wsn))) (vaddn (vtail(succ(n))(vsn)) (vtail(succ(n))(wsn))) ))))
val vadd = indNVnVnVn(v0 :-> (w0 :-> vnil ))(n :~> (a :-> (vn :-> (a1 :-> ???))))
val vhead = recVN(errorEl)(n :~> (a :-> (vn :-> (a1 :-> a))))
vhead(two)(vect) == a

val indVnVpn = VecInd.induc(n :~> (vn :-> Vec(pred(n)) ))
val errorList = "error" :: Vec(pred(zero))
val vpn = "v_(pred(n))" :: Vec(pred(n))
//val vtail = indVnVpn(errorList)(n :~> (a :-> (vn :-> (vpn :-> ???))))
val vtail = indVnVpn(errorList)(n :~> (a :-> (vn :-> (vpn :-> vn))))
vtail(two)(vect) == vcons(zero)(a1)(vnil)

//val vadd = indNVnVnVn(v0 :-> (w0 :-> vnil ))(n :~> (vaddn :-> (vsn :-> (wsn :-> vcons (n) (add (vhead(succ(n))(vsn)) (vhead(succ(n))(wsn))) (vaddn (vtail(succ(n))(vsn)) (vtail(succ(n))(wsn))) ))))


val indNVnVnN = NatInd.induc(n :-> (Vec(n) ->: (Vec(n) ->: Nat)))
val vscalarProdn = "vscalarProd(n)" :: (Vec(n) ->: (Vec(n) ->: Nat))
val vect = vcons(one)(one)(vcons(zero)(two)(vnil))
val vect1 = vcons(one)(two)(vcons(zero)(three)(vnil))

val v0 = "v_0" :: Vec(zero)
val w0 = "w_0" :: Vec(zero)
val vsn = "v_(succ(n))" :: Vec(succ(n))
val wsn = "w_(succ(n))" :: Vec(succ(n))

//val vscalarProd = indNVnVnN(v0 :-> (w0 :-> ???))(n :~> (vscalarProdn :-> (vsn :-> (wsn :-> 
//  �???(
//    ???(???(???)(vsn))(???(???)(wsn))
//  )(
//    ???(???(???)(vsn))(???(???)(wsn))
//  )
//� ))))

val vscalarProd = indNVnVnN(v0 :-> (w0 :-> zero))(n :~> (vscalarProdn :-> (vsn :-> (wsn :-> 
    add
    (mult (vhead(succ(n))(vsn)) (vhead(succ(n))(wsn)))
    (vscalarProdn (vtail(succ(n))(vsn)) (vtail(succ(n))(wsn))) 
))))

vscalarProd(two)(vect)(vect1).fansi 
//> res: String = "succ(succ(succ(succ(succ(succ(succ(succ(0))))))))"

vscalarProd(two)(vect)(vect1) == eight
//> res: Boolean = true
//Enter below the output of command (after symbol = )
add(vscalarProd(n)(vn)(wn))(vscalarProd(m)(vm)(vm)) 
































import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros

val one = succ(zero)
  val two = succ(one)
  val three = succ(two)
  val four = succ(three)
  val five = succ(four)
  val six = succ(five)
  val seven = succ(six)
  val eight = succ(seven)

  val n = "n" :: Nat
  val m = "m" :: Nat

  val a = "a" :: Nat
  val a1 = "a1" :: Nat
  val a2 = "a2" :: Nat
  val a3 = "a3" :: Nat
  val a4 = "a4" :: Nat
  val a5 = "a5" :: Nat
  val a6 = "a6" :: Nat
  val a7 = "a7" :: Nat

  val Vec = "Vec" :: Nat ->: Type
  val VecInd = ("nil" ::: (Vec -> Vec(zero))) |: { "cons" ::: n ~>>: (Nat ->>: (Vec :> Vec(n)) -->>: (Vec -> Vec(succ(n))))} =:: Vec
  val vnil :: vcons :: HNil = VecInd.intros

  val vn = "v_n" :: Vec(n)
  val vm = "v_m" :: Vec(m)
  val wm = "w_m" :: Vec(m)

  val recNN = NatInd.rec(Nat)
  val errorN = "error" :: Nat
  val pred = recNN(errorN)(n :-> (m :-> n))

  val recVN = VecInd.rec(Nat)
  val errorEl = "error" :: Nat
  val vhead = recVN(errorEl)(n :~> (a :-> (vn :-> (a1 :-> a))))

  val indVnVpn = VecInd.induc(n :~> (vn :-> Vec(pred(n)) ))
  val errorVec = "error" :: Vec(pred(zero))
  val vpn = "v_(pred(n))" :: Vec(pred(n))
  val vtail = indVnVpn(errorVec)(n :~> (a :-> (vn :-> (vpn :-> vn))))

  val Matrix = "Matrix" :: Nat ->: Nat ->: Type
  val MatrixInd = {"nil" ::: m ~>>: (Matrix -> Matrix(zero)(m))} |: { "cons" ::: n ~>>: (m ~>>: (Vec(m) ->>: (Matrix :> Matrix(n)(m)) -->>: (Matrix -> Matrix(succ(n))(m))))} =:: Matrix
  val mnil :: mcons :: HNil = MatrixInd.intros

  val matnm = "mat_nm" :: Matrix(n)(m)

  val indMnmVm = MatrixInd.induc(n :~> (m :~> (matnm :-> Vec(m) )))
  val errorVecm = "error" :: Vec(m)
  val mhead = indMnmVm(m :~> errorVecm)(n :~> (m :~> (vm :-> (matnm :-> (wm :-> vm)))))

  val indMnmMpnm = MatrixInd.induc(n :~> (m :~> (matnm :-> Matrix(pred(n))(m) )))
  val errorMat = "error" :: Matrix(pred(zero))(m)
  val matpnm = "v_(pred(n))" :: Matrix(pred(n))(m)
  val mtail = indMnmMpnm(m :~> errorMat)(n :~> (m :~> (vm :-> (matnm :-> (matpnm :-> matnm)))))

  val indNMn0 = NatInd.induc(n :-> Matrix(n)(zero))
  val matn0 = "mat_n0" :: Matrix(n)(zero)
  val replicateNil = indNMn0(mnil(zero))(n :~> (matn0 :-> mcons(n)(zero)(vnil)(matn0) ))
  replicateNil(three).fansi 
  // "cons(succ(succ(0)))(0)(nil)(
  //  cons(succ(0))(0)(nil)(
  //  cons(0)(0)(nil)(
  //  nil(0))))"

  val indNnMnmMnsm = NatInd.induc(n :-> (Vec(n) ->: (m ~>: (Matrix(n)(m) ->: Matrix(n)(succ(m))) )))
  val v0 = "v_0" :: Vec(zero)
  val mat0m = "mat_0m" :: Matrix(zero)(m)
  val vsn = "v_(succ(n))" :: Vec(succ(n))
  val matsnm = "mat_(succ(n),m)" :: Matrix(succ(n))(m)
  val zipWithConsn = "zipWithCons(n)" :: Vec(n) ->: (m ~>: (Matrix(n)(m) ->: Matrix(n)(succ(m))) )
  val zipWithCons = indNnMnmMnsm (v0 :-> (m :~> (mat0m :->
      //this has to return 0 * n + 1
      //mcons (zero) (succ(m))
      //        (vcons (m) (vhead (zero) (v0)) (mhead (zero) (m) (mat0m)) )
      //        (mnil(succ(m)))

      mnil(succ(m))

      ))) (n :~> (zipWithConsn :-> (vsn :-> (m :~> (matsnm :->
         //???
         mcons (n) (succ(m))
           (vcons (m) (vhead (succ(n)) (vsn)) (mhead (succ(n))(m) (matsnm)) )
           (zipWithConsn (vtail (succ(n)) (vsn)) (m) (mtail (succ(n))(m) (matsnm)))


      )))))  !: n ~>: (Vec(n) ->: (m ~>: (Matrix(n)(m) ->: Matrix(n)(succ(m))) ))


  val vect = vcons(two)(a)(vcons(one)(a1)(vcons(zero)(a2)(vnil))) !: Vec(three)
  val vect1 = vcons(two)(a3)(vcons(one)(a4)(vcons(zero)(a5)(vnil))) !: Vec(three)
  val mat = mcons(one)(three)(vect)(mcons(zero)(three)(vect1)(mnil(three))) !: Matrix(two)(three)
  val vect2 = vcons(one)(a6)(vcons(zero)(a7)(vnil)) !: Vec(two)
  zipWithCons(two)(vect2)(three)(mat).fansi
  //cons(succ(0))(succ(succ(succ(succ(0)))))(cons(succ(succ(succ(0))))(a6)(cons(succ(succ(0)))(a)(cons(succ(0))(a1)(cons(0)(a2)(nil)))))(cons(0)(succ(succ(succ(succ(0)))))
  // (cons(succ(succ(succ(0))))(a7)(cons(succ(succ(0)))(a3)(cons(succ(0))(a4)(cons(0)(a5)(nil)))))(nil(succ(succ(succ(succ(0)))))))

  val indMnmMmn = MatrixInd.induc(n :~> (m :~> (matnm :-> Matrix(m)(n) )))
  val matmn = "mat_mn" :: Matrix(m)(n)
  val transpose = indMnmMmn(m :~> replicateNil(m))(n :~> (m :~> (vm :-> (matnm :-> (matmn :->
      //zipWithCons(???)(???)(???)(???)
      zipWithCons (m) (vm) (n) (matmn)
      //zipWithCons (m) (vm) (zero) (replicateNil(m))
      //zipWithCons(m) (vm) (n) (transpose(
    )))))  !: n ~>: (m ~>: (Matrix(n)(m) ->: Matrix(m)(n) ))

  transpose(two)(three)(mat).fansi
  //"cons(succ(succ(0)))(succ(succ(0)))(cons(succ(0))(a)(cons(0)(a3)(nil)))
  // (cons(succ(0))(succ(succ(0)))(cons(succ(0))(a1)(cons(0)(a4)(nil)))
  // (cons(0)(succ(succ(0)))(cons(succ(0))(a2)(cons(0)(a5)(nil)))
  // (nil(succ(succ(0))))))"

//Enter below the output of command (after symbol = )
transpose(m)(n)(transpose(n)(m)(matnm)).typ






















 import TLImplicits._
  import shapeless._
  val A = "A" :: Type
  val a = "a" :: A
  val a1 = "a1" :: A
  val a2 = "a2" :: A
  val Id = "Id" :: A ->: A ->: Type
  val IdInd = ("refl" ::: a ~>>: (Id -> Id(a)(a) )) =:: Id
  val refl :: HNil = IdInd.intros

  val a1_eq_a2 = "a1 = a2" :: Id(a1)(a2)
  val ind_a1eqa2_a2eqa1 = IdInd.induc(a1 :~> (a2 :~> (a1_eq_a2 :-> Id(a2)(a1) )))
  //val sym = ind_a1eqa2_a2eqa1(a :~> Id(a1)(a2) -> Id(a2)(a1) )  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: Id(a2)(a1) ))
  val sym = ind_a1eqa2_a2eqa1(a :~> refl(a) )  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: Id(a2)(a1) ))




import TLImplicits._
  import shapeless._
  val A = "A" :: Type
  val a = "a" :: A
  val a1 = "a1" :: A
  val a2 = "a2" :: A
  val a3 = "a3" :: A
  val Id = "Id" :: A ->: A ->: Type
  val IdInd = ("refl" ::: a ~>>: (Id -> Id(a)(a) )) =:: Id
  val refl :: HNil = IdInd.intros

  val a1_eq_a2 = "a1 = a2" :: Id(a1)(a2)
  val ind_a1eqa2_a2eqa3_a1eqa3 = IdInd.induc(a1 :~> (a2 :~> (a1_eq_a2 :-> (a3 ~>: (Id(a2)(a3) ->: Id(a1)(a3) )))))
  val a_eq_a3 = "a = a3" :: Id(a)(a3)
  //val trans = ind_a1eqa2_a2eqa3_a1eqa3(a :~> (a3 :~> (a_eq_a3 :-> ??? )))  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: (a3 ~>: (Id(a2)(a3) ->: Id(a1)(a3) ))))
  //val trans = ind_a1eqa2_a2eqa3_a1eqa3(a :~> (a3 :~> (a_eq_a3 :-> refl(a_eq_a3(a)) )))  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: (a3 ~>: (Id(a2)(a3) ->: Id(a1)(a3) ))))
  val trans = ind_a1eqa2_a2eqa3_a1eqa3(a :~> (a3 :~> (a_eq_a3 :-> a_eq_a3 )))  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: (a3 ~>: (Id(a2)(a3) ->: Id(a1)(a3) ))))



import TLImplicits._
  import shapeless._
  val A = "A" :: Type
  val a = "a" :: A
  val a1 = "a1" :: A
  val a2 = "a2" :: A
  val Id = "Id" :: A ->: A ->: Type
  val IdInd = ("refl" ::: a ~>>: (Id -> Id(a)(a) )) =:: Id
  val refl :: HNil = IdInd.intros

  val a1_eq_a2 = "a1 = a2" :: Id(a1)(a2)
  val f = "f" :: A ->: A
  val ind_a1eqa2_fa1eqfa2 = IdInd.induc(a1 :~> (a2 :~> (a1_eq_a2 :-> (f ~>: Id(f(a1))(f(a2)) ))))
  //val ext = ind_a1eqa2_fa1eqfa2(a :~> (f :~> ??? ))  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: (f ~>: Id(f(a1))(f(a2)) )))
  val ext = ind_a1eqa2_fa1eqfa2(a :~> (f :~> refl(f(a)) ))  !: a1 ~>: (a2 ~>: (Id(a1)(a2) ->: (f ~>: Id(f(a1))(f(a2)) )))



import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBB = BoolInd.rec(Bool)
val not = recBB(fls)(tru)

val indB_not_not_b_eq_b = BoolInd.induc(b :-> (not(not(b)) =:= b))
//val not_not_b_eq_b = indB_not_not_b_eq_b(???)(???)  !: b ~>: (not(not(b)) =:= b)
val not_not_b_eq_b = indB_not_not_b_eq_b(tru.refl)(fls.refl)  !: b ~>: (not(not(b)) =:= b)
//Enter below the output of command (after symbol = )
not_not_b_eq_b(b)





import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBBB = BoolInd.rec(Bool ->: Bool)
val and = recBBB(b :-> b)(b :-> fls)

//val tru_and_b_eq_b = b :~> ???  !: b ~>: (and(tru)(b) =:= b)
val tru_and_b_eq_b = b :~> and(tru)(b).refl  !: b ~>: (and(tru)(b) =:= b)

//val fls_and_b_eq_fls = b :~> ???  !: b ~>: (and(fls)(b) =:= fls)
val fls_and_b_eq_fls = b :~> and(fls)(b).refl  !: b ~>: (and(fls)(b) =:= fls)

val indB_b_and_tru_eq_b = BoolInd.induc(b :-> (and(b)(tru) =:= b))
//val b_and_tru_eq_b = indB_b_and_tru_eq_b(???)(???)  !: b ~>: (and(b)(tru) =:= b)
val b_and_tru_eq_b = indB_b_and_tru_eq_b(tru.refl)(fls.refl)  !: b ~>: (and(b)(tru) =:= b)

val indB_b_and_fls_eq_fls = BoolInd.induc(b :-> (and(b)(fls) =:= fls))
//val b_and_fls_eq_fls = indB_b_and_fls_eq_fls(???)(???)  !: b ~>: (and(b)(fls) =:= fls)
val b_and_fls_eq_fls = indB_b_and_fls_eq_fls(fls.refl)(fls.refl)  !: b ~>: (and(b)(fls) =:= fls)
//Enter below the output of command (after symbol = )
tru_and_b_eq_b(b).fansi ++ fls_and_b_eq_fls(b).fansi ++ b_and_tru_eq_b(b).fansi ++ b_and_fls_eq_fls(b).fansi





import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val b1 = "b1" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBB = BoolInd.rec(Bool)
val not = recBB(fls)(tru)
val recBBB = BoolInd.rec(Bool ->: Bool)
val or = recBBB(b :-> tru)(b :-> b)
val and = recBBB(b :-> b)(b :-> fls)

val indB_deMorgan = BoolInd.induc(b :-> (b1 ~>: ( not(and(b)(b1)) =:= or(not(b))(not(b1)) )))
//val deMorgan = indB_deMorgan(b1 :~> ???)(b1 :~> ???)  !: b ~>: (b1 ~>: (not(and(b)(b1)) =:= or(not(b))(not(b1)) ))
val deMorgan = indB_deMorgan(b1 :~> not(b1).refl)(b1 :~> tru.refl)  !: b ~>: (b1 ~>: (not(and(b)(b1)) =:= or(not(b))(not(b1)) ))
//Enter below the output of command (after symbol = )
deMorgan(b1)(b)










import TLImplicits._
import shapeless._
val Bool = "Boolean" :: Type
val b = "b" :: Bool
val b1 = "b1" :: Bool
val BoolInd = ("true" ::: Bool) |: ("false" ::: Bool) =: Bool
val tru :: fls :: HNil = BoolInd.intros
val recBBB = BoolInd.rec(Bool ->: Bool)
val and = recBBB(b :-> b)(b :-> fls)

val indB_b_and_tru_eq_b = BoolInd.induc(b :-> (and(b)(tru) =:= b))
//val b_and_tru_eq_b = indB_b_and_tru_eq_b(???)(???)  !: b ~>: (and(b)(tru) =:= b)
val b_and_tru_eq_b = indB_b_and_tru_eq_b(tru.refl)(fls.refl)  !: b ~>: (and(b)(tru) =:= b)

val indB_b_and_fls_eq_fls = BoolInd.induc(b :-> (and(b)(fls) =:= fls))
//val b_and_fls_eq_fls = indB_b_and_fls_eq_fls(???)(???)  !: b ~>: (and(b)(fls) =:= fls)
val b_and_fls_eq_fls = indB_b_and_fls_eq_fls(fls.refl)(fls.refl)  !: b ~>: (and(b)(fls) =:= fls)
val indB_b_and_b1_eq_b1_and_b = BoolInd.induc(b :-> (b1 ~>: ( and(b)(b1) =:= and(b1)(b) )))
val b_and_b1_eq_b1_and_b = indB_b_and_b1_eq_b1_and_b(b1 :~>
 //IdentityTyp.symm(Bool)(???)(???)(???)
 //IdentityTyp.symm(Bool)(b1)(b1)(b1.refl)
 IdentityTyp.symm(Bool) ( and(b1)(tru)) (and(tru)(b1)) (b_and_tru_eq_b(b1))
)(b1 :~>
 //IdentityTyp.symm(Bool)(???)(???)(???)
 //IdentityTyp.symm(Bool)(fls)(fls)(fls.refl)
 IdentityTyp.symm(Bool) (and(b1)(fls)) (and(fls)(b1)) (b_and_fls_eq_fls(b1))
)  !: b ~>: (b1 ~>: ( and(b)(b1) =:= and(b1)(b) ))
//Enter below the output of command (after symbol = )
b_and_b1_eq_b1_and_b(b)(b1)



//n.refl  !: (n =:= n)
//IdentityTyp.extnslty(f) !: n ~>: (m ~>: ((n =:= m) ->: (f(n) =:= f(m) )))

import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val recNNN = NatInd.rec(Nat ->: Nat)
val addn ="add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
//val add_zero_n_eq_n = n :~> ??? !: (n ~>: (add(zero)(n) =:= n))
val add_zero_n_eq_n = n :~> n.refl !: (n ~>: (add(zero)(n) =:= n))

val indN_add_n_zero_eq_n = NatInd.induc(n :-> (add(n)(zero) =:= n))
val hyp = "n + 0 = n" :: (add(n)(zero) =:= n)
val add_n_zero_eq_n = indN_add_n_zero_eq_n(zero.refl)(n :~> (hyp :->
  //IdentityTyp.extnslty(???)(???)(???)(???) ))  !: (n ~>: (add(n)(zero) =:= n))
  IdentityTyp.extnslty(succ) (add(n)(zero)) (n) (hyp) ))  !: (n ~>: (add(n)(zero) =:= n))
//Enter below the output of command (after symbol = )
(add_n_zero_eq_n(succ(succ(n))).fansi ++ add_zero_n_eq_n(succ(succ(zero))).fansi).take(500)

//Hypothesis is  n + 0 = n
//to proof is Sn + 0 = Sn 


















trait Monad[M[_]] {
  def bind[A, B](ma: M[A], k: A => M[B]): M[B]
  def pure[A](a: A): M[A]
}

val as: List[String] = List("a", "b", "c")
val k: String => List[String] = x => List(x + "0" , x + "1")

implicit object listMonad extends Monad[List] {
  override def bind[A, B](ma: List[A], k: A => List[B]): List[B] = ma.flatMap(k(_))
  override def pure[A](a: A): List[A] = List[A](a)
}

implicit class ListWithBind[A](as: List[A]) {
    def >>=[B](k: A => List[B]): List[B] = implicitly[Monad[List]].bind(as, k)
    //def WithPure(a: A): List[A] = implicitly[Monad[List]].pure(a)
}

//implicit class ListStringMonad(as: List[String]) {
//  def >>=(k: String => List[String]): List[String] = implicitly[Monad[List]].bind(as, k)
//}

implicit class WithPure[A](a: A) {
  def pure = implicitly[Monad[List]].pure(a)
}


implicitly[Monad[List]].bind(as, k)
//> List(a0, a1, b0, b1, c0, c1)

as >>= k
//> List(a0, a1, b0, b1, c0, c1)

implicitly[Monad[List]].pure("a")
//> List(a)

"a".pure
//> List(a)







trait Foldable[T[_]] {
  def foldr[A, B](ta: T[A])(f: A => B => B)(seed: B): B
}

sealed trait BTree[+A]
case object Empty extends BTree[Nothing]
case class Leaf[A](a: A) extends BTree[A]
case class Fork[A](left: BTree[A], a: A, right: BTree[A]) extends BTree[A]


implicit object bTreeFoldable extends Foldable[BTree] {
   def foldr[A, B](ta:BTree[A])(f: A => B => B)(seed: B):B = {
       ta match {
           case Empty => seed
           case Leaf(a) => f(a)(seed)
           case Fork(tl, a, tr) => {
                val lb = foldr(tl)(f)(seed)
                val lt = f(a)(lb)
                foldr(tr)(f)(lt)
           }
      
       }
   }

}

implicit class FoldableBTree[A](tree: BTree[A]) {
   def foldr[B](f: A => B => B)(seed: B):B = {
       implicitly[Foldable[BTree]].foldr(tree)(f)(seed)
   }
}

val tree = Fork(Leaf(1), 2, Leaf(3))
tree.foldr((x: Int) => (y: Int) => x + y)(0)
//> 6

implicitly[Foldable[BTree]].foldr(tree)((x: Int) => (y: Int) => x + y)(0)
//> 6






/**
 */
object Demo2 {
  sealed trait Nat {
    type This >: this.type <: Nat
    type ++ = Succ[This]
    type + [_ <: Nat] <: Nat
    type * [_ <: Nat] <: Nat
    type pf [_ <: Nat] <: Nat
    type ^ [X <: Nat] = (X# pf [This])
  }

  final object Zero extends Nat {
    type This = Zero
    type + [X <: Nat] = X
    type * [X <: Nat] = Zero
    type pf [_ <: Nat] = Succ[Zero]
    //type ^ [X <: Nat] = Succ[Zero]
  }


  type Zero = Zero.type

  final class Succ[N <: Nat] extends Nat {
    type This = Succ[N]
    type + [X <: Nat] = Succ[N# + [X]]
    type * [X <: Nat] = (N# * [X])# + [X]
    type pf [X <: Nat] = (N# pf [X])# * [X]
    //type ^ [X <: Nat] = (N# ^ [X])# * [X]
  }

  type _0 = Zero
  type _1 = _0 # ++
  type _2 = _1 # ++
  type _3 = _2 # ++
  type _4 = _3 # ++
  type _5 = _4 # ++
  type _6 = _5 # ++
  type _7 = _6 # ++
  type _8 = _7 # ++
  type _9 = _8 # ++y



  implicitly[_2# ^ [_2] =:= _4]
  implicitly[_2# ^ [_3] =:= _8]
  implicitly[_0# ^ [_1] =:= _0]
  implicitly[_1# ^ [_0] =:= _1]
  implicitly[_3# ^ [_0] =:= _1]
  implicitly[_3# ^ [_2] =:= _9]
  implicitly[_3# ^ [_3] =:= _9# * [_3]]

}

//sealed trait Nat {
//  type This >: this.type <: Nat
//  type ++ = Succ[This]
//  type + [_ <: Nat] <: Nat
//  type * [_ <: Nat] <: Nat
//  type ^ [X <: Nat] = ???
//}
//
//object Zero extends Nat {
//  type This = Zero
//  type + [X <: Nat] = X
//  type * [_ <: Nat] = Zero
//}
//type Zero = Zero.type
//class Succ[N <: Nat] extends Nat {
//  type This = Succ[N]
//  type + [X <: Nat] = Succ[N# + [X]]
//  type * [X <: Nat] = (N# * [X])# + [X]
//}



import TLImplicits._
import shapeless._
val Nat = "Nat" :: Type
val NatInd = ("0" ::: Nat) |: ("succ" ::: Nat -->>: Nat) =: Nat
val zero :: succ :: HNil = NatInd.intros
val n = "n" :: Nat
val m = "m" :: Nat
val one = succ(zero)
val two = succ(one)
val three = succ(two)
val four = succ(three)
val five = succ(four)
val six = succ(five)
val recNNN = NatInd.rec(Nat ->: Nat)
val addn = "add(n)" :: Nat ->: Nat
val add = recNNN(m :-> m)(n :-> (addn :-> (m :-> succ(addn(m)) )))
val multn = "mult(n)" :: Nat ->: Nat
//val mult = recNNN(m :-> ???)(n :-> (multn :-> (m :-> ??? )))
val mult = recNNN(m :-> zero)( n :-> (multn :-> (m :-> add(multn(m))(m) )))
val powm = "pow(_, m)" :: Nat ->: Nat
val pow_flip = recNNN(n :-> one)(m :-> (powm :-> (n :-> mult(powm(n))(n) )))

val pow = n :-> (m :-> pow_flip(m)(n) )
pow(two)(three).fansi
pow(two)(three) == add(six)(two) 


val recNN = NatInd.rec(Nat)
val fact = recNN(one)(n :-> (m :-> mult(succ(n))(m)))























