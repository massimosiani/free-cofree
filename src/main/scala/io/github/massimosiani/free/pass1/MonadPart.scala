package io.github.massimosiani.free.pass1

import cats.data.StateT
import io.github.massimosiani.free.types.Adder._
import cats.free.Free

object MonadPart {

  val add: Int => Adder[Boolean] = x => Free.liftF(Add(x, identity))
  val clear: Adder[Unit] = Free.liftF(Clear(()))
  val total: Adder[Int] = Free.liftF(Total(identity))

  val findLimit: Adder[Int] = for {
    t <- total
    _ <- clear
    r <- `findLimit'`.runS(0)
    _ <- clear
    _ <- add(t)
  } yield r

  def `findLimit'` : StateT[Adder, Int, Unit] =
    for {
      r <- StateT.liftF[Adder, Int, Boolean](add(1))
      _ <- if (r) for {
        _ <- StateT.modify[Adder, Int](_ + 1)
        _ <- `findLimit'`
      } yield ()
      else StateT.pure[Adder, Int, Unit](())
    } yield ()
}
