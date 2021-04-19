import DBHelperFunctions._
//import com.sun.org.slf4j.internal.LoggerFactory

import org.slf4j.LoggerFactory
import java.sql.DriverManager
import scala.io.StdIn.readLine

object FruitsCSV extends App {

  // Data source: https://ec.europa.eu/info/food-farming-fisheries/farming/facts-and-figures/markets/prices/price-monitoring-sector/eu-prices-selected-representative-products_en#allproducts

  val relativePath = "src/resources/market-prices-fruit-products_en_6.csv"
  val lines = Utilities.getLinesFromFile(relativePath)

  //Adding logging to the project
  val log = LoggerFactory.getLogger("com.github.jolay07")
  log.debug("Try to get log!")
  // in place of print line we use logging.
  log.debug(s"We got ${lines.length} lines.")

  /** Splitting lines
   * split each line in tokens by "," separator and trim whitespace
   * take off " in line beginning and end */

  //val cols = lines.map(line => line.split("\",\"|\",|,\"").map(_.trim))
  val cols = lines.map(line => line.split("\",\"").map(_.trim))
  val endCols = cols.map(line => line.map(_.stripPrefix("\"").stripSuffix("\"")))

  val tokenCounts = for {line <- cols} yield line.length
  //Just check if in all lines are the same count of tokens and check if parsing works correct.
  log.debug(s"The minimum of ${tokenCounts.min} items is the same as max of ${tokenCounts.max} items")

  def convertToFruitPrice(tokens: Seq[String]):FruitPriceEU = {
    FruitPriceEU(
      tokens.head,
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
  /** We use case class.
   * check if everything work appropriate. */

  fruitPrices.slice(0, 5) foreach (println)

  //Filtering apples
  val allApples = fruitPrices.filter(fruit => fruit.Product.contains("Apple"))
  log.debug("-------Gala Apples--------")
  log.debug(s"There are ${allApples.length} lines with apples")
  //gala apples sorted by price DESC, in Germany
  val galaApplesSorted = allApples.sortBy(-_.Price).filter(fruit => fruit.Product.contains("Gala")
    && fruit.Country.equalsIgnoreCase("de"))
  log.debug(s"There are ${galaApplesSorted.length} lines with Gala apples in Germany. Printing some for testing: ")
  galaApplesSorted.slice(0, 4).foreach(println)

  /** Helper Function to revert back form Array[FruitPriceEU] so as to save in .csv format */
  def getFruitPriceEUCSV(fr: FruitPriceEU): String = s"${fr.Category},${fr.SectorCode},${fr.ProductCode}," +
    s"${fr.Product},${fr.Description},${fr.Unit},${fr.Country},${fr.Period},${fr.Price}"

  val columnNames = Array("Category, Sector Code, Product Code, Product, Description , Unit, Country, Period, Price")
  val galaAppleStrings = galaApplesSorted.map(line => getFruitPriceEUCSV(line))
  val rawAllApplesSorted = columnNames.concat(galaAppleStrings)

  val savePath = "src/resources/allGalaApplesSortedByPriceDesc.csv"
  //commented out, because already saved
  //Utilities.saveLines(rawAllApplesSorted, savePath)


  //DB section, create connection with Data Base
  val url = "jdbc:sqlite:./src/resources/dataBase/FruitPrice.db"
  val connection = DriverManager.getConnection(url)

  //Commented out, because table created
  //migrateFruitPriceTable(connection)

  //For testing purposes I just took 200 lines to save in DB - works
  //val pricesSliced = fruitPrices.slice(0,20)
  //pricesSliced.foreach(insertFruitPriceEU(connection, _))

  //save all fruit prices in one table, commented out, because already saved
  //fruitPrices.foreach(insertFruitPriceEU(connection, _))

  val galaFromDB = getGalaApples(connection)
  log.debug(s"There are ${galaFromDB.length} lines of Gala apples in Germany from DB. Printing some for testing: ")
  galaFromDB.slice(0, 4).foreach(println)


  log.debug("-------User chooses options for filtering------------")

  //Filtering all entries, getting unique product names
  val uniqueProducts = fruitPrices.map(_.Product).distinct
  for(line <- uniqueProducts) print(line + " | ")

  //user chooses a fruit
  println("Take a look at the list, choose one fruit for which to get prices!")
  var userProduct = readLine("Type in the fruit name: ")
    .toUpperCase
    .trim
  while(!(uniqueProducts.exists(_.toUpperCase == userProduct))) {
    userProduct = readLine("Fruit not found! Try to type it in again: ")
  }
  println(s"$userProduct found.")

  //filtering unique countries and user chooses one
  println("Now take a look at the countries and choose one!")
  val uniqueCountries = fruitPrices.map(_.Country).distinct
  for (i <- uniqueCountries) print(i + " | ")
  println()
  var userCountry = readLine("Type in the country code you chose! ")
    .toUpperCase
    .trim
  while(!(uniqueCountries.exists(_ == userCountry))) {
    println("Country not found!")
    userCountry = readLine("Country not found! Try to type it in again: ").toUpperCase
  }
    println(s"$userCountry found.")

  //filter results per user specifications
  val filterRes = fruitPrices.filter(fruit => fruit.Product.toUpperCase.contains(userProduct)
    && fruit.Country.equals(userCountry))
  if (filterRes.length == 0)
    println(s"No results found for the combination $userProduct and $userCountry")
  else {
    println(s"${filterRes.length} results found for the combination $userProduct and $userCountry")

    val sortingDir = readLine("How do you want to sort it? Type A for ascending and D for descending: ")
      .toUpperCase
      .trim

  var sortedRes = scala.collection.mutable.Seq.empty[FruitPriceEU].toArray
    if (sortingDir.equals("A")) {
      sortedRes = filterRes.sortBy(_.Price)
        sortedRes.slice(0, 5).foreach(println)
    }
    else if (sortingDir.equals("D")) {
      sortedRes = filterRes.sortBy(-_.Price)
        sortedRes.slice(0, 5).foreach(println)
    } else {
      println("User input was not understandable. Sorting descending!")
      sortedRes = filterRes.sortBy(-_.Price)
        sortedRes.slice(0, 5).foreach(println)
    }

  }

  connection.close()


}
