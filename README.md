Squint <img src="https://cdn.rawgit.com/p14n/squint/master/squint.svg" alt="Squint" style="padding-bottom: -20px"/>
======

Creating summary DTOs from your domain objects.


```scala
/* Given some domain classes */
  case class Person(name:String, address:Address, dob: DateTime, salutation: String)
  case class Address(lines: List[String], city:String, postCode:String)
  case class Company(name: String, address:Address, employees: List[Person],ceo :Person)

/* We want to reduce the information for our DTOs */
  case class PersonSummary(name:String, addressPostCode: String)
  case class CompanySummary(name: String, ceo: PersonSummary)
  
/* Start with our domain data */
  val c = new Company(
    ceo = new Person(name = "bob", address = new Address("BN2 XXX"),
    name="Acme",
    address=new Address(lines = List("21 George st"),postCode = "BN1 XXX"),
    employees=List())
    
/* Call squint on the object tree */
import com.p14n.squint.Squint._ 
val result: CompanySummary = squint[Company,CompanySummary](c)

/* Squint matches on name to convert into new object tree */  
result = CompanySummary(name = "Acme", ceo = PersonSummary( name = "bob", addressPostCode = "BN2 XXX"))
```
