package io.github.massimosiani.free.pass1

import io.github.massimosiani.free.types.Adder
import io.github.massimosiani.free.types.Adder.AdderF
import io.github.massimosiani.free.types.CoAdder.CoAdderF

object PairingProgram {

  implicit def CoAdderFAdderFPairing[IN](implicit p: Pairing[(IN, *), IN => *]): Pairing[CoAdderF, AdderF] = new Pairing[CoAdderF, AdderF] {
    override def pair[A, B, C]: (A => B => C) => CoAdderF[A] => AdderF[B] => C =
      f =>
        coAdder => {
          case Adder.Add(value, k) => p.pair(f)(coAdder.addH(value).asInstanceOf[(IN, A)])(k.asInstanceOf[IN => B])
          case Adder.Clear(k)      => f(coAdder.clearH)(k)
          case Adder.Total(k)      => p.pair(f)(coAdder.totalH.asInstanceOf[(IN, A)])(k.asInstanceOf[IN => B])
        }
  }
}
