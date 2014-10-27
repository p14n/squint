package com.p14n.squint

object PropertyMatcher {

  def findMatch(target: FieldAndFields,src: Iterable[FieldAndFields]): Option[Either[String,Any]]  = {
    findMatch(target.name.toLowerCase(),target.fieldType,src)
  }

  def findMatch(targetName: String,
    targetType:Any,src: Iterable[FieldAndFields]): Option[Either[String,Any]]  = {

    src.foreach( f => {
      if(targetName == f.lower){
        if(targetType == f.fieldType){
           return Some(Left(f.name))
        } else {
          return Some(Right(f.fieldType))
        }
      }
       
    })

    src.foreach( f => {
      if(targetName.startsWith(f.lower)){
        val nextPart = targetName.substring(f.lower.length)
        findMatch(nextPart,targetType,f.fields) match {
          case Some(x) => {
            x match {
              case Left(y) => return Some(Left(f.name+"."+y))
              case _ => None
            }
            
          }
          case None =>
        }
      }
    })
    None
  }
}
