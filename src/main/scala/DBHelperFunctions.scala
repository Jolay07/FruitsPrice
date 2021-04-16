import java.sql.{Connection, PreparedStatement}

/** Here are functions that are used in relation to DB */

object DBHelperFunctions extends App {

  def migrateFruitPriceTable(connection: Connection) = {
    println("Migrating table for Fruits in EU")
    val statement = connection.createStatement()

    /** Function for creating a table FruitPrice
     * First column id as integer with autoincrement
     * columns in accordance with case class
     * "Category","Sector Code","Product_code","Product_desc","Description","Unit","Country","Period","MP Market Price" */

    val sqlCreateFruitPriceTable =
      """
        |DROP TABLE IF EXISTS FruitPrice;
        |CREATE TABLE IF NOT EXISTS FruitPrice (
        | Item_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        | Category TEXT NOT NULL,
        | Sector_code TEXT NOT NULL,
        | Product_code TEXT NOT NULL,
        | Product_desc TEXT NOT NULL,
        | Description TEXT NOT NULL,
        | Unit TEXT NOT NULL,
        | Country TEXT NOT NULL,
        | Period INTEGER NOT NULL,
        | MP_Market_Price DOUBLE NOT NULL
        |);
        |""".stripMargin

    statement.executeUpdate(sqlCreateFruitPriceTable)
    statement.close()
  }

  def insertFruitPriceEU(conn: Connection, fruitPriceEUObject: FruitPriceEU): Unit = {

    /** inserting an FruitPriceEU object into the created table
     * taking parameters from single object */

    println(s"Inserting Fruit Price in EU $fruitPriceEUObject")
    val insertSql =
      """
        |INSERT INTO FruitPrice (
        |    category,
        |    sector_code,
        |    product_code,
        |    product_desc,
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

}