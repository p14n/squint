package com.p14n.squint

import com.p14n.squint.Squint._ 

import org.scalatest.FunSuite

class TestSimpleCases extends FunSuite {

  test("Should convert address"){
    val p = new Person(new Address("BN2 5JS"))
    //val result: PersonSummary = squint[Person,PersonSummary](p)
    val result: PersonSummary = squint(p)
    assert(result.addressPostCode == p.address.postCode)
  }

}
