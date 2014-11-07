/*
 * SudokuSolver.java
 */

package sudoku;

import java.util.*;
import java.util.stream.*;

/**
 * A Sudoku solver based on the code at https://github.com/maruks/sudoku-scala/blob/master/src/main/scala/sudoku/SudokuSolver.scala
 */
public class SudokuSolver {

    /**
     * Sentinel value used to signal failues in solve()
     */
    private final static Vector<Integer> emptyVector = new Vector<Integer>();

    /**
     * Sentinel value used to signal failues in getSmallestPossibleNumberOfValuesForOnePosition()
     */
    private final static PossibleValues noPossibilities = new PossibleValues(-1, new HashSet<Integer>(emptyVector));

    /**
     * Attempt to fill all of the '0' entries in a board
     */
    private static Vector<Integer> solve(Vector<Integer> board) {
        /*
         * Get a single PossibleValues record that contains a set of values that are
         * still available for one of the board positions that contains a '0' The
         * function used for this will return the position with the smallest number of
         * alternative values, as the others will be easier to solve than this one.
         */
        PossibleValues record = getSmallestPossibleNumberOfValuesForOnePosition(board);
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
            return board;
        } else {
            /*
             * Get the possible values in the record as a sorted sequence (from 1 to 9)
             */
            Stream<Integer> values = record.values.stream().sorted();

            /*
             * Try to advance the board by iterating through all the possible values
             * for the given position in the PossibleValues record. For each updated board
             * an attempt to solve the next unfilled position is made until the board is
             * solved or it is determined not to be solvable.
             */
            Stream<Vector<Integer>> solutions = values.map(aValue -> solve(setVector(board,record.position, aValue)));

            /*
             * Get the first solution that was found to work. If there are none then
             * return the value emptyVector to signal to the caller (be it this routine,
             * or solveBoard), that we are either stuck. If the board is complete, we may
             * end up with more tham one solution, if so we simply return the first one.
             */
            Vector<Integer> firstBoard = solutions.filter(aBoard -> aBoard.size() > 0).findFirst().orElse(emptyVector);

            /*
             * Return the final solution or emptyVector if we failed
             */
            return firstBoard;
        }
    }

    /**
     * Copies a Vector and changes one element
     */
    private static Vector<Integer> setVector(Vector<Integer> board, int pos, int value) {
        Vector<Integer> res = new Vector<Integer>(board);
        res.set(pos, value);
        return res;
    }

    /**
     * Returns a PossibleValues record for the next board position that should be filled,
     * or the value noPossibilities if there is no way of proceeding with this board
     * either because the board is solved or it is reached a stalemate state.
     */
    private static PossibleValues getSmallestPossibleNumberOfValuesForOnePosition(Vector<Integer> board) {

        /*
         * Get all the board positons (offsets into the board array) in a randomized order
         */
        IntStream boardPositions = randomize(IntStream.range(0, 81));

        /*
         * Select only the positions that contain a '0'
         */
        IntStream unsolvedEntries = boardPositions.filter(pos -> board.get(pos) == 0);

        /*
         * Create a number of PossibleValues records that contains the unused
         * values for the row/column/square for each board entry that still has a '0'
         */
        Stream<PossibleValues> possibilities = unsolvedEntries.boxed().map(pos -> new PossibleValues(pos, possibleValuesForBoardPosition(board, pos)));

        /*
         * If there are no values left that can go in any of the unfilled board positions
         * then return the value noPossibilities, which will force a backtrack.
         * Otherwise return the single PossibleValues record that has the smallest set
         * of values (as this one has the fewest possibilities it is better to fill this
         * before filling the others that have a larger number of possibilities).
         */
        return possibilities.min((PossibleValues a, PossibleValues b) -> Integer.compare(a.values.size(), b.values.size())).orElse(noPossibilities);
    }

    /**
     * Get the set of values from 1 to 9 that do not already appear in the
     * row, column, and square corresponding to the requested board position (pos)
     */
    private static Set<Integer> possibleValuesForBoardPosition(Vector<Integer> board, int pos) {
        Set<Integer> fullSet   = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
        Set<Integer> rowSet    = getBoardValuesForOffsets(board, getRowOffsets(pos));
        Set<Integer> columnSet = getBoardValuesForOffsets(board, getColumnOffsets(pos));
        Set<Integer> squareSet = getBoardValuesForOffsets(board, getSquareOffsets(pos));
        fullSet.removeAll(rowSet);
        fullSet.removeAll(columnSet);
        fullSet.removeAll(squareSet);
        return fullSet;
    }

    /**
     * Get a set of the current values in a board for a sequence of board positions
     */
    private static Set<Integer> getBoardValuesForOffsets(Vector<Integer> board, Set<Integer> set) {
        return set.stream().map(pos -> board.get(pos)).collect(Collectors.toSet());
    }

    /**
     * Get a sequence of board positions that correspond to the row of the supplied position
     */
    private static Set<Integer> getRowOffsets(int pos) {
        int start = pos - (pos % 9);
        return mkSet(
                      start + 0,
                      start + 1,
                      start + 2,
                      start + 3,
                      start + 4,
                      start + 5,
                      start + 6,
                      start + 7,
                      start + 8
                    );
    }

    /**
     * Get a sequence of board positions that correspond to the column of the supplied position
     */
    private static Set<Integer> getColumnOffsets(int pos) {
        int start = pos % 9;
        return mkSet(
                      start + 0 * 9,
                      start + 1 * 9,
                      start + 2 * 9,
                      start + 3 * 9,
                      start + 4 * 9,
                      start + 5 * 9,
                      start + 6 * 9,
                      start + 7 * 9,
                      start + 8 * 9
                    );

    }

    /**
     * Get a sequence of board positions that correspond to the sub square of the supplied position
     */
    private static Set<Integer> getSquareOffsets(int pos) {
        int x = (pos % 9) - ((pos % 9) % 3);
        int y = (pos / 9) - ((pos / 9) % 3);
        int start = x + (y * 9);
        return mkSet(
                      start + 0 + (0 * 9),
                      start + 1 + (0 * 9),
                      start + 2 + (0 * 9),
                      start + 0 + (1 * 9),
                      start + 1 + (1 * 9),
                      start + 2 + (1 * 9),
                      start + 0 + (2 * 9),
                      start + 1 + (2 * 9),
                      start + 2 + (2 * 9)
                    );
    }

    /**
     * Makes a Set from the int parms
     */
    private static Set<Integer> mkSet(Integer... args) {
        return new HashSet<Integer>(Arrays.asList(args));
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
    private static IntStream randomize(IntStream seq) {
        List<Integer> list = seq.boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        IntStream.Builder b = IntStream.builder();
        for (int i : list) {
            b.add(i);
        }
        return b.build();
    }

    /**
     * This main entry point for this file, it is called with the board that need to be solved
     */
    public static Vector<Integer> solveBoard(Vector<Integer> board) {
        return solve(board);
    }

    /**
     * This is the class that defines a set of values that could be used for a particular
     * board position.
     */
    private static class PossibleValues {
        int position;
        Set<Integer> values;

        PossibleValues(int pos, Set<Integer> vals) {
            position = pos;
            values   = vals;
        }
    }
}
