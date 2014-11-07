package sudoku;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            Vector<Integer> p   = SudokuIO.readFile(new File(args[0]));
            Vector<Integer> sol = SudokuSolver.solveBoard(p);
            SudokuIO.writeFile(System.out, sol, 0);
        } else {
            System.err.println("Usage SudokuSolver <filename>");
        }
    }
}
