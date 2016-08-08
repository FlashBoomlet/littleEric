package com.flashboomlet.io

/**
  * Created by trill on 7/18/16.
  */
trait MongoConstants {

  val DatabaseIp = "localhost"

  val BlueBugattiDatabaseName = "bluebugatti"

  val GenderNamesCollectionName = "gender_names"

  val EthnicityNamesCollectionName = "ethnicity_names"

  val GenderNgramsCollectionName = "gender_ngrams"

  val EthnicityNgramsCollectionName = "ethnicity_ngrams"

  object NameComponentConstants {
    val Name = "name"

    val Count = "count"

    val Percentages = "percentages"
  }

  /**
    * String constants used across the entirity of the MongoDB database
    */
  object GlobalConstants {

    final val SetString = "$set"

    final val ElemMatchString = "$elemMatch"
  }
}
