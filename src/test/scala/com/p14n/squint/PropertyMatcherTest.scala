package com.p14n.squint

import com.p14n.squint.Squint._ 

import org.scalatest.FunSuite


class PropertyMatcherTest extends FunSuite {

  val testCompany = List(new FieldAndFields("company","Company",List(
      new FieldAndFields("name","String",List()),
      new FieldAndFields("ceo","Person",List(
              new FieldAndFields("address","Address",List(
                new FieldAndFields("postcode","String",List()))))))))

  test("Should match exact name"){
    val src = List(new FieldAndFields("postcode","String",List()))
    val target = new FieldAndFields("postcode","String",List())
    val result = PropertyMatcher.findMatch(target,src)
    assert(result == Some("postcode"))
  }

  test("Should match child property"){
    val src = List(new FieldAndFields("company","Company",List(
      new FieldAndFields("name","String",List()))))
    val target = new FieldAndFields("companyName","String",List())
    val result = PropertyMatcher.findMatch(target,src)
    assert(result == Some("company.name"))
  }
  test("Should match child of child property"){
    val target = new FieldAndFields("companyCeoAddressPostcode","String",List())
    val result = PropertyMatcher.findMatch(target,testCompany)
    assert(result == Some("company.ceo.address.postcode"))
  }
  test("Should match child property case class"){
    val target = new FieldAndFields("companyCeo","Person",List())
    val result = PropertyMatcher.findMatch(target,testCompany)
    assert(result == Some("company.ceo"))
  }

}
