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
          c.returnType,gatherFields(c.returnType))
     })
   }
   def createCaseClass(targetType:c.universe.Type,sourceType:c.universe.Type): c.Tree = {
     val targetFields = gatherFields(targetType)
     val srcFields =  gatherFields(sourceType)
     val fieldText = targetFields.map { (faf) =>

     
      val matched = PropertyMatcher.findMatch(faf,srcFields)
      val tname = TermName(faf.name)

      matched match {
        case Some(v) => {
          v match {
            case Left(x) => {
              val fieldArr = x.split("\\.")
              val startPoint = Select(Ident(TermName("src")),TermName(fieldArr.head))
              val selector = fieldArr.tail.foldLeft(startPoint) {
                (select,z) => Select(select,TermName(z))
              }

              Some(q"$tname = $selector")
            }
            case Right(x) => {
              val ccInner = createCaseClass(targetType, x.asInstanceOf[c.universe.Type])
              Some(q"$tname = $ccInner")
            }
          }
        }
        case _ => None
      }
     }.flatMap( x => x)

     q"new ${targetType}(..$fieldText)"
    }


    val targetType = weakTypeOf[T]
    val sourceType = weakTypeOf[S]

    val caseCreation = createCaseClass(targetType,sourceType)

    val tree = q"""
 new Squintable[${sourceType}, ${targetType}] {
    def doSquint(src: ${sourceType}): ${targetType} = 
      $caseCreation
 }
"""
    println(tree)
    tree
  }
 }
}
