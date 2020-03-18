package io.github.massimosiani.free.pass2

import cats.{Comonad, Eval, Functor, Representable}
import cats.data.RepresentableStore
import io.github.massimosiani.free.types.CoAdder.CoAdderF
import cats.syntax.coflatMap._
import cats.syntax.comonad._
import cats.syntax.functor._

object ComonadPart {

  type Limit = Int
  type Count = Int
  private type Env[A] = (Limit, Count => A)
  implicit def r: Representable.Aux[Env, Count] = new Representable[Env] {
    override def F: Functor[Env] = new Functor[Env] {
      override def map[A, B](fa: Env[A])(f: A => B): Env[B] = fa.copy(_2 = fa._2.andThen(f))
    }
    override type Representation = Count
    override def index[A](f: Env[A]): Count => A = f._2
    override def tabulate[A](f: Count => A): Env[A] = (0, f)
  }
  private type Base[A] = RepresentableStore[Env, Count, A]

  case class CofreeF[F[_], A, B](head: A, tail: Eval[F[B]])
  case class CofreeT[F[_], W[_], A](runCofreeT: W[CofreeF[F, A, CofreeT[F, W, A]]])

  private type CoAdderT[W[_], A] = CofreeT[CoAdderF, W, A]
  private type CoAdder[A] = CoAdderT[Base, A]

  private def coiterT[F[_]: Functor, W[_]: Comonad, A](next: W[A] => F[W[A]])(start: W[A]): CofreeT[F, W, A] = {
    val tail: W[A] => F[CofreeT[F, W, A]] = (w: W[A]) => next(w).map(coiterT(next))
    CofreeT(start.coflatMap(w => CofreeF(w.extract, Eval.later(tail(w)))))
  }

  val mkCoAdder: Limit => Count => CoAdder[Unit] =
    limit =>
      count => {
        implicit def baseIsEnv[A](b: Base[A]): Env[A] = b.fa
        implicit def baseIsStore[A](b: Base[A]): StoreOps[Base, Count, A] = new StoreOps[Base, Count, A] {
          override def pos: Count = b.index
          override def seek(s: Count): Base[A] = b.copy(index = s)
        }
        def next(w: Base[Unit]): CoAdderF[Base[Unit]] = CoAdderF(coAdd(w), coClear(w), coTotal(w))
        coiterT[CoAdderF, Base, Unit](next)(RepresentableStore[Env, Count, Unit]((limit, _ => ()), count))
      }

  private trait StoreOps[W[_], S, A] {
    def pos: S
    def seek(s: S): W[A]
  }

  def coAdd[W[_], A](w: W[A])(value: Int)(implicit wIsEnv: W[A] => Env[A], wIsStore: W[A] => StoreOps[W, Count, A]): (Boolean, W[A]) = {
    val count = w.pos
    val limit = w._1
    val newCount = count + value
    val test = newCount <= limit
    val nextCount = if (test) newCount else count
    (test, w.seek(nextCount))
  }
  def coClear[W[_], A](w: W[A])(implicit wIsStore: W[A] => StoreOps[W, Count, A]): W[A] = w.seek(0)
  def coTotal[W[_], A](w: W[A])(implicit wIsStore: W[A] => StoreOps[W, Count, A]): (Count, W[A]) = (w.pos, w)
}
