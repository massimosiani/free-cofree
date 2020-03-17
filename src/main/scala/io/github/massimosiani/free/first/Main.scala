package io.github.massimosiani.free.first

import io.github.massimosiani.free.first.ComonadPart.CoAdder
import io.github.massimosiani.free.first.PairingProgram.CoAdderFAdderFPairing
import io.github.massimosiani.free.first.Pairing.CoFreeFreePairing
import io.github.massimosiani.free.types.Adder.{functorAdder, AdderF}
import io.github.massimosiani.free.types.CoAdder.{functorCoAdder, CoAdderF}

object Main extends App {

  def runLimit[A](coAdder: CoAdder[A]): Int =
    CoFreeFreePairing[CoAdderF, AdderF].pair((_: A) => (b: Int) => b)(coAdder)(MonadPart.findLimit)

  def getLimit: Int => Int = x => runLimit(ComonadPart.mkCoAdder(x)(0))

  def testLimit: Int => Boolean = x => runLimit(ComonadPart.mkCoAdder(x)(0)) == x

  val x = 1000
  println(getLimit(x))
  println(testLimit(x))
}
