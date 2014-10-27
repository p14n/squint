package com.p14n.squint

import com.p14n.squint.Squint._ 
import com.p14n.squint.TestClasses._ 

import org.scalatest.FunSuite

class TestSimpleCases extends FunSuite {

  test("Should convert address"){
    val p = new Person("bob",new Address("BN2 5JS"))
    val result: PersonSummary = squint[Person,PersonSummary](p)
    assert(result.addressPostCode == p.address.postCode)
  }
  test("Should convert inner case class"){

    val p = new Person("bob",new Address("BN2 5JS"))
    val c = new Company(ceo = p,name="Acme",address=p.address,employees=List(p))
    val result: CompanySummary = squint[Company,CompanySummary](c)
      
    assert(result.ceo.addressPostCode == p.address.postCode)
  }

}
