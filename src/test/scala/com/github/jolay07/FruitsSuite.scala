package com.github.jolay07
import org.scalatest.funsuite.AnyFunSuite
class FruitsSuite extends AnyFunSuite{

test("Testing fail reading"){
  val line = Utilities.getLinesFromFile("src/resources/testFruits.txt")
  assert(line sameElements Array("Apples", "Kiwis", "Melons", "Watermelons", "Avocados"))
}

}
