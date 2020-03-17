package io.github.massimosiani.free

import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

trait PropSpec extends AnyPropSpec with Matchers with ScalaCheckPropertyChecks
