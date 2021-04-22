package com.github.jolay07

import java.sql.{Connection, PreparedStatement}

/** DB Helper Functions object holding methods for FruitPrice parser [[com.github.jolay07]]
 * Methods for creating table and selecting data from the table
 */

object DBHelperFunctions {

  /** Creates a table FruitPrice
   * First column id as integer with autoincrement
   * Columns in accordance with case class
   * "Category","Sector Code","Product_code","Product","Description","Unit","Country","Period","MP Market Price"
   *
   * @param connection a connections with the DB
   */
  def migrateFruitPriceTable(connection: Connection): Unit = {
    println("Migrating table for Fruits in EU")
    val statement = connection.createStatement()

    val sqlCreateFruitPriceTable =
      """
        |DROP TABLE IF EXISTS FruitPrice;
        |CREATE TABLE IF NOT EXISTS FruitPrice (
        | Item_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        | Category TEXT NOT NULL,
        | Sector_code TEXT NOT NULL,
        | Product_code TEXT NOT NULL,
        | Product TEXT NOT NULL,
        | Description TEXT NOT NULL,
        | Unit TEXT NOT NULL,
        | Country TEXT NOT NULL,
        | Period TEXT NOT NULL,
        | MP_Market_Price DOUBLE NOT NULL
        |);
        |""".stripMargin

    statement.executeUpdate(sqlCreateFruitPriceTable)
    statement.close()
  }

  /** Inserting an FruitPriceEU object into the created table
   * Taking parameters from single object
   *
   * @param conn               connection with the DB
   * @param fruitPriceEUObject the object from which parameters are taken
   */
  def insertFruitPriceEU(conn: Connection, fruitPriceEUObject: FruitPriceEU): Unit = {

    //println(s"Inserting Fruit Price in EU $fruitPriceEUObject")

    val insertSql =
      """
        |INSERT INTO FruitPrice (
        |    category,
        |    sector_code,
        |    product_code,
        |    product,
        |    description,
        |    unit,
        |    country,
        |    period,
        |    mp_market_price)
        |VALUES (?,?,?,?,?,
        |         ?,?,?,?);
""".stripMargin

    val preparedStmt: PreparedStatement = conn.prepareStatement(insertSql)

    preparedStmt.setString(1, fruitPriceEUObject.Category)
    preparedStmt.setString(2, fruitPriceEUObject.SectorCode)
    preparedStmt.setString(3, fruitPriceEUObject.ProductCode)
    preparedStmt.setString(4, fruitPriceEUObject.Product)
    preparedStmt.setString(5, fruitPriceEUObject.Description)
    preparedStmt.setString(6, fruitPriceEUObject.Unit)
    preparedStmt.setString(7, fruitPriceEUObject.Country)
    preparedStmt.setString(8, fruitPriceEUObject.Period)
    preparedStmt.setDouble(9, fruitPriceEUObject.Price)
    preparedStmt.execute
    preparedStmt.close()
  }

  /** Select query for a specific type of fruit and specific country
   * Ordered descending
   *
   * @param conn connection with the DB
   * @return results as ListBuffer[FruitsPriceEU]
   */
  def getGalaApples(conn: Connection): Seq[FruitPriceEU] = {
    val statement = conn.createStatement()

    val sqlSelectGala =
      """
        |SELECT * from FruitPrice
        |WHERE Product LIKE '%Gala%' AND Country = 'DE'
        |ORDER BY MP_Market_Price DESC;
        |""".stripMargin

    val resultSet = statement.executeQuery(sqlSelectGala)
    val galaList = scala.collection.mutable.ListBuffer.empty[FruitPriceEU]

    while (resultSet.next()) {
      val galaApple = FruitPriceEU(resultSet.getString("Category"),
        resultSet.getString("Sector_code"),
        resultSet.getString("Product_code"),
        resultSet.getString("Product"),
        resultSet.getString("Description"),
        resultSet.getString("Unit"),
        resultSet.getString("Country"),
        resultSet.getString("Period"),
        resultSet.getString("MP_Market_Price").toDouble)
      galaList.append(galaApple)
      //    println(row.size)
    }
    conn.close()
    galaList.toSeq
  }

}
