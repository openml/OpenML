
## An example run

library(devtools)

## This requires the development version of DMwR that is not yet
## released to the general public. It will not work with the current (0.3.0)
## version!
load_all("/home/ltorgo/Research/Publications/Books/DataMiningWithR/CRC/Livro/Package/DMwR_devel/DMwR")

## The openML package
load_all("/usersData/ltorgo/Research/Projects/OnGoing/Harvest/ResearchWork/OpenML/R/openML")


##################################################################
## USE CASE 1:
## - A user has checked in the OpenML a task that he wants to try. He got
## the task ID from the site.
## - The user wants to try a certain existing R algorithm he is
## familiar with, on this task to check its results and eventually
## upload them
##################################################################
## This was tested and it is currently running properly (ltorgo, 25Ja2013)
##

## Running a standard workflow using a regression tree on the iris openML task
## The user supplies:
## i) the implementation OpenML ID
## ii) the workflow function name
## iii) the list of the workflow parameters
## iv) the names of the packages the workflow depends upon (are need
##     for it to be run)
##
r <- runOnTask("9999",
                WFfunc='standardWF',
                WFpars=list(learner='rpartXse',learner.pars=list(se=0)),
                WFdeps=c('DMwR'))


## the resulting object (r) contains objects that should then be transformed
## to XML and uploaded

## -------------------
## the OpenMLImplementation object to be transformed into XML and uploaded
r$implementation

## the produced ouput (show of the class):
** Implementation Information ** 
Name    ::  standardWF 

Date ::  2013-01-25 22:07:15
Language ::  English

Description of the implementation :
The DMwR pre-defined workflow standardWF using rpartXse as the learner   

Dependencies ::  DMwR
Programming language ::  R
Operating system ::  linux-gnu
Source format ::  R script

## -------------------
## the file name where the R script corresponding to the workflow was
## automatically stored to be uploaded with the implementation
r$scriptFileName

## the produced output:
[1] "standardWF.rpartXse.R"


## -------------------
## the OpenMLRun object to be transformed into XML and uploaded
r$run

## the produced output:

** Information on an OpenML Run **

Task ID           ::  9999 
Implementation ID ::  known after uploading (to change) 


## -------------------
## the data frame with the results of the run to be uploaded with the run
head(r$runResults)

## the produced output:
    repeat fold row_id Iris-setosa Iris-versicolor Iris-virginica
139      1    1    139           0               0              1
122      1    1    122           0               0              1
109      1    1    109           0               0              1
136      1    1    136           0               0              1
103      1    1    103           0               0              1
17       1    1     17           1               0              0


##################################################################
## USE CASE 2:
## - A user has seen at the OpenML web site that a certain implementation
## (read workflow implemented in R) works well on tasks similar to
## the one he currently has.
## - He wants to apply this workflow to his task to get the results
##################################################################
## Tested and working (ltorgo, 25Jan2013)
##

data(iris)
idx <- sample(1:150,100)
tr <- iris[idx,]
ts <- iris[-idx,]

WFpreds <- useImplementation("impl1234",Species ~ .,tr,ts)

head(WFpreds)

## the resulting output:
   setosa versicolor virginica
2       1          0         0
3       1          0         0
4       1          0         0
5       1          0         0
8       1          0         0
17      1          0         0
