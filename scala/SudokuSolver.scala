/*
 * SudokuSolver.scala
 */

package sudoku

/**
 * A Sudoku solver based on the code at https://github.com/maruks/sudoku-scala/blob/master/src/main/scala/sudoku/SudokuSolver.scala
 */
object SudokuSolver {

  /**
   * Sentinel value used to signal failues in solve()
   */
  val emptyVector = Vector[Int]()

  /**
   * Sentinel value used to signal failues in getSmallestPossibleNumberOfValuesForOnePosition()
   */
  val noPossibilities = new PossibleValues(-1, emptyVector.toSet)

  /**
   * Attempt to fill all of the '0' entries in a board
   */
  def solve(board: Vector[Int]): Vector[Int] = {
    /*
     * Get a single PossibleValues record that contains a set of values that are
     * still available for one of the board positions that contains a '0' The
     * function used for this will return the position with the smallest number of
     * alternative values, as the others will be easier to solve than this one.
     */
    val record = getSmallestPossibleNumberOfValuesForOnePosition(board)
    if (record == noPossibilities) {
      /*
       * If there are none, then either the board is complete and we are done,
       * or the board is unsolvable and we need to backtrack. Both of these
       * conditions are dealt with by returning the current board. If the board
       * is complete we will cascade back through 81 - the number of pre
       * filled cells to this function and wind up back in solveBoard. Otherwise
       * we will pick up the search again in the first iteration of this function
       * that contains another board position that could work.
       */
      return board
    } else {
      /*
       * Get the possible values in the record as a sorted sequence (from 1 to 9)
       * The sorted sequence is turned into a Scala view, which is only an
       * optimization that makes backtracking less expensive in this program
       * which is strictly functional (i.e. like a Haskell program)
       */
      val values = record.values.toSeq.sorted.view

      /*
       * Try to advance the board by iterating through all the possible values
       * for the given position in the PossibleValues record. For each updated board
       * an attempt is mode to solve the next unfilled position until either the board
       * is solved or it is determined that none of the values we have can solve it...
       */
      val solutions = values.map(aValue => solve(board.updated(record.position, aValue)))

      /*
       * Get the first solution that was found to work. If there are none then
       * return the value emptyVector to signal to the caller (be it this routine,
       * or solveBoard), that we are either stuck. If the board is complete, we may
       * end up with more tham one solution, if so we simply return the first one.
       */
      val firstBoard = solutions.find(aBoard => aBoard.size > 0).getOrElse(emptyVector)

      /*
       * Return the final solution or emptyVector if we failed
       */
      return firstBoard
    }
  }

  /**
   * Returns a PossibleValues record for the next board position that should be filled,
   * or the value noPossibilities if there is no way of proceeding with this board
   * either because the board is solved or it is reached a stalemate state.
   */
  def getSmallestPossibleNumberOfValuesForOnePosition(board: Vector[Int]): PossibleValues = {
    /*
     * Get all the board positons (offsets into the board array) in a randomized order
     */
    val boardPositions = randomize(0 to 81 - 1)

    /*
     * Select only the positions that contain a '0'
     */
    val unsolvedEntries = boardPositions.filter(pos => board(pos) == 0)

    /*
     * Create a number of PossibleValues records that contains the unused
     * values for the row/column/square for each board entry that still has a '0'
     */
    val possibilities = unsolvedEntries.map(pos => new PossibleValues(pos, possibleValuesForBoardPosition(board, pos)))

    /*
     * If there are no values left that can go in any of the unfilled board positions
     * then return the value noPossibilities, which will force a backtrack.
     * Otherwise return the single PossibleValues record that has the smallest set
     * of values (as this one has the fewest possibilities it is better to fill this
     * before filling the others that have a larger number of possibilities).
     */
    return if (possibilities.isEmpty) noPossibilities else possibilities.min
  }

  /**
   * Get the set of values from 1 to 9 that do not already appear in the
   * row, column, and square corresponding to the requested board position (pos)
   */
  def possibleValuesForBoardPosition(board: Vector[Int], pos: Int): Set[Int] = {
    val fullSet   = (1 to 9).toSet
    val rowSet    = getBoardValuesForOffsets(board, getRowOffsets(pos))
    val columnSet = getBoardValuesForOffsets(board, getColumnOffsets(pos))
    val squareSet = getBoardValuesForOffsets(board, getSquareOffsets(pos))
    return fullSet -- rowSet -- columnSet -- squareSet
  }

  /**
   * Get a set of the current values in a board for a sequence of board positions
   */
  def getBoardValuesForOffsets(board: Vector[Int], seq : Seq[Int]): Set[Int] = {
    return seq.map(pos => board(pos)).toSet
  }

  /**
   * Get a sequence of board positions that correspond to the row of the supplied position
   */
  def getRowOffsets(pos: Int): Seq[Int] = {
    return pos - (pos % 9) to pos - (pos % 9) + 8
  }

  /**
   * Get a sequence of board positions that correspond to the column of the supplied position
   */
  def getColumnOffsets(pos: Int): Seq[Int] = {
    return (pos % 9) to (pos % 9) + 72 by 9
  }

  /**
   * Get a sequence of board positions that correspond to the sub square of the supplied position
   */
  def getSquareOffsets(pos: Int): Seq[Int] = {
    val x = (pos % 9) - ((pos % 9) % 3)
    val y = (pos / 9) - ((pos / 9) % 3)
    val spos = x + (y * 9)
    val r = (spos to spos + 2)
    return r ++ r.map(apos => apos + 9) ++ r.map(apos => apos + 18)
  }

  /**
   * Randomize the supplied sequence of integers using the Fisher-Yates shuffle,
   * (see: http://en.wikipedia.org/wiki/Fisher-Yates_shuffle) Randomizing the
   * evaluation order normally cuts the number of board evaluation to between 30%
   * and 40% of the number that would be done by evacuating them in order. The only
   * downside to this strategy is that if one repeatedly runs the same problem, the
   * number of iterations will be different. i.e. the program does not execute
   * deterministly, though the result should nevertheless be correct.
   */
  def randomize(seq: Seq[Int]): Seq[Int] = {
    val array = seq.toArray
    val rnd = new java.util.Random
    for (n <- Iterator.range(array.length - 1, 0, -1)) {
      val k = rnd.nextInt(n + 1)
      val t = array(k)
      array(k) = array(n)
      array(n) = t
    }
    return array
  }

  /**
   * This main entry point for this file, it is called with the board that need to be solved
   */
  def solveBoard(board: Vector[Int]): Vector[Int] = {
    return solve(board)
  }
}

/**
 * This is the class that defines a set of values that could be used for a particular board
 * position. Is is defined as an Ordered class so that is can be sorted, and the sort order
 * is defined by providing a compare method. This will cause the sort (which is called from
 * the call to min at the end of getSmallestPossibleNumberOfValuesForOnePosition) to return
 * the PossibleValues record with the smallest set of possible values first.  .
 */
class PossibleValues(pos: Int, vals: Set[Int]) extends Ordered[PossibleValues] {
  val position = pos
  val values   = vals
  def compare(other: PossibleValues) = this.values.size compare other.values.size
}