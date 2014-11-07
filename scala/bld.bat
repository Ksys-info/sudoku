@echo off
rem Build
del sudoko.jar
mkdir sudoku
scalac *.scala
jar cf sudoko.jar sudoku\*.class
del sudoku\*.class
rmdir sudoku
