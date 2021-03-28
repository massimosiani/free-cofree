package io.github.massimosiani.free.pass2

import cats.free.{Free, FreeT}
import cats.{Comonad, Monad}
import io.github.massimosiani.free.pass1.Pairing
import io.github.massimosiani.free.pass2.ComonadPart.CofreeT

abstract class PairEffect[W[_]: Comonad, M[_]: Monad] {

  def extract[F[_], A](cofreeT: CofreeT[F, W, A]): A

  def unwrap[F[_], A](cofreeT: CofreeT[F, W, A]): F[W[A]]

  def pairEffect[F[_], A, B, C](f: A => B => C)(cofree: CofreeT[F, W, A])(free: Free[M, B])(implicit p: Pairing[W, M]): M[C] = {
    val pe: CofreeT[F, W, A] => Free[M, B] => M[C] = pairEffect(f)
    free.fold[M[C]](x => Monad[M].pure(f(extract(cofree))(x)), mfb => p.pair(pe)(unwrap(cofree))(mfb))
  }
}
