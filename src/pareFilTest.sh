#!/bin/bash
# Define a string variable with a value
StringVal=$(ls ~/dev/VozzWorks/VozzWorksDist/lib/*.jar)
IFS='/'
# Iterate the string variable using for loop
for val in $StringVal; do
    read -a strArr <<< $val
    echo "Arr is $strArr"
    # Print each value of the array by using loop
 done
