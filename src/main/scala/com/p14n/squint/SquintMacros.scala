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
     gatherFieldsWithDepth(tpe,10)
   }
   def gatherFieldsWithDepth(fromType: c.universe.Type,depth: Int): Iterable[FieldAndFields] = {
     val tpe = if(fromType <:< typeOf[Option[Any]]) 
       fromType.asInstanceOf[TypeRefApi].args.head else fromType
     val fafs = tpe.decls.collect{
       case m if m.isMethod => m.asMethod
     }.filter{ mm => { mm.isCaseAccessor } }.map( c => {
       new FieldAndFields(c.name.toString,
          c.returnType,if(depth > 0) gatherFieldsWithDepth(c.returnType,depth - 1) else List())
     })

     if(tpe <:< typeOf[Traversable[_]] || tpe <:< typeOf[Array[_]]){
       fafs ++ List(new FieldAndFields("size",typeOf[Int],List()))
     } else fafs
   }

   def createForComprehension(targetName: TermName,fieldArray:Array[String],srcFields: Iterable[FieldAndFields]): c.universe.Tree = {

    var variableCount = 0
    var vlast = TermName("src")
    var vname = TermName("src")
    var currentFields = new FieldAndFields("",None,srcFields)

    val selector = fieldArray.map {
      (z) => {
        currentFields.find(z) match {
          case Some(flds) => {

            currentFields = flds
            val zname = TermName(z)
            vlast = vname
            variableCount = variableCount + 1
            vname = TermName("v"+variableCount)
            if(flds.fieldType.asInstanceOf[c.universe.Type] <:< typeOf[Option[Any]]){
              Some(fq"$vname <- $vlast.$zname ")
            } else {
              Some(fq"$vname <- if($vlast.$zname == null) None else Some($vlast.$zname) ")
            }
          }
          case _ => None
        }
      }
    }.flatMap( x => x).toList
    q"$targetName = for ( ..$selector ) yield $vname"
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
              val fieldTargetType = faf.fieldType.asInstanceOf[c.universe.Type]
              val fieldArr = x.split("\\.")

              if(fieldTargetType <:< typeOf[Option[Any]]){

                Some(createForComprehension(tname,fieldArr,srcFields))

              } else {
                val startPoint = Select(Ident(TermName("src")),TermName(fieldArr.head))
                val selector = fieldArr.tail.foldLeft(startPoint) {
                  (select,z) => Select(select,TermName(z))
                }

                Some(q"$tname = $selector")
              }
            }
            case Right(x) => {
              val ccInner = createCaseClass(
                faf.fieldType.asInstanceOf[c.universe.Type], 
                x.asInstanceOf[c.universe.Type])
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
