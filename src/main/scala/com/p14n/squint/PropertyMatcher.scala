package com.p14n.squint

object PropertyMatcher {

  def findMatch(target: FieldAndFields,src: Iterable[FieldAndFields]): Option[String]  = {
    findMatch(target.name.toLowerCase(),target.typeName,src)
  }

  def findMatch(targetName: String,
    targetType:String,src: Iterable[FieldAndFields]): Option[String]  = {

    src.foreach( f => {
      println(f.name + ","+ targetName)
      println(f.typeName +","+ targetType)
      if(targetName == f.lower && targetType == f.typeName)
        return Some(f.name)
    })

    src.foreach( f => {
      if(targetName.startsWith(f.lower)){
        val nextPart = targetName.substring(f.lower.length)
        findMatch(nextPart,targetType,f.fields) match {
          case Some(x) => return Some(f.name+"."+x)
          case None =>
        }
      }
    })
    None
  }
}
