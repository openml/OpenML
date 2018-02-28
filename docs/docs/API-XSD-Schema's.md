In order to ensure data integrity on the server, data that passed to upload functions is checked against XSD schema's. This ensures that the data that is uploaded is in the correct format, and does not contain any illegal characters. XSD schema's can be obtained through the API (exact links are provided in the API docs, but for example: https://www.openml.org/api/v1/xsd/openml.data.upload (where openml.data.upload can be replaced by any other schema's name). Also XML examples are provided, e.g., https://www.openml.org/api/v1/xml_example/data . The XSD schema's are exactly the same as used on the server. Whenever an upload fails and the server mentions an XML/XSD verification error, please run the uploaded xml against one of the provided XSD schema's, for example on this webtool: http://www.freeformatter.com/xml-validator-xsd.html

In order to maintain one XSD schema for both uploading and downloading stuff, the XSD sometimes contains more fields than seem necessary from the offset. Usually, the additional fields that are indicated as such in the comments (for example, in the upload dataset xsd this are the id, upload_date, etc fields).
The XSD's maintain basically three consistencies functions:
* Ensure that the correct fields are uploaded
* Ensure that the fields contain the correct data types. 
* Ensure that the fields do not contain to much characters for the database to upload. 

For the latter two, it is important to note that the XSD seldom accept default string content (i.e., xs:string). Rather, we use self defined data types, that use regular expressions to ensure the right content. Examples of these are oml:system_string128, oml:casual_string128, oml:basic_latin128, where the oml prefix is used, the name indicates the level of restriction and the number indicates the maximum size of the field.

IMPORTANT: The maximum field sizes are (often) chosen with great care. Do not extend them without consulting other team members. 