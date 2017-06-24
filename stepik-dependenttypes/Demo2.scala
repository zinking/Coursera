package com.marin.bidding

/**
 * Created by awang on 4/26/17.
 */
object Demo2 {
  sealed trait Nat {
    type This >: this.type <: Nat
    type ++ = Succ[This]
    type + [_ <: Nat] <: Nat
  }

  final object Zero extends Nat {
    type This = Zero
    type + [X <: Nat] = X
  }
  type Zero = Zero.type
  final class Succ[N <: Nat] extends Nat {
    type This = Succ[N]
    type + [X <: Nat] = Succ[N# + [X]]
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


//  sealed trait Vec[n <: Nat, +A]
//  object Nil extends Vec[_0, Nothing]
//  type Nil = Nil.type
//  class Cons[n <: Nat, A, a <: A, as <: Vec[n, A]] extends Vec[Succ[n], A]
//
//  sealed trait Concat[n <: Nat, m <: Nat, A, vn <: Vec[n, A], vm <: Vec[m, A]] {
//    type Res <: Vec[n# + [m], A]
//  }
//
//  implicit def concatNil[_0, m <: Nat, A, Nil, vm <: Vec[m, A]] = new Concat {
//    type Res = vm
//  }

//  implicit def concatCons[Succ(n) <: Nat, m <: Nat, A, vn <: Cons[n, A, a, as], vm <: Vec[m, A]] = new Concat {
//    type Res = Cons[]
//  }

//  trait A
//  class a extends A
//
//  type vec = Cons[_0, A, a, Nil]
//  type vec1 = Cons[_1, A, a, Cons[_0, A, a, Nil]]
  //implicitly[Concat[_1, _1, A, vec, vec] { type Res = vec1 } ]


  //val indVVV = VecInd.induc(n :~> (vn :-> (m ~>: (Vec(m) ->: Vec(add(n)(m)) ))))
  //val concatVn = "concat(v_n)" :: (m ~>: (Vec(m) ->: Vec(add(n)(m)) ))
  //val vconcat = indVVV(m :~> (vm :-> ???))(n :~> (a :-> (vn :-> (concatVn :-> (m :~> (vm :-> ??? ))))))
  //val vconcat = indVVV(m :~> (vm :-> vm))(n :~> (a :-> (vn :-> (concatVn :-> (m :~> (vm :->  vcons(add(n)(m))(a)(concatVn(m)(vm)) ))))))



}
