package com.flashboomlet

import com.flashboomlet.io.DatabaseDriver
import com.flashboomlet.io.MongoConstants


/** Entry point into the program */
object Driver extends MongoConstants {

  /** Database driver to be used throughout the application. */
  val databaseDriver = DatabaseDriver(BlueBugattiDatabaseName)

  /** Entry point into the program (main method) */
  def main(args: Array[String]): Unit = {
  }

}
