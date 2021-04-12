import java.sql.Connection
import java.sql.{Connection, DriverManager, PreparedStatement}

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
  def migrateFruitPriceTable(connection:Connection) = {
    println("Migrating table for Fruits in EU")
    val statement = connection.createStatement()
    //Creates a Statement object for sending SQL statements to the database.

    //"Category","Sector Code","Product_code","Product_desc","Description","Unit","Country","Period","MP Market Price"
    val sql =
      """
        |DROP TABLE IF EXISTS FruitPrice;
        |CREATE TABLE IF NOT EXISTS FruitPrice (
        | Category TEXT NOT NULL,
        | Sector Code TEXT NOT NULL,
        | Product_code TEXT NOT NULL,
        | Product_desc TEXT NOT NULL,
        | Description TEXT NOT NULL,
        | Unit TEXT NOT NULL,
        | Country TEXT NOT NULL,
        | Period INTEGER NOT NULL,
        | MP Market Price DOUBLE NOT NULL
        |);
        |""".stripMargin

    statement.executeUpdate(sql)
  }

  migrateFruitPriceTable(connection)


}
