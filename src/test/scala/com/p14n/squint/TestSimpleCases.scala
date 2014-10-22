package com.p14n.squint

import com.p14n.squint.Squint._ 

import org.scalatest.FunSuite

class TestSimpleCases extends FunSuite {

  def myMeth(p: Person) : PersonSummary = {
    squint(p)
  }

  test("Should convert address"){
    val p = new Person(new Address("BN2 5JS"))
    val result = myMeth(p)
    assert(result.addressPostCode == p.address.postCode)
  }

}
