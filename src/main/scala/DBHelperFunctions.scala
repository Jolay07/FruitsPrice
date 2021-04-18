import java.sql.{Connection, PreparedStatement}

/** Here are functions that are used in relation to DB */

object DBHelperFunctions extends App {

  def migrateFruitPriceTable(connection: Connection):Unit = {
    println("Migrating table for Fruits in EU")
    val statement = connection.createStatement()

    /** Function for creating a table FruitPrice
     * First column id as integer with autoincrement
     * columns in accordance with case class
     * "Category","Sector Code","Product_code","Product","Description","Unit","Country","Period","MP Market Price" */

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

  def insertFruitPriceEU(conn: Connection, fruitPriceEUObject: FruitPriceEU): Unit = {

    /** inserting an FruitPriceEU object into the created table
     * taking parameters from single object */

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
