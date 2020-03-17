package io.github.massimosiani.free.pass1

import cats.free.Cofree
import io.github.massimosiani.free.types.CoAdder.CoAdderF

object ComonadPart {

  type Limit = Int
  type Count = Int
  type ThisState = (Limit, Count)

  type CoAdder[A] = Cofree[CoAdderF, A]

  val mkCoAdder: Limit => Count => CoAdder[ThisState] =
    limit =>
      count => {
        def next(w: ThisState): CoAdderF[ThisState] = CoAdderF(coAdd(w), coClear(w), coTotal(w))
        Cofree.unfold[CoAdderF, ThisState]((limit, count))(next)
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
