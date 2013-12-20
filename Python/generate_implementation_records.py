import urllib2
import json
from time import gmtime, strftime

req = urllib2.Request("http://www.openml.org/api_query/free_query?q=SELECT+%2A+FROM+%60math_function%60")
opener = urllib2.build_opener()
f = opener.open(req)
json = json.loads(f.read())

sql = [];

for row in json['data']:
  name = "openml.evaluation." + row[0]
  version = "1.0"
  fullName = name + "(" + version + ")"
  creator = "Jan N. van Rijn"
  contributor = '"Bernd Bischl","Luis Torgo","Bo Gao","Venkatesh Umaashankar","Simon Fischer","Patrick Winter","Bernd Wiswedel","Michael R. Berthold","Joaquin Vanschoren"'
  uploadDate = strftime("%Y-%m-%d %H:%M:%S", gmtime())
  licence = "public domain"
  language = "english"
  description = 'An implementation of the evaluation measure "' + row[0] + '"'
  fullDescription = str(row[6]).replace("'","\\\'")
  installationNotes = "Runs on OpenML servers"
  dependencies = "Build on top of Weka API (Jar version 3.?.?)"
  implements = row[0]
  sql.append('INSERT INTO `implementation`(`fullName`, `name`, `version`, `creator`, `contributor`, `uploadDate`, `licence`, `language`, `description`, `fullDescription`, `installationNotes`, `dependencies`, `implements`) VALUES (\'' + str(fullName)  + "','" + str(name) + "','" + str(version) + "','" + str(creator) + "','" + str(contributor) + "','" + str(uploadDate) + "','" + str(licence) + "','" + str(language) + "','" + str(description) + "','" + str(fullDescription) + "','" + str(installationNotes) + "','" + str(dependencies) + "','" + str(implements) + '\');\n' )

for row in sql:
  
  print row
