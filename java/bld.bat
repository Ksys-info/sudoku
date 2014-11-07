@echo off
del sudoko.jar
mkdir sudoku
copy *.java sudoku  > nul
javac sudoku\*.java
jar cf sudoko.jar sudoku\*.class
del /Q sudoku\*.*
rmdir sudoku

