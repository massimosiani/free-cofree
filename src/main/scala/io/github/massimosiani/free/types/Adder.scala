package io.github.massimosiani.free.types

import cats.free.{Free, FreeT}
import cats.{derived, Functor}
import cats.implicits._

object Adder {

  sealed trait AdderF[K]
  case class Add[K](value: Int, f: Boolean => K) extends AdderF[K]
  case class Clear[K](k: K) extends AdderF[K]
  case class Total[K](f: Int => K) extends AdderF[K]

  type AdderT[M[_], A] = FreeT[AdderF, M, A]
  type Adder[A] = Free[AdderF, A]

  implicit val functorAdder: Functor[AdderF] = {
    import derived.auto.functor._
    derived.semi.functor[AdderF]
  }
}
