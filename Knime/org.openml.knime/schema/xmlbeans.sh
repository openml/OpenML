#!/bin/bash
rm -f ../lib/schemas.jar
scomp -out ../lib/schemas.jar ./*.xsd
echo "Execution finished"
read -n 1 -s

