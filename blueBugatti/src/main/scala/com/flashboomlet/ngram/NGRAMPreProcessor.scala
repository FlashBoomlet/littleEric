package com.flashboomlet.ngram

import java.io.File

import com.flashboomlet.models.NameComponent
import com.github.tototoshi.csv.CSVFormat
import com.github.tototoshi.csv.CSVReader

import scala.util.Try

/**
  * Created by trill on 8/7/16.
  */
object NGRAMPreProcessor {

  val SocialSecurityFileName: String = "cleanSocialSecurityNames.tsv"

  val CensusFileName: String = "ethnicitiesAndNames.csv"

  def loadFile(
      fileName: String,
      listToName: List[String] => NameComponent)
      (implicit csvFormat: CSVFormat): List[NameComponent] = {

    val reader: CSVReader = CSVReader.open(new File(fileName))(csvFormat)
    reader.toStream.tail.foldLeft[List[NameComponent]](List()) {
      (acc: List[NameComponent], row: List[String]) =>
        listToName(row) +: acc
    }
  }

  def constructSSA(row: List[String]): NameComponent = NameComponent(
    row.head,
    row(1).toInt,
    Map("m" -> row(2).toDouble, "f" -> row(3).toDouble))

  def constructCensus(row: List[String]): NameComponent = NameComponent(
    row.head,
    row(2).toInt,
    Map(
      "white" -> Try(row(5).toDouble).getOrElse(0d),
      "black" -> Try(row(6).toDouble).getOrElse(0d),
      "api" -> Try(row(7).toDouble).getOrElse(0d),
      "aian" -> Try(row(8).toDouble).getOrElse(0d),
      "2prace" -> Try(row(9).toDouble).getOrElse(0d),
      "hispanic" -> Try(row(10).toDouble).getOrElse(0d)
    )
  )
}
