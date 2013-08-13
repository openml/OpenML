# Can a test implementation be uploaded?

source("C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/openML/R/authenticateUser.R")
source("C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/openML/R/server_interface.R")
source("C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/openML/R/xml_helpers.R")
source("C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/openML/R/uploadImplementation.R")

username <- "dominik.kirchhoff@tu-dortmund.de"
password <- "testpasswort"
sessionHash <- authenticateUser(username, password)

# test description and an empty implementation R-file
description <- "C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/test_implementation_desc.xml"
sourcefile <- "C:/Users/Dom/Desktop/Hiwi-Job/OpenML/R/test_implementation.R"

uploadOpenMLImplementation(description = description, 
                           sourcefile = sourcefile, 
                           session.hash = sessionHash) 
# Uploading run to server.
# Downloading response to: C:\Users\Dom\AppData\Local\Temp\Rtmp6vMr87\file1040309e1124
# Error in checkAndHandleErrorXML(file, doc, msg) : 
#   Error in server / XML response for: Uploading implementation
# Please provide description xml
# File: C:\Users\Dom\AppData\Local\Temp\Rtmp6vMr87\file1040309e1124

doc <- xmlParse(description)

uploadOpenMLImplementation(description = doc, 
                           sourcefile = sourcefile, 
                           session.hash = sessionHash)
# Uploading run to server.
# Downloading response to: C:\Users\Dom\AppData\Local\Temp\Rtmp6vMr87\file104044c44cab
# Error in .postForm(curl, .opts, .params, style) : 
#   STRING_ELT() can only be applied to a 'character vector', not a 'externalptr'