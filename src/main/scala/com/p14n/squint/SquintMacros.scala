package com.p14n.squint

import scala.reflect.macros.blackbox.Context
import language.experimental.macros



object Squint {

  trait Squintable[Src,Target] { def doSquint(x: Src): Target }

  def squint[Src,Target](x: Src)(implicit s: Squintable[Src,Target]) = s.doSquint(x)

 object Squintable {

  implicit def materializeSquintable[Src,Target]: Squintable[Src,Target] = 
    macro squintMacro[Src,Target]
  
  def squintMacro[S: c.WeakTypeTag,T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._

    def gatherFields(tpe: c.universe.Type): Iterable[FieldAndFields] = {
      //is this a collection?  return size or count
      tpe.decls.collect{
        case m if m.isMethod => m.asMethod
      }.filter{_.isCaseAccessor}.map( c => {
        new FieldAndFields(c.name.toString,
          c.returnType.toString,gatherFields(c.returnType))
      })
    }

    val targetType = weakTypeOf[T]
    val targetFields = gatherFields(targetType)
    val sourceType = weakTypeOf[S]
    val srcFields =  gatherFields(sourceType)


println("***********")
    println(srcFields)

    val fieldText = targetFields.foldLeft(""){ (txt,faf) =>
      val matched = PropertyMatcher.findMatch(faf,srcFields)
      matched match {
        case Some(x) => txt + faf.name +" = src."+ x
        //case Some(x) => txt :+ q"src.${x}"
        case _ => txt
      }
    }
println(fieldText)
    val tree = q"""
 new Squintable[${sourceType}, ${targetType}] {
    def doSquint(src: ${sourceType}): ${targetType} = 
      new ${targetType}(
        $fieldText
      )
 }
"""
    println(tree)
    tree
  }
 }
}
