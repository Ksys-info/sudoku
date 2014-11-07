package sudoku

import java.io.File
import scala.collection.immutable.VectorBuilder

object MainSS {
  def main(args: Array[String]): Unit = {
    args match {
      case Array(f) => {
        val dir = new File(f)
        for (file <- dir.listFiles) {
          if (file.getName.endsWith(".ss")) {
            println("Found "+file)
            val input = read(file.getAbsolutePath)
            val useful = input.filter(line => line.length > 0 && !line.startsWith("-"))
            if (useful.length < 9) {
              println("File seems to have the wrong mnumber of lines " + useful.length);
            }
            val converted = useful.take(9).map(line => line.replaceAll("\\|", "").replaceAll("\\.", "0"))
            val b = new VectorBuilder[Int]()
            converted.foreach(line => b ++= line/*.split("[0-9]")*/.map(ch => Integer.parseInt(""+ch)))
            val board = b.result
            val sol = SudokuSolver.solveBoard(board)
            println(if (sol.length == 81) "Worked" else "Failed")
            if (sol.length != 81) {
              SudokuIO.writeFile(System.out, sol)
            }
          }
        }
      }
      case _ => println("Usage SudokuSolver <filename>")
    }
  }

  def read(name: String) = {
    using (scala.io.Source.fromFile(name)) {
      src => src.getLines.toArray
    }
  }

  def using[A <: {def close(): Unit}, B] (param: A) (f: A => B): B = {
    import scala.language.reflectiveCalls
    try {f(param)} finally {param.close()}
  }
}
