package com.p14n.squint

import scala.language.experimental.macros
import scala.reflect.macros.Context

object Squint {

  trait Squintable[Src,Target] { def doSquint(x: Src): Target }

  def squint[Src,Target](x: Src)(implicit s: Squintable[Src,Target]) = s.doSquint(x)

 object Squintable {

  implicit def materializeSquintable[Src,Target]: Squintable[Src,Target] = 
    macro squintMacro[Src,Target]
  
  def squintMacro[S: c.WeakTypeTag,T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    weakTypeOf[T].declarations.foreach( c => {
        println(c.name )
    })

    val tree = q"""
 new Squintable[Person, PersonSummary] {
    def doSquint(src: Person): PersonSummary = new PersonSummary(src.address.postCode)
  }
"""
    println(tree)
    tree
  }
 }
}
