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
    val c = new Company(ceo = p,name="Acme",address=p.address,employees=List(p),industry = Some("finance"))
    val result: CompanySummary = squint[Company,CompanySummary](c)
      
    assert(result.ceo.addressPostCode == p.address.postCode)
    assert(result.industry.get == "finance")
  }

  test("Should find end of tree with for comprehension"){
    val n = act(opt(act(opt(named("Bob")))))
    val p: OptionalNodeSummary1 = squint[OptionalActualNode,OptionalNodeSummary1](n)
    assert(p.actOptActOpt.get.name == "Bob")
  }
  test("Should find None not null with for comprehension"){
    val n = act(opt(act(null)))
    val p: OptionalNodeSummary1 = squint[OptionalActualNode,OptionalNodeSummary1](n)
    assert(!p.actOptActOpt.isDefined)
  }
  test("Should find None with for comprehension"){
    val n = act(opt(act(named("Bob"))))
    val p: OptionalNodeSummary1 = squint[OptionalActualNode,OptionalNodeSummary1](n)
    assert(! p.actOptActOpt.isDefined)
  }
  test("Should find the end of tree"){
    val n = act(opt(act(opt(act(opt(act(named("Bob"))))))))
    val p = for { 
      v1 <- if(n.act == null) None else Some(n.act) ;
      v2 <- v1.opt ;
      v3 <- if(v2.act == null) None else Some(v2.act) ;
      v4 <- v3.opt ;
      v5 <- if(v4.act == null) None else Some(v4.act) ;
      v6 <- v5.opt ;
      v7 <- if(v6.act == null) None else Some(v6.act) 
    } yield v7
    assert(p.get.name == "Bob")
  }
  test("Should find None not null"){
    val n = act(opt(act(opt(act(opt(act(named("Bob"))))))))
    val p = for { 
      v1 <- if(n.act == null) None else Some(n.act) 
      v2 <- v1.opt
      v3 <- if(v2.act == null) None else Some(v2.act) 
      v4 <- v3.opt
      v5 <- if(v4.act == null) None else Some(v4.act) 
      v6 <- if(v5.act == null) None else Some(v5.act) 
      v7 <- if(v6.act == null) None else Some(v6.act) 
    } yield v7
    assert(p  == None)
  }
  test("Should find None in the tree"){
    val n = act(opt(act(opt(act(opt(act(named("Bob"))))))))
    val p = for { 
      v1 <- if(n.act == null) None else Some(n.act) 
      v2 <- v1.opt
      v3 <- if(v2.act == null) None else Some(v2.act) 
      v4 <- v3.opt
      v5 <- v4.opt
      v6 <- v5.opt
      v7 <- if(v6.act == null) None else Some(v6.act) 
    } yield v7
    assert(p  == None)
  }

  def named(name:String): OptionalActualNode = {
    new OptionalActualNode(act = null, None, name)
  }
  def act(n: OptionalActualNode): OptionalActualNode = {
    new OptionalActualNode(act = n, None)
  }
  def opt(n: OptionalActualNode): OptionalActualNode = {
    new OptionalActualNode(act = null, Some(n))
  }
}
