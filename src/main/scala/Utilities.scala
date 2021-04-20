import scala.io.Source

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
}
