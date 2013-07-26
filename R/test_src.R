library(devtools)
library(XML)
library(RCurl)
library(BBmisc)
library(RWeka)

load_all("openML", reset = TRUE)
source('~/cos/OpenML/R/openML/R/uploadOpenMLRun.R')

options(warn = 2)

un = "bernd_bischl@gmx.net"
pwd = "jxaibdrm"
session.hash = authenticateUser(username = un, password = pwd)

file = "bla.xml"


uploadOpenMLRun(description = file, output.files = file, session.hash)

#source('~/cos/OpenML/R/openML/R/authenticateUser.R')
#downloadOpenMLImplementation("df", file, TRUE)

# 
# <?xml version="1.0" encoding="UTF-8"?>
#   <oml:authenticate xmlns:oml="http://open-ml.org/openml">
#   <oml:session_hash>CYWJBLVYIPQ42IGB1NHSTGP181Y4TQIWE45GGQ4P</oml:session_hash>
#   <oml:valid_until>2020-01-01 00:00:00</oml:valid_until>
#   </oml:authenticate>
#   
#   Error codes
# 
# 250: Please provide username
# Please provide the username as a POST variable
# 251: Please provide password
# Please provide the password (hashed as a MD5) as a POST variable
# 252: Authentication failed
# The username and password did not match any record in the database. Please note that the password should be hashed using md5
# openml
# 
