import java.text.SimpleDateFormat

import Main.context
import org.apache.spark.sql.functions.{explode, split, udf, when}
import org.apache.spark.sql.types.{LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row}

import scala.util.matching.Regex


case class Cleaner() {

  def selectValueFromGivenCount(data: DataFrame, limit: String, column: String) = {
    data.groupBy(column).count()
      .filter("count "+limit)
      .sort("count")
      .select(column)
      //.show(200)
      .collect()
  }
}

object Cleaner{

  /**
    * Function that renders defaultValue if this defaultValue is contained in a given list of String, otherwise it returns the value of the column that is being processed.
    * @param list
    * @param defaultValue
    * @return
    */
  def udf_check(list: Set[String], defaultValue: String) = {
    udf {(s: String) => if(list.contains("["+s+"]")){
      defaultValue
    }
    else{
      s
    }}
  }

def udf_clean_network = {
    udf {(s: String) =>
      /*if(list.contains("["+s+"]")){
            defaultValue
          }
          else{
            s
          }*/
      val ethernet = new Regex("1(.*)")
      val wifi = new Regex("2(.*)")
      val cell1 = new Regex("3(.*)")
      val cell2 = new Regex("4(.*)")
      val cell3 = new Regex("5(.*)")
      val cell4 = new Regex("6(.*)")

      s match {
        case ethernet(x) => "Ethernet"
        case wifi(x) => "Wifi"
        case cell1(x) => "Cellular Network Unknown"
        case cell2(x) => "2G"
        case cell3(x) => "3G"
        case cell4(x) => "4G"
        case _ => "Unknown"
      }
    }
  }

  def udf_clean_timestamp = {
    udf((col: String) => {
      val ts = col.toInt * 1000L
      val df = new SimpleDateFormat("HH")
      val hour = df.format(ts)
        
      hour match{
        case x if (x.toInt >= 0 && x.toInt <= 6) => "night"
        case x if (x.toInt > 20) => "night"
        case x if (x.toInt > 6 && x.toInt <= 12) => "morning"
        case x if (x.toInt > 12 && x.toInt <= 20) => "afternoon"
        case _ => "Other"
      }
    })
  }

   /**
 **udf to replace name of interests
 **/
   def udf_renameI = {
    udf { (s: String) =>
      /*if(list.contains("["+s+"]")){
            defaultValue
          }
          else{
            s
          }*/
      val IAB1 = new Regex("IAB1-(.*)")
      val IAB2 = new Regex("IAB2-(.*)")
      val IAB3 = new Regex("IAB3-(.*)")
      val IAB4 = new Regex("IAB4-(.*)")
      val IAB5 = new Regex("IAB5-(.*)")
      val IAB6 = new Regex("IAB6-(.*)")
      val IAB7 = new Regex("IAB7-(.*)")
      val IAB8 = new Regex("IAB8-(.*)")
      val IAB9 = new Regex("IAB9-(.*)")
      val IAB10 = new Regex("IAB10-(.*)")
      val IAB11 = new Regex("IAB11-(.*)")
      val IAB12 = new Regex("IAB12-(.*)")
      val IAB13 = new Regex("IAB13-(.*)")
      val IAB14 = new Regex("IAB14-(.*)")
      val IAB15 = new Regex("IAB15-(.*)")
      val IAB16 = new Regex("IAB16-(.*)")
      val IAB17 = new Regex("IAB17-(.*)")
      val IAB18 = new Regex("IAB18-(.*)")
      val IAB19 = new Regex("IAB19-(.*)")
      val IAB20 = new Regex("IAB20-(.*)")
      val IAB21 = new Regex("IAB21-(.*)")
      val IAB22 = new Regex("IAB22-(.*)")
      val IAB23 = new Regex("IAB23-(.*)")
      val IAB24 = new Regex("IAB24-(.*)")
      val IAB25 = new Regex("IAB25-(.*)")
      val IAB26 = new Regex("IAB26-(.*)")
      /*val IAB27 = new Regex("IAB27-(.*)")
      val IAB28 = new Regex("IAB28-(.*)")
      val IAB29 = new Regex("IAB29-(.*)")*/

      s match {
        case IAB1(x) => "IAB1-"
        case IAB2(x) => "IAB2-"
        case IAB3(x) => "IAB3-"
        case IAB4(x) => "IAB4-"
        case IAB5(x) => "IAB5-"
        case IAB6(x) => "IAB6-"
        case IAB7(x) => "IAB7-"
        case IAB8(x) => "IAB8-"
        case IAB9(x) => "IAB9-"
        case IAB10(x) => "IAB10-"
        case IAB11(x) => "IAB11-"
        case IAB12(x) => "IAB12-"
        case IAB13(x) => "IAB13-"
        case IAB14(x) => "IAB14-"
        case IAB15(x) => "IAB15-"
        case IAB16(x) => "IAB16-"
        case IAB17(x) => "IAB17-"
        case IAB18(x) => "IAB18-"
        case IAB19(x) => "IAB19-"
        case IAB20(x) => "IAB20-"
        case IAB21(x) => "IAB21-"
        case IAB22(x) => "IAB22-"
        case IAB23(x) => "IAB23-"
        case IAB24(x) => "IAB24-"
        case IAB25(x) => "IAB25-"
        case IAB26(x) => "IAB26-"
        /*case IAB27(x) => "IAB27"
        case IAB28(x) => "IAB28"
        case IAB29(x) => "IAB29"*/
        case _ => "Other"
        //case _ => s
      }
    }
  }

