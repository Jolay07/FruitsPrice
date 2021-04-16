import DBHelperFunctions._

object FruitsCSV extends App{

  val relativePath = "src/resources/market-prices-fruit-products_en_6.csv"
  val lines = Utilities.getLinesFromFile(relativePath)

  println(s"We got ${lines.length} lines.")

  //Splitting lines with Regex
  val cols = lines.map(line => line.split("\",\"|\",|,\"").map(_.trim))
  val endCols = cols.map(line => line.map(_.stripPrefix("\"").stripSuffix("\"")))

//  endCols.slice(0,10).foreach(line => println(line.mkString(" | ")))
  val tokenCounts = for {line <- cols} yield line.length
//Just check if in all lines are the same count of tokens and check if parsing works correct.
  println(s"The minimum of ${tokenCounts.min} items is the same as max of ${tokenCounts.max} items")

  def convertToFruitPrice(tokens: Seq[String]):FruitPriceEU = {
    FruitPriceEU(
      tokens(0),
      tokens(1),
      tokens(2),
      tokens(3),
      tokens(4),
      tokens(5),
      tokens(6),
      tokens(7),
      tokens(8).toDouble
    )
  }

  val fruitPrices = endCols.tail.map(convertToFruitPrice(_))
  //we don't need header. We have case class.
  //check if everything work.
  fruitPrices.slice(0,5)foreach(println)

  //DB section, create connection with Data Base 
  val url = "jdbc:sqlite:./src/resources/dataBase/FruitPrice.db"
  import java.sql.DriverManager

  val connection = DriverManager.getConnection(url)

  val statement = connection.createStatement()

  migrateFruitPriceTable(connection)

  //For testing purposes I just took 200 lines to save in DB
  val pricesSliced = fruitPrices.slice(0,200)
  pricesSliced.foreach(insertFruitPriceEU(connection, _))

  connection.close()


}
