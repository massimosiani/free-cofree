package io.github.massimosiani.free.pass2

import cats.{Comonad, Functor, Representable}
import cats.data.RepresentableStore
import cats.free.Cofree
import io.github.massimosiani.free.types.CoAdder.CoAdderF

object ComonadPart {

  type Limit = Int
  type Count = Int
  type Inside[A] = (Limit, Count => A)
  implicit def r: Representable.Aux[Inside, Count] = new Representable[Inside] {
    override def F: Functor[Inside] = new Functor[Inside] {
      override def map[A, B](fa: (Limit, Count => A))(f: A => B): (Limit, Count => B) = fa.copy(_2 = fa._2.andThen(f))
    }
    override type Representation = Count
    override def index[A](f: (Limit, Count => A)): Count => A = f._2
    override def tabulate[A](f: Count => A): (Limit, Count => A) = (0, f)
  }
  type Base[A] = RepresentableStore[Inside, Count, A]
  type CofreeT[F[_], W[_], A] = Cofree[F, W[A]]

  type CoAdderT[W[_], A] = CofreeT[CoAdderF, W, A]
  type CoAdder[A] = CoAdderT[Base, A]

  private def coiterT[F[_]: Functor, W[_]: Comonad, A](next: W[A] => F[W[A]])(start: W[A]): CofreeT[F, W, A] =
    Cofree.unfold(start)(next)

  val mkCoAdder: Limit => Count => CoAdder[Unit] =
    limit =>
      count => {
        def next(w: Base[Unit]): CoAdderF[Base[Unit]] = CoAdderF(coAdd(w), coClear(w), coTotal(w))
        coiterT[CoAdderF, Base, Unit](next)(RepresentableStore[Inside, Count, Unit]((limit, _ => ()), count))
      }

  def coAdd[A](w: Base[A])(value: Int): (Boolean, Base[A]) = {
    val count = w.index
    val limit = w.fa._1
    val newCount = count + value
    val test = newCount <= limit
    val nextCount = if (test) newCount else count
    (test, w.copy(index = nextCount))
  }
  def coClear[A](w: Base[A]): Base[A] = w.copy(index = 0)
  def coTotal[A](w: Base[A]): (Count, Base[A]) = (w.index, w)
}
