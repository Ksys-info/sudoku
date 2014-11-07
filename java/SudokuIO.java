package sudoku;

import java.io.*;
import java.util.*;
import java.nio.file.*;

public class SudokuIO {

    static Vector<Integer> readFile(File file) throws IOException {
        Vector<Integer> b = new Vector<Integer>();
        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        for (String line :lines) {
            for (String ch : line.split(",")) {
                b.add(Integer.parseInt(ch));
            }
        }
        return b;
    }


    static void writeFile(OutputStream os, Vector<Integer> v, int level) throws IOException {
        PrintWriter out = new PrintWriter(os,true);
        if (v.size() != 81) {
            out.write("no solution");
        } else {
            String pre = "";
            while (--level > 0) {
                pre += "  ";
            }
            for (int j = 0 ; j < 9 ; j++) {
                out.print(pre);
                for (int i = 0 ; i < 9 ; i++) {
                    if (i > 0) {
                        out.print(',');
                    }
                    out.print(Integer.toString(v.get(i+j*9)));
                }
                out.println();
            }
        }
    }
}
