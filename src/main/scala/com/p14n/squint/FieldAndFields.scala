package com.p14n.squint

case class FieldAndFields(val name: String, val fieldType: Any,val fields: Iterable[FieldAndFields]) {
  val lower = name.toLowerCase()

  def find(name: String): Option[FieldAndFields] = {
    fields.filter( _.name == name ).headOption
  }

  def treeString(gap: String = ""): String = {
    var str = gap+name+" "+fieldType +"\n"
    fields.foldLeft(str) { (orig,f) =>
      orig + f.treeString(gap + " ")
    }
  }
}
