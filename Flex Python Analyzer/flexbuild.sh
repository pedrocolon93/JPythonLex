#!/bin/bash
# My first script

flex pyflex.txt
echo Done flexing
gcc lex.yy.c -o out
echo Done compiling
./out lexemes.txt
