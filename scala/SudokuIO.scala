package sudoku

import java.io._
import io.Source
import scala.collection.immutable.VectorBuilder

object SudokuIO {

  def readFile(file: File): Vector[Int] = {
    val b = new VectorBuilder[Int]()
    b.sizeHint(9 * 9)
    Source.fromFile(file).getLines.foreach(line => b ++= line.split(',').map(s => Integer.parseInt(s.trim)))
    b.result
  }

  def readBatchFile(file: File): Iterable[Vector[Int]] = {
    val lines = Source.fromFile(file).getLines
    lines.map(line => line.trim.map(c => Integer.parseInt(c.toString)).asInstanceOf[Vector[Int]]).toIterable
  }

  def writeFile(os: OutputStream, v: Vector[Int], level: Int = 0): Unit = {
      writeFile0(os, if(v.size != 81) None else Some(v), level)
  }

  def writeFile0(os: OutputStream, v: Option[Vector[Int]], level: Int): Unit = {
    val out = new PrintWriter(os,true)
    v match {
      case None => out.println("no solution")
      case Some(s) => {
        val pre = "".padTo(level*2, ' ')
        val sol = for (g <- s.grouped(9).toList; str = (g.head.toString /: g.tail)(_ + ',' + _)) yield pre + str
        out.println((sol.head /: sol.tail)(_ + '\n' + _) + '\n')
      }
    }
  }
}