  def handleInterest(data: DataFrame) : DataFrame = {
    //TODO: clean interests
    val renamedInterests= data.withColumn("listInterests", Cleaner.udf_renameI((data("interests")))).select("listInterests")
    val updatedInterestsArray = renamedInterests.withColumn("uniqueInterests", explode(split(renamedInterests("listInterests"), ","))).select("uniqueInterests").distinct()

    //identify the ones that are not well written
    //val nonIAB = updateInterestsArray.distinct().where(not(updateInterestsArray("listInterests").contains("IAB"))).show()

    // val arrayOfInterests =  updatedInterestsArray.withColumn("interestsArray", split(updatedInterestsArray("uniqueInterests"), ",")).select( "interestsArray").show()

    //var target = network.withColumn("interestsArray", split(network("interests"), ",")).select("user", "interestsArray", "interests")

    var target = data.select("user", "interests")

    //list of string that represents the list of renamed interests
    val listOfUniqueInterest: List[String] = updatedInterestsArray.select("uniqueInterests").collect().map(row => row.toString()).toList

    //result: a list of column that contains, for each user, either 1 if he/she is interested in the concerned interest(ie corresponds to the name of the column)
    val l1 = listOfUniqueInterest.map(x => target.withColumn(x, when(target("interests").contains(x.substring(1, x.size-2))|| target("interests").contains(x.substring(1, x.size-3)), 1).otherwise(0)).select(x))

    /*val l1 = l2.map(x => {
      val indexer = new StringIndexer()
        .setInputCol(x.toString())
        .setOutputCol(x.toString()+"Index"))
      val indexed = indexer.fit(x).transform(x)
      indexed
    })*/


    //TODO: 3 steps left
    //Step1: create a dataframe that contains each element of the list l1 as columns

    /**
      * Add Column Index to dataframe
      */
    def addColumnIndex(df: DataFrame) = {
      context.sqlContext.createDataFrame(
        df.rdd.zipWithIndex.map {
          case (row, index) => Row.fromSeq(row.toSeq :+ index)
        },
        // Create schema for index column
        StructType(df.schema.fields :+ StructField("index", LongType, false)))
    }

    val interestDF = l1.map(col=>{
      val df = col.toDF()
      addColumnIndex(df)
    })

    def recursiveJoinOnIndex(list: List[DataFrame]): DataFrame = {
      if (list.isEmpty){
        null
      }
      else if(list.size >1){
        list.head.join(recursiveJoinOnIndex(list.tail),"index")
      }
      else {
        list.head
      }
    }
    val interestData = recursiveJoinOnIndex(interestDF).drop("index")

    val dfIndexed = addColumnIndex(data)
    val interestDfIndexed = addColumnIndex(interestData)



    val cleanedData = recursiveJoinOnIndex(List(dfIndexed,interestDfIndexed)).drop("index").drop("user").drop("interests")
    cleanedData
    /*val arrayOfUniqueInterest: Array[String] = updatedInterestsArray.select("uniqueInterests").collect().map(row => row.toString())

    //encode each column interest as a vector
    val encoderI = new OneHotEncoderEstimator()
      .setInputCols(arrayOfUniqueInterest)
      .setOutputCols(arrayOfUniqueInterest.map(x => x+"Vec"))
    val modelI = encoderI.fit(cleanedData)
    val encodedI = modelI.transform(cleanedData)


    //create a vectore that assembles all the interests
    val assemblerI = new VectorAssembler()
      .setInputCols(arrayOfUniqueInterest.map(x => x+"Vec"))
      .setOutputCol("interestsVectorAssembler")

    val ouputI = assemblerI.transform(encodedI)
    ouputI*/
  }

  
}
