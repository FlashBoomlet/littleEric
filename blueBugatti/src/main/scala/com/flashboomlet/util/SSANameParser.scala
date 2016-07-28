package com.flashboomlet.util

import java.io.File

import com.flashboomlet.models.NameComponent
import com.flashboomlet.util.model.Name
import com.github.tototoshi.csv.CSVReader
import com.github.tototoshi.csv.CSVWriter
import com.github.tototoshi.csv.DefaultCSVFormat
import com.github.tototoshi.csv.TSVFormat
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

/** This class offers and extremely inefficient method to parse the name data given by
  * the Social Security Administration.  */
object SSANameParser extends LazyLogging {

  /** Earliest year containing name data */
  val StartYear = 1880

  /** Current year, which contains no name data. */
  val THECURRENTFUCKINGYEAR = 2016

  /** File to output aggregated name data. */
  val OutFileName: String = "gendersAndNames.tsv"

  val ProcessedOutFileName: String = "cleanSocialSecurityNames.tsv"

  /** Implicit to use java bigdecimal functions. */
  implicit def doubleToJavaBigDecimal(b: Double): java.math.BigDecimal = BigDecimal(b).underlying()

  /** Top level function exposed to parse the data.
    *
    * Reads in each file, aggregates the data onto existing data, and then outputs the data
    *
    * @note the data must be in bigDONjr root directory in a directory called `names`
    */
  def parseRawNamesAndOutput(): Unit = {
    val names = aggregateFiles(Vector(), StartYear)
    val writer: CSVWriter = CSVWriter.open(new File(OutFileName))(LocalTSVFormat)
    writer.writeAll(names.map(n => Seq(n.firstName, n.gender, n.popularity)))
  }

  /** Loads clean names */
  def loadCleanNames(): Vector[Name] = {
    SSANameParser.getClass.getClassLoader.getResourceAsStream(OutFileName)
    CSVReader.open(OutFileName)(LocalTSVFormat).all().map{ l =>
      Name(l.head, l(1), l(2).toInt)
    }.toVector
  }

  def outputNameComponents(nameComps: Vector[NameComponent]): Unit = {
    val writer: CSVWriter = CSVWriter.open(new File(ProcessedOutFileName))(LocalTSVFormat)
    writer.writeRow(Seq("name", "count", "percent_male", "percent_female"))
    writer.writeAll(nameComps.map(n => Seq(n.name,
      n.count,
      n.percentages.get("m").get.stripTrailingZeros().toPlainString,
      n.percentages.get("f").get.stripTrailingZeros().toPlainString)))
  }

  /** Converts a list of Names to NameComponents
    *
    * @param names list of pre-processed names
    * @return list of processed name components
    */
  def namesToNameComponents(names: Vector[Name]): Vector[NameComponent] = {
    names.map(_.firstName).distinct.map { name =>
      names.filter(n => n.firstName == name)
        .foldLeft[NameComponent](NameComponent()) { (acc: NameComponent, item: Name) =>
          if (acc.name == "") {
            val genderMap: Map[String, Double] = Map(item.gender.toLowerCase() -> 1.0d,
              { if (item.gender.toLowerCase() == "f") { "m" } else { "f" } } -> 0.0d)
            NameComponent(
              item.firstName.toLowerCase(),
              item.popularity,
              genderMap)
          } else {
            val newCount = acc.count + item.popularity
            val currentMaleCount = acc.percentages.get("m") match {
              case Some(d) => d * acc.count
              case None => 0.0d
            }
            val currentFemaleCount = acc.percentages.get("f") match {
              case Some(d) => d * acc.count
              case None => 0.0d
            }
            val newMalePercentage = if (item.gender.toLowerCase == "m") {
              (currentMaleCount + item.popularity) / newCount
            } else {
              currentMaleCount / newCount
            }
            val newFemalePercentage = if (item.gender.toLowerCase == "f") {
              (currentFemaleCount + item.popularity) / newCount
            } else {
              currentFemaleCount / newCount
            }
            NameComponent(
              acc.name,
              newCount,
              Map("m" -> newMalePercentage, "f" -> newFemalePercentage))
          }
        }
    }
  }

  /** TSV Formatter */
  private implicit object LocalTSVFormat extends TSVFormat

  /** CSV Formatter */
  private implicit object LocalCSVFormat extends DefaultCSVFormat

  /** Determines if a name exists for a gender already */
  private def isNameDuplicate(name: Name): List[String] => Boolean = (other: List[String]) =>
    name.firstName == other.head && name.gender == other(1)

  /** Tail recursive function to aggregate each file's data onto previous existing data until files
    * run out.
    *
    * @note this function is inefficient as fuuuckkk
    * @param acc Existing accumulated data
    * @param year Current year to get data for
    * @return Aggregated list of data
    */
  @tailrec
  private def aggregateFiles(acc: Vector[Name], year: Int): Vector[Name] = {
    if (year < THECURRENTFUCKINGYEAR) {
      logger.info(s"Processing year: $year")
      val reader: CSVReader = CSVReader.open(new File(s"names/yob$year.txt"))(LocalCSVFormat)
      val updated = reader.toStream
        .foldLeft[Vector[Name]](acc) { (names: Vector[Name], lsName: List[String]) => // POS list
          val maybeDuplicate = names.find(n => isNameDuplicate(n)(lsName))
          maybeDuplicate match {
            case Some(d) =>
              Name(d.firstName, d.gender, d.popularity + lsName(2).toInt) +: names.filterNot(
                n => isNameDuplicate(n)(lsName))
            case None =>
              Name(lsName.head, lsName(1), lsName(2).toInt) +: names
          }
        }
      aggregateFiles(updated, year + 1)
    } else {
      acc
    }
  }
}
