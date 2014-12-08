package com.newcircle.tests.patterns

import com.newcircle.patterns.PatternMatch2._
import com.newcircle.UnitSpec

class PatternMatch2Spec extends UnitSpec {
  "sumLeaves" should "handle a single leaf node with an Int" in {
    sumLeaves(LeafNode(10)) should equal (10)
  }

  it should "handle a single leaf node with something other than an Int" in {
    sumLeaves(LeafNode("abc")) should equal (0)
  }

  it should "handle a tree with no Int leaves" in {
    sumLeaves(ParentNode(value = "", 
                         left  = LeafNode(30.0),
                         right = LeafNode("abc"))) should equal (0)
  }

  it should "handle a tree with one Int leaf node somewhere below" in {
    val tree = ParentNode(value = "",
                          left  = ParentNode(value = "",
                                             left  = LeafNode(10),
                                             right = LeafNode("abc")),
                          right = LeafNode("abc"))
    sumLeaves(tree) should equal(10)
  }

  it should "handle ignore the Int values at non-leaf nodes" in {
    val tree = ParentNode(value = 10,
                          left  = ParentNode(value = 20,
                                             left  = LeafNode(10),
                                             right = LeafNode("abc")),
                          right = LeafNode(1))
    sumLeaves(tree) should equal(11)
  }

  it should "sum leaves that contain integers and other things" in {

    val tree = ParentNode(
      value = 10,
      left = ParentNode(
        value = "abc",
        left = LeafNode(10),
        right = LeafNode("abc")
      ),

      right = ParentNode(
        value = 30.0,
        left = ParentNode(
          value = "Something irrelevant",
          left  = ParentNode(
            value = BigDecimal("10.0"),
            left  = LeafNode(100),
            right = LeafNode("Ignore me")
          ),
          right = LeafNode(230)
        ),

        right = ParentNode(
          value = 100,
          left  = LeafNode(5),
          right = ParentNode(
            value = "asdf",
            left  = LeafNode(20),
            right = LeafNode(40.0)
          )
        )
      )
    )

    sumLeaves(tree) should equal(365)
  }
}
