package com.p14n.squint

object TestClasses {

  case class Person(name:String,address:Address)
  case class Address(postCode:String)
  case class Company(name: String, address:Address, employees: List[Person],ceo :Person)

  case class PersonSummary(addressPostCode: String)
  case class CompanySummry(
    companyName: String)
}
