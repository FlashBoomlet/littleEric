package com.flashboomlet.io

import com.flashboomlet.models.NameComponent
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/** Database Driver class.
  *
  * @param database name of the database to connect to
  */
class DatabaseDriver(val database: String)
  extends MongoConstants
  with MongoImplicits {

  val driver = MongoDriver()

  val connection = driver.connection(List(DatabaseIp))

  val db = connection(database)

  val genderNamesCollection: BSONCollection = db(GenderNamesCollectionName)

  val genderNgramsCollection: BSONCollection = db(GenderNgramsCollectionName)

  val ethnicityNamesCollection: BSONCollection = db(EthnicityNamesCollectionName)

  val ethnicityNgramsCollection: BSONCollection = db(EthnicityNgramsCollectionName)

  def insertNameComponent(name: NameComponent, col: BSONCollection): Unit = {
    Await.result(col.insert(name), Duration.Inf)
  }

  def insertNgram(
      nameComp: NameComponent,
      col: BSONCollection,
      mergeStrategy: (NameComponent, NameComponent) => NameComponent): Unit =  {
    val existingNameComponents = getNameComponents(nameComp.name, col)
    if (existingNameComponents.isEmpty) {
      insertNameComponent(nameComp, col)
    } else {
      val oldNameComponent = existingNameComponents.head
      val newNameComponent = mergeStrategy(oldNameComponent, nameComp)
      val selector = BSONDocument(NameComponentConstants.Name -> nameComp.name)
      val modifier = BSONDocument(GlobalConstants.SetString -> BSONDocument(
        NameComponentConstants.Count -> newNameComponent.count,
        NameComponentConstants.Percentages ->MongoUtil.mapToBSONDocument(
          newNameComponent.percentages)
      ))
      Await.result(col.findAndUpdate(selector, modifier), Duration.Inf)
    }
  }

  def insertGenderNameComponent(nameComp: NameComponent) = insertNameComponent(
    nameComp, genderNamesCollection)

//  def insertGenderNgram(nameComp: NameComponent) = insertNgram(nameComp, genderNgramsCollection)

  def insertEthnicityNameComponent(nameComp: NameComponent) = insertNameComponent(
    nameComp, ethnicityNamesCollection)

//  def insertEthnicityNgram(nameComp: NameComponent) = insertNgram(
//    nameComp, ethnicityNgramsCollection)

  def getNameComponents(name: String, col: BSONCollection): List[NameComponent] = {
    val future = col.find(BSONDocument(NameComponentConstants.Name -> name))
      .cursor[NameComponent]()
      .collect[List]()

    Await.result(future, Duration.Inf)
  }


  def bulkInsertNgrams(nameComponents: List[NameComponent], col: BSONCollection): Unit = () // TBA

  def bulkInsertNameComponents(
      nameComponents: List[NameComponent],
      col: BSONCollection): Unit = {
    nameComponents.foreach((n: NameComponent) => insertNameComponent(n, col))
  }

}

/** Companion object with constructor method. */
object DatabaseDriver {

  def apply(db: String): DatabaseDriver = new DatabaseDriver(db)
}
