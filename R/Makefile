R	:= R 
RSCRIPT	:= Rscript
DELETE	:= rm -fR

.SILENT:
.PHONEY: clean package install test check

usage:
	echo "Available targets:"
	echo ""
	echo " clean         - Clean everything up"
	echo " package       - build source package"
	echo " install       - install the package"
	echo " test          - run tests"
	echo " check         - run R CMD check on the package"

clean:
	echo "Cleaning up ..."
	${DELETE} openML.Rcheck
	${DELETE} .RData .Rhistory

  
package: clean
	echo "Building package file ..."
	${R} CMD build openML
  
install: clean
	echo "Installing package ..."
	${R} CMD INSTALL openML

test: install
	echo "Testing package ..."
	${RSCRIPT} ./test_all.R

check: clean 
	echo "Running R CMD check ..."
	${R} CMD check openML

