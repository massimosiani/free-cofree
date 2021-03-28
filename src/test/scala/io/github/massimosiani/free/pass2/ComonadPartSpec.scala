package io.github.massimosiani.free.pass2

import io.github.massimosiani.free.pass2.ComonadPart.Base._
import io.github.massimosiani.free.PropSpec
import io.github.massimosiani.free.pass2.ComonadPart.{Base, Count, Env, coAdd, coClear, coTotal}
import org.scalacheck.Gen

class ComonadPartSpec extends PropSpec {

  private val stateGen: Gen[Base[Count]] = for {
    limit <- Gen.choose(0, 2000)
    count <- Gen.choose(0, 30000)
  } yield Base(Env(limit, identity), count)

  private val belowLimitGen: Gen[(Int, Base[Count])] = for {
    limit <- Gen.choose(0, 2000)
    adder <- Gen.choose(0, 1000)
    if limit > adder + 1
    count <- Gen.choose(0, limit - adder - 1)
  } yield (adder, Base(Env(limit, identity), count))

  private val aboveLimitGen: Gen[(Int, Base[Count])] = for {
    limit <- Gen.choose(0, 2000)
    adder <- Gen.choose(0, 1000)
    count <- Gen.choose(limit - adder, limit)
  } yield (adder, Base(Env(limit, identity), count))

  property("coAdd returns true if can be added") {
    forAll(belowLimitGen) {
      state => {
        val expected = Base(state._2.fa, state._1 + state._2.index)
        coAdd(state._2)(state._1) shouldBe ((true, expected))
      }
    }
  }

  property("coAdd returns false if can't be added") {
    forAll(aboveLimitGen)(state => coAdd(state._2)(state._1) shouldBe ((false, state._2)))
  }

  property("coClear clears the second argument") {
    forAll(stateGen)(state => coClear(state) shouldBe state.seek(0))
  }

  property("coTotal returns the second argument with the state") {
    forAll(stateGen)(state => coTotal(state) shouldBe ((state.index, state)))
  }
}
