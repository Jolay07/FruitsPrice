import scala.io.Source
import scala.io.StdIn.readLine

/** Utilities object holding methods for [FruitsCSV]
  */

  object Utilities {

  /** Get content from url
   *
   * @param srcPath source path
   * @param encoding default UTF8
   * @return Array of String Lines
   */
  def getLinesFromFile(srcPath: String, encoding:String="UTF8"): Array[String] = {
    val bufferedSource = Source.fromFile(srcPath, enc=encoding)
    val lines = bufferedSource.getLines.toArray
    bufferedSource.close
    lines
    }

  /** Save content
   *
   * @param lines content to save as Array of Strings
   * @param destPath destination path
   * @param sep String, default "\n"
   */
    def saveLines(lines: Array[String], destPath: String, sep: String = "\n"): Unit = {
      val txt = lines.mkString(sep)
      import java.io.{File, PrintWriter} //explicit import
      //import java.io._ //this was wildcard import meaning we got all of java.io library which we might not need
      val pw = new PrintWriter(new File(destPath))
      pw.write(txt)
      pw.close()
    }

  /** Method for custom printing
   *
   * @param list content to print, Array of Strings
   * @param separator String, default " | "
   */
    def customPrint(list: Array[String], separator: String = " | "): Unit = {
      for (index <- 0 until list.length) {
        print(list(index) + separator)
        if (index >= 4 && index % 4 == 0 ) print ("\n")
      }
      println()
    }

  /** Method to get the user to choose a specific item
   * using a while loop
   *
   * @param list content from which to choose, Array of Strings
   * @return chosen item, String
   */
  def selectSpecific(list: Array[String]):String = {
    println("Take a look at the options, choose one for which to get prices!")
    var userChoice = readLine("Type in the name: ")
      .toUpperCase
      .trim
    while(!list.exists(_.toUpperCase.contains(userChoice))) {
      userChoice = readLine("Not found! Try to type it in again: ")
    }
    println(s"$userChoice found.")
    println()
    userChoice
  }
}
