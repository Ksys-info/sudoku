package sudoku

import java.io.File

object Main {
  def main(args: Array[String]): Unit = {

    args match {
      case Array(f) => {
        val p = SudokuIO.readFile(new File(f))
        val sol = SudokuSolver.solveBoard(p)
        SudokuIO.writeFile(System.out, sol)
      }
      case Array(f, a) => {
        val v = SudokuIO.readBatchFile(new File(f))
        val s = if (a == "p") v.par.map(SudokuSolver.solveBoard(_))
        else v.map(SudokuSolver.solveBoard(_))
        s.foreach(SudokuIO.writeFile(System.out, _))
      }
      case _ => println("Usage SudokuSolver <filename>")
    }
  }
}
