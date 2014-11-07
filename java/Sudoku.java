/*
 * Sudoku.java
 */

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Sudoku solver
 */
public class Sudoku {

    /**
     * The Current iteraton level
     */
     private int level = 0;

     private int count = 0;

     private int backs = 0;


    /**
     * The entry point
     *
     * @param args The command line arguments (whick are not used)
     */
    public static void main(String[] args) {
        new Sudoku().go();
    }

    private void go() {
        int[][] matrix = readInput();
        printMatrix(matrix);
        System.out.println();
        boolean res = solve(0, 0, matrix);
        System.out.println((res ? "Done" : "No Solution") + " interations = " + count + " backtracks = " + backs);
        writeMatrix(matrix, 0);

    }

    /**
     * Attempt to solve the supplied board starting from a specific starting point
     *
     * @param i The starting column number
     * @param j The starting row number
     * @param cells A two dimensional array of values that represent the board
     * @return true if the problem was sucesfully solved
     */
    private boolean solve(int i, int j, int[][] cells) {
        if (i == 9) {
            if (j == 8) {
                return true;
            } else {
                i = 0;
                j++;
            }
        }

        if (cells[i][j] != 0) {  // skip filled cells
            return solve(i + 1, j, cells);
        } else {
            level++;
            count++;
            writeMatrix(cells, level);
            for (int val = 1; val <= 9; val++) {
                if (legal(i, j,  val, cells)) {
                    cells[i][j] = val;
                    if (solve(i + 1, j, cells)) {
                        --level;
                        return true;
                    }
                }
            }
            cells[i][j] = 0; // reset on backtrack
            backs++;
            --level;
            return false;
        }
    }

    /**
     * Comment required
     */
    private boolean legal(int i, int j, int val, int[][] cells) {

        for (int k = 0; k < 9; ++k)  { // row
            if (val == cells[k][j]) {
                return false;
            }
        }

        for (int k = 0; k < 9; ++k) { // col
            if (val == cells[i][k]) {
                return false;
            }
        }

        int boxRowOffset = (i / 3) * 3;
        int boxColOffset = (j / 3) * 3;
        for (int k = 0; k < 3; ++k) {// box
            for (int m = 0; m < 3; ++m) {
                if (val == cells[boxRowOffset + k][boxColOffset + m]) {
                    return false;
                }
            }
        }

        return true; // no violations, so it's legal
    }

    /**
     * Comment required
     */
    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Comment required
     */
    private void writeMatrix(int[][] solution, int lvl) {
        String pref = "";
        while (--lvl > 0) {
            pref += "  ";
        }
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0) {
                System.out.println(pref + " -----------------------");
            }
            System.out.print(pref);
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) {
                    System.out.print("| ");
                }
                System.out.print(solution[i][j] == 0 ? " " : Integer.toString(solution[i][j]));
                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(pref + " -----------------------");
    }

    /**
     * Comment required
     */
    private int[][] readInput() {
        try {
            int row = 0;
            int col = 0;
            int[][] mat = new int[9][9];

            File file = new File("input.csv");
            BufferedReader bufRdr;
            bufRdr = new BufferedReader(new FileReader(file));
            String line = null;

            while((line = bufRdr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line,",");
                col=0;
                while (st.hasMoreTokens()) {
                    mat[row][col] = Integer.parseInt(st.nextToken());
                    //System.out.println("number["+row+"]["+col+"]:"+matrix[row][col]);
                    col++;
                }
                row++;
            }

            bufRdr.close();
            return mat;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("The following error occurred"+e);
        }
        return null;
    }
}