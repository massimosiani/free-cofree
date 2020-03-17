package io.github.massimosiani.free.pass1

import io.github.massimosiani.free.PropSpec
import org.scalacheck.Gen

class MainSpec extends PropSpec {

  property("getLimit should return the limit") {
    forAll(Gen.choose[Int](0, 2000) :| "limit")(limit => Main.getLimit(limit) shouldBe limit)
  }

  property("testLimit should give true") {
    forAll(Gen.choose[Int](0, 2000) :| "limit")(limit => Main.testLimit(limit) shouldBe true)
  }
}
