package io.github.massimosiani.free.types

import cats.{derived, Functor}
import cats.implicits._

object CoAdder {

  case class CoAdderF[K](
      addH: Int => (Boolean, K),
      clearH: K,
      totalH: (Int, K)
  )

  implicit val functorCoAdder: Functor[CoAdderF] = {
    import derived.auto.functor._
    derived.semi.functor[CoAdderF]
  }
}
