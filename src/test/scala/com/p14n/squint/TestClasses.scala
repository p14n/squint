package com.p14n.squint

object TestClasses {

  case class Person(name:String,address:Address)
  case class Address(postCode:String)
  case class Company(name: String, address:Address, employees: List[Person],ceo :Person,industry: Option[String] = None)

  case class PersonSummary(addressPostCode: String)
  case class CompanySummary(
    name: String, ceo: PersonSummary, employeesCount: Int,industry: Option[String] = None)
  /*case class CompanySummary(
    name: String, ceo: PersonSummary, employeesCount: Int, addressPostCode: Option[String])*/

}
