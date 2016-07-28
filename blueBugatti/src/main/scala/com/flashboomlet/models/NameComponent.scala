package com.flashboomlet.models

/** Model for a complete name component (i.e. first, last, middle) and it's associated percentages*/
case class NameComponent(
  name: String = "",
  count: Int = 0,
  percentages: Map[String, Double] = Map())
