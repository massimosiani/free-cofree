package io.github.massimosiani.free.pass1

import io.github.massimosiani.free.PropSpec
import io.github.massimosiani.free.pass1.ComonadPart.ThisState
import org.scalacheck.Gen

class ComonadPartSpec extends PropSpec {

  private val stateGen: Gen[ThisState] = for {
    limit <- Gen.choose(0, 2000)
    count <- Gen.choose(0, 30000)
  } yield (limit, count)

  private val belowLimitGen: Gen[(Int, ThisState)] = for {
    limit <- Gen.choose(0, 2000)
    adder <- Gen.choose(0, 1000)
    if limit > adder + 1
    count <- Gen.choose(0, limit - adder - 1)
  } yield (adder, (limit, count))

  private val aboveLimitGen: Gen[(Int, ThisState)] = for {
    limit <- Gen.choose(0, 2000)
    adder <- Gen.choose(0, 1000)
    count <- Gen.choose(limit - adder, limit)
  } yield (adder, (limit, count))

  property("coAdd returns true if can be added") {
    forAll(belowLimitGen)(state => ComonadPart.coAdd(state._2)(state._1) shouldBe ((true, (state._2._1, state._2._2 + state._1))))
  }

  property("coAdd returns false if can't be added") {
    forAll(aboveLimitGen)(state => ComonadPart.coAdd(state._2)(state._1) shouldBe ((false, state._2)))
  }

  property("coClear clears the second argument") {
    forAll(stateGen)(state => ComonadPart.coClear(state) shouldBe ((state._1, 0)))
  }

  property("coTotal returns the second argument with the state") {
    forAll(stateGen)(state => ComonadPart.coTotal(state) shouldBe ((state._2, state)))
  }
}
