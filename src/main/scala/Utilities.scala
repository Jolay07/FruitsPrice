import scala.io.Source
import scala.io.StdIn.readLine

/** Object to store all Utilities functions/methods that are used in other objects/classes
 * as not make multiple copies */

  object Utilities {

    def getLinesFromFile(srcPath: String): Array[String] = {
      val bufferedSource = Source.fromFile(srcPath)
      val lines = bufferedSource.getLines.toArray
      bufferedSource.close
      lines
    }

    def saveLines(lines: Array[String], destPath: String, sep: String = "\n"): Unit = {
      val txt = lines.mkString(sep)
      import java.io.{File, PrintWriter} //explicit import
      //import java.io._ //this was wildcard import meaning we got all of java.io library which we might not need
      val pw = new PrintWriter(new File(destPath))
      pw.write(txt)
      pw.close()
    }

    def customPrint(list: Array[String], separator: String = " | "): Unit = {
      for (index <- 0 until list.length) {
        print(list(index) + separator)
        if (index >= 4 && index % 4 == 0 ) print ("\n")
      }
      println()
    }

  def selectSpecific(list: Array[String]):String = {
    println("Take a look at the options, choose one for which to get prices!")
    var userChoice = readLine("Type in the name: ")
      .toUpperCase
      .trim
    while(!list.exists(_.toUpperCase.contains(userChoice))) {
      userChoice = readLine("Not found! Try to type it in again: ")
    }
    println(s"$userChoice found.")
    userChoice
  }
}
