package io.github.massimosiani.free.first

import cats.{Functor, Id}
import cats.free.{Cofree, Free}
import cats.implicits._

abstract class Pairing[F[_]: Functor, G[_]: Functor] {
  def pair[A, B, C]: (A => B => C) => F[A] => G[B] => C
}

object Pairing {

  implicit def IdIdPairing: Pairing[Id, Id] = new Pairing[Id, Id] {
    override def pair[A, B, C] = f => a => b => f(a)(b)
  }

  implicit def ReaderProdPairing[IN]: Pairing[IN => *, (IN, *)] = new Pairing[IN => *, (IN, *)] {
    override def pair[A, B, C] = f => r => p => f.compose(r)(p._1)(p._2)
  }

  implicit def ProdReaderPairing[IN]: Pairing[(IN, *), IN => *] = new Pairing[(IN, *), IN => *] {
    override def pair[A, B, C] = f => p => r => f(p._2)(r(p._1))
  }

  implicit def CoFreeFreePairing[F[_], G[_]](implicit F: Functor[F], G: Functor[G], P: Pairing[F, G]): Pairing[Cofree[F, *], Free[G, *]] =
    new Pairing[Cofree[F, *], Free[G, *]] {
      override def pair[A, B, C]: (A => B => C) => Cofree[F, A] => Free[G, B] => C =
        f => cof => _.fold(a => f(cof.head)(a), gs => P.pair(pair(f))(cof.tailForced)(gs))
    }
}
