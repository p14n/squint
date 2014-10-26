package com.p14n.squint

case class FieldAndFields(val name: String, val typeName: String,val fields: Iterable[FieldAndFields]) {
  val lower = name.toLowerCase()
}
