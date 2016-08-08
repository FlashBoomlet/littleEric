package com.flashboomlet.io

import com.flashboomlet.models.NameComponent
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONInteger
import reactivemongo.bson.BSONString

/**
  * Created by trill on 8/7/16.
  */
class NameComponentImplicits extends MongoConstants {

  implicit object NameComponentWriter extends BSONDocumentWriter[NameComponent] {

    override def write(nameComponent: NameComponent): BSONDocument = BSONDocument(
      NameComponentConstants.Name -> BSONString(nameComponent.name),
      NameComponentConstants.Count -> BSONInteger(nameComponent.count),
      NameComponentConstants.Percentages -> MongoUtil.mapToBSONDocument(nameComponent.percentages-)
    )
  }


  implicit object NameComponentReader extends BSONDocumentReader[NameComponent] {

    override def read(doc: BSONDocument): NameComponent = {
      val name = doc.getAs[String](NameComponentConstants.Name).get
      val count = doc.getAs[Int](NameComponentConstants.Count).get
      val percentages = doc.getAs[BSONDocument](NameComponentConstants.Percentages).get

      NameComponent(
        name = name,
        count = count,
        percentages = MongoUtil.bsonDocumentToMap(percentages)
      )
    }
  }
}
