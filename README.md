sudoku
======

Suduko in Scala and Java 8

To help someone who was learning Scala I took the Sudoku implementation at :

    https://github.com/maruks/sudoku-scala/blob/master/src/main/scala/sudoku/SudokuSolver.scala

and simplified/commented it so it would be easier to understand for a novice Java programmer. I removed the use of Option, Symbol, tuples, partial functions/currying, and anonymous parameters, and I changed many identifiers soTheyWouldGenerallyBe MoreMeaningful and in many places put explicit type declarations where they would normally be inferred.
When I finished this I thought this might now be in a form that could fairly easily be translated into Java 8. So I did this as an exercise. This is the only Java 8 program I have ever written, so there may be some things that could be done a little more elegantly, but for the most part I am reasonably happy with the result.