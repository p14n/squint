package com.p14n.squint

case class FieldAndFields(val name: String, val fieldType: Any,val fields: Iterable[FieldAndFields]) {
  val lower = name.toLowerCase()
}
