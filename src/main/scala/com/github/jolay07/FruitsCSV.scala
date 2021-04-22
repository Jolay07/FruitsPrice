package com.github.jolay07

import com.github.jolay07.DBHelperFunctions.getGalaApples
import org.slf4j.LoggerFactory

import java.sql.DriverManager
import scala.io.StdIn.readLine

/** FruitsCSV object
 * provides .csv parsing/filtering
 *
 * @author Jolanta Ijannidi
 * @author Elīna Šime-Pilāne
 * @version 1.0
 */
object FruitsCSV extends App {

  // Data source:
  // https://ec.europa.eu/info/food-farming-fisheries/farming/facts-and-figures/markets/prices/price-monitoring-sector/eu-prices-selected-representative-products_en#allproducts

  val relativePath = "src/resources/market-prices-fruit-products_en_6.csv"
  val lines = Utilities.getLinesFromFile(relativePath)

  //Adding logging to the project
  val log = LoggerFactory.getLogger("com.github.jolay07")
  log.debug("Try to get log!")
  // in place of print line we use logging.
  log.debug(s"We got ${lines.length} lines.")

  //Splitting lines, split each line in by "," separator and trim whitespace
  //Take off " in line beginning and end
  val cols = lines.map(line => line.split("\",\"").map(_.trim))
  val endCols = cols.map(line => line.map(_.stripPrefix("\"").stripSuffix("\"")))

  val tokenCounts = for {line <- cols} yield line.length
  //Just check if in all lines are the same count of tokens and check if parsing works correct.
  log.debug(s"The minimum of ${tokenCounts.min} items is the same as max of ${tokenCounts.max} items")

  /** Taking a Sequence of String and converting it into a FruitPriceEU object
   *
   * @param tokens items in each line in the Sequence of Strings
   * @return FruitPriceEU
   */
  def convertToFruitPrice(tokens: Seq[String]): FruitPriceEU = {
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

  //We use case class.
  val fruitPrices = endCols.tail.map(convertToFruitPrice(_))

  fruitPrices.slice(0, 5).foreach(println)

  //Filtering apples
  val allApples = fruitPrices.filter(fruit => fruit.Product.contains("Apple"))
  log.debug("-------Gala Apples--------")
  log.debug(s"There are ${allApples.length} lines with apples")

  //gala apples sorted by price DESC, in Germany
  val galaApplesSorted = allApples.filter(fruit => fruit.Product.contains("Gala")
    && fruit.Country.equalsIgnoreCase("de")).sortBy(-_.Price)
  log.debug(s"There are ${galaApplesSorted.length} lines with Gala apples in Germany. Printing some for testing: ")
  galaApplesSorted.slice(0, 4).foreach(println)

  /** Helper Function to revert back form Array[FruitPriceEU] so as to save in .csv format
   *
   * @param fr FruitPriceEU object
   * @return String
   */
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

  //save all fruit prices in one table, commented out, because already saved
  //fruitPrices.foreach(insertFruitPriceEU(connection, _))

  //selecting specific fruit from DB
  val galaFromDB = getGalaApples(connection)
  log.debug(s"There are ${galaFromDB.length} lines of Gala apples in Germany from DB. Printing some for testing: ")
  galaFromDB.slice(0, 4).foreach(println)


  log.debug("-------User chooses options for filtering------------")

  //Filtering all entries, getting unique product names and printing them
  val uniqueProducts = fruitPrices.map(_.Product).distinct
  Utilities.customPrint(uniqueProducts)

  //user chooses a fruit name
  val userProduct = Utilities.selectSpecific(uniqueProducts)

  //filtering all entries, getting unique countries and printing them
  val uniqueCountries = fruitPrices.map(_.Country).distinct
  Utilities.customPrint(uniqueCountries)

  //user chooses a country
  val userCountry = Utilities.selectSpecific(uniqueCountries)

  //filter results using user specified fruit and country
  val filterRes = fruitPrices.filter(fruit => fruit.Product.toUpperCase.contains(userProduct)
    && fruit.Country.equals(userCountry))

  //if results exist, offer to sort asc or desc, default desc, printing first results for checking
  if (filterRes.length != 0) {
    log.debug(s"${filterRes.length} results found for the combination $userProduct and $userCountry")

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

    //user has choice to save results into file, default not saving
    val save = readLine("Do you want to save he results as .csv? Type Y for yes or N for no: ")
      .toUpperCase
      .trim

    if (save == "Y") {
      println("Saving results!")
      val userResults = sortedRes.map(line => getFruitPriceEUCSV(line))
      val rawUserResults = columnNames.concat(userResults)
      val destPath = "src/resources/userFilteringResults.csv"
      Utilities.saveLines(rawUserResults, destPath)

    } else if (save == "N") {
      println("Not saving")

    } else {
      println("Input not understandable. Not saving results!")
    }

  } else {
    println(s"No results found for the combination $userProduct and $userCountry")
    println("The filtering ends")
  }


  connection.close()


}
