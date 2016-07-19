package com.flashboomlet.io

import reactivemongo.api.MongoDriver

/** Database Driver class.
  *
  * @param database name of the database to connect to
  */
class DatabaseDriver(val database: String) extends MongoConstants {

  val driver = MongoDriver()

  val connection = driver.connection(List(DatabaseIp))

}

/** Companion object with constructor method. */
object DatabaseDriver {

  def apply(db: String): DatabaseDriver = new DatabaseDriver(db)
}
