package io.github.massimosiani.free.pass1

import cats.Functor
import cats.free.Cofree
import io.github.massimosiani.free.types.CoAdder.CoAdderF

object ComonadPart {

  type Limit = Int
  type Count = Int
  type ThisState = (Limit, Count)

  type CoAdder[A] = Cofree[CoAdderF, A]

  private def coiter[F[_]: Functor, A]: (A => F[A]) => A => Cofree[F, A] = next => start => Cofree.unfold(start)(next)

  val mkCoAdder: Limit => Count => CoAdder[ThisState] =
    limit =>
      count => {
        def next(w: ThisState): CoAdderF[ThisState] = CoAdderF(coAdd(w), coClear(w), coTotal(w))
        coiter(implicitly[Functor[CoAdderF]])(next)((limit, count))
      }

  def coAdd(w: ThisState)(value: Int): (Boolean, ThisState) = {
    val newCount = w._2 + value
    val test = newCount <= w._1
    val nextCount = if (test) newCount else w._2
    (test, (w._1, nextCount))
  }
  def coClear(w: ThisState): ThisState = w.copy(_2 = 0)
  def coTotal(w: ThisState): (Count, ThisState) = (w._2, w)
}
