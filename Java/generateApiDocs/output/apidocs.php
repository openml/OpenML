<li><a href="#openml_authenticate">openml.authenticate</a></li>
<li><a href="#openml_authenticate_check">openml.authenticate.check</a></li>

<li><a href="#openml_data">openml.data</a></li>
<li><a href="#openml_data_description">openml.data.description</a></li>
<li><a href="#openml_data_upload">openml.data.upload</a></li>
<li><a href="#openml_data_delete">openml.data.delete</a></li>
<li><a href="#openml_data_licences">openml.data.licences</a></li>
<li><a href="#openml_data_features">openml.data.features</a></li>
<li><a href="#openml_data_qualities">openml.data.qualities</a></li>
<li><a href="#openml_data_qualities_list">openml.data.qualities.list</a></li>

<li><a href="#openml_task_search">openml.task.search</a></li>
<li><a href="#openml_task_evaluations">openml.task.evaluations</a></li>
<li><a href="#openml_task_types">openml.task.types</a></li>
<li><a href="#openml_task_types_search">openml.task.types.search</a></li>

<li><a href="#openml_estimationprocedure_get">openml.estimationprocedure.get</a></li>

<!-- [START] Api function description: openml.authenticate --> 


<h3 id=openml_authenticate>openml.authenticate</h3>
<p><i>returns a session_hash, which can be used for writing to the API</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>POST username</code> (Required)</dt><dd>The username to be authenticated with</dd></dl>
<dl><dt><code>POST password</code> (Required)</dt><dd>An md5 hash of the password, corresponding to the username</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:authenticate xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:session_hash&gt;KB269Y8UP5IJENFFIKDKJNLEH10NESKPTCMT0KDG&lt;/oml:session_hash&gt;
  &lt;oml:valid_until&gt;2014-07-25 19:05:19&lt;/oml:valid_until&gt;
  &lt;oml:timezone&gt;Europe/Berlin&lt;/oml:timezone&gt;
&lt;/oml:authenticate&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>250: Please provide username</dt><dd>Please provide the username as a POST variable</dd></dl>
<dl><dt>251: Please provide password</dt><dd>Please provide the password (hashed as a MD5) as a POST variable</dd></dl>
<dl><dt>252: Authentication failed</dt><dd>The username and password did not match any record in the database. Please note that the password should be hashed using md5</dd></dl>
</div>

<!-- [END] Api function description: openml.authenticate -->  



<!-- [START] Api function description: openml.authenticate.check --> 


<h3 id=openml_authenticate_check>openml.authenticate.check</h3>
<p><i>checks the validity of the session hash</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>POST username</code> (Required)</dt><dd>The username to be authenticated with</dd></dl>
<dl><dt><code>POST session_hash</code> (Required)</dt><dd>The session hash to be checked</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:authenticate xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:valid_until&gt;2014-07-25 14:02:20&lt;/oml:valid_until&gt;
&lt;/oml:authenticate&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>290: Username not provided</dt><dd>Please provide username</dd></dl>
<dl><dt>291: Hash not provided</dt><dd>Please provide hash to be checked</dd></dl>
<dl><dt>292: Hash does not exist</dt><dd>Hash does not exist, or is not owned by this user</dd></dl>
</div>

<!-- [END] Api function description: openml.authenticate.check -->  




<!-- [START] Api function description: openml.data --> 


<h3 id=openml_data>openml.data</h3>
<p><i>Returns a list with all dataset ids in OpenML that are ready to use</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
None
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:did&gt;1&lt;/oml:did&gt;
  &lt;oml:did&gt;2&lt;/oml:did&gt;
  &lt;oml:did&gt;3&lt;/oml:did&gt;
  &lt;oml:did&gt;4&lt;/oml:did&gt;
  &lt;oml:did&gt;5&lt;/oml:did&gt;
  &lt;oml:did&gt;6&lt;/oml:did&gt;
  &lt;oml:did&gt;7&lt;/oml:did&gt;
  &lt;oml:did&gt;8&lt;/oml:did&gt;
  &lt;oml:did&gt;9&lt;/oml:did&gt;
  &lt;oml:did&gt;10&lt;/oml:did&gt;
&lt;/oml:data&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>370: No datasets available</dt><dd>There are no valid datasets in the system. Please upload!</dd></dl>
</div>

<!-- [END] Api function description: openml.data -->  



<!-- [START] Api function description: openml.data.description --> 


<h3 id=openml_data_description>openml.data.description</h3>
<p><i>returns dataset descriptions in XML</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET data_id</code> (Required)</dt><dd>The dataset id</dd></dl>
</div>
<h5>Schema's</h5>
<div class="bs-callout bs-callout-info">
<h5>openml.data.description</h5>

This XSD schema is applicable for both uploading and downloading data. <br/>
<a type="button" class="btn btn-primary" href="https://github.com/openml/OpenML/blob/master/XML/Schemas/dataset.xsd">XSD Schema</a>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data_set_description xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:id&gt;1&lt;/oml:id&gt;
  &lt;oml:name&gt;anneal&lt;/oml:name&gt;
  &lt;oml:version&gt;1&lt;/oml:version&gt;
  &lt;oml:description&gt;1. Title of Database: Annealing Data
 
 2. Source Information: donated by David Sterling and Wray Buntine.
 
 3. Past Usage: unknown
 
 4. Relevant Information:
    -- Explanation: I suspect this was left by Ross Quinlan in 1987 at the
       4th Machine Learning Workshop.  I'd have to check with Jeff Schlimmer
       to double check this.
 
 5. Number of Instances: 898
 
 6. Number of Attributes: 38
    -- 6 continuously-valued
    -- 3 integer-valued
    -- 29 nominal-valued
 
 7. Attribute Information:
     1. family:          --,GB,GK,GS,TN,ZA,ZF,ZH,ZM,ZS
     2. product-type:    C, H, G
     3. steel:           -,R,A,U,K,M,S,W,V
     4. carbon:          continuous
     5. hardness:        continuous
     6. temper_rolling:  -,T
     7. condition:       -,S,A,X
     8. formability:     -,1,2,3,4,5
     9. strength:        continuous
    10. non-ageing:      -,N
    11. surface-finish:  P,M,-
    12. surface-quality: -,D,E,F,G
    13. enamelability:   -,1,2,3,4,5
    14. bc:              Y,-
    15. bf:              Y,-
    16. bt:              Y,-
    17. bw/me:           B,M,-
    18. bl:              Y,-
    19. m:               Y,-
    20. chrom:           C,-
    21. phos:            P,-
    22. cbond:           Y,-
    23. marvi:           Y,-
    24. exptl:           Y,-
    25. ferro:           Y,-
    26. corr:            Y,-
    27. blue/bright/varn/clean:          B,R,V,C,-
    28. lustre:          Y,-
    29. jurofm:          Y,-
    30. s:               Y,-
    31. p:               Y,-
    32. shape:           COIL, SHEET
    33. thick:           continuous
    34. width:           continuous
    35. len:             continuous
    36. oil:             -,Y,N
    37. bore:            0000,0500,0600,0760
    38. packing: -,1,2,3
    classes:        1,2,3,4,5,U
  
    -- The '-' values are actually 'not_applicable' values rather than
       'missing_values' (and so can be treated as legal discrete
       values rather than as showing the absence of a discrete value).
 
 8. Missing Attribute Values: Signified with "?"
    Attribute:  Number of instances missing its value:
    1           0
    2           0
    3           70
    4           0
    5           0
    6           675
    7           271
    8           283
    9           0
   10           703
   11           790
   12           217
   13           785
   14           797
   15           680
   16           736
   17           609
   18           662
   19           798
   20           775
   21           791
   22           730
   23           798
   24           796
   25           772
   26           798
   27           793
   28           753
   29           798
   30           798
   31           798
   32           0
   33           0
   34           0
   35           0
   36           740
   37           0
   38           789
   39           0
 
 9. Distribution of Classes
      Class Name:   Number of Instances:
      1               8
      2              88
      3             608
      4               0
      5              60
      U              34
                    ---
                    798&lt;/oml:description&gt;
  &lt;oml:format&gt;ARFF&lt;/oml:format&gt;
  &lt;oml:upload_date&gt;2014-04-06 23:19:20&lt;/oml:upload_date&gt;
  &lt;oml:licence&gt;public domain&lt;/oml:licence&gt;
  &lt;oml:url&gt;http://openml.liacs.nl/files/download/1/dataset_1_anneal.arff&lt;/oml:url&gt;
  &lt;oml:md5_checksum&gt;08dc9d6bf8e5196de0d56bfc89631931&lt;/oml:md5_checksum&gt;
&lt;/oml:data_set_description&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>110: Please provide data_id</dt><dd>Please provide data_id</dd></dl>
<dl><dt>111: Unknown dataset</dt><dd>Data set description with data_id was not found in the database</dd></dl>
</div>

<!-- [END] Api function description: openml.data.description -->  



<!-- [START] Api function description: openml.data.upload --> 


<h3 id=openml_data_upload>openml.data.upload</h3>
<p><i>Uploads a dataset</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>POST description</code> (Required)</dt><dd>An XML file containing the data set description</dd></dl>
<dl><dt><code>POST dataset</code> (Required)</dt><dd>The dataset file to be stored on the server</dd></dl>
<dl><dt><code>POST session_hash</code> (Required)</dt><dd>The session hash, provided by the server on authentication (1 hour valid)</dd></dl>
</div>
<h5>Schema's</h5>
<div class="bs-callout bs-callout-info">
<h5>openml.data.upload</h5>

This XSD schema is applicable for both uploading and downloading data, hence some fields are not used.<br/>
<a type="button" class="btn btn-primary" href="https://github.com/openml/OpenML/blob/master/XML/Schemas/dataset.xsd">XSD Schema</a>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>130: Problem with file uploading</dt><dd>There was a problem with the file upload</dd></dl>
<dl><dt>131: Problem validating uploaded description file</dt><dd>The XML description format does not meet the standards</dd></dl>
<dl><dt>132: Failed to move the files</dt><dd>Internal server error, please contact api administrators</dd></dl>
<dl><dt>133: Failed to make checksum of datafile</dt><dd>Internal server error, please contact api administrators</dd></dl>
<dl><dt>134: Failed to insert record in database</dt><dd>Internal server error, please contact api administrators</dd></dl>
<dl><dt>135: Please provide description xml</dt><dd>Please provide description xml</dd></dl>
<dl><dt>136: Error slot open</dt><dd>Error slot open, will be filled by not yet defined error</dd></dl>
<dl><dt>137: Please provide session_hash</dt><dd>In order to share content, please authenticate (openml.authenticate) and provide session_hash</dd></dl>
<dl><dt>138: Authentication failed</dt><dd>The session_hash was not valid. Please try to login again, or contact api administrators</dd></dl>
<dl><dt>139: Combination name / version already exists</dt><dd>The combination of name and version of this dataset already exists. Leave version out for auto increment</dd></dl>
<dl><dt>140: Both dataset file and dataset url provided. Please provide only one</dt><dd>The system is confused since both a dataset file (post) and a dataset url (xml) are provided. Please remove one</dd></dl>
<dl><dt>141: Neither dataset file or dataset url are provided</dt><dd>Please provide either a dataset file as POST variable, xor a dataset url in the description XML</dd></dl>
<dl><dt>142: Error in processing arff file. Can be a syntax error, or the specified target feature does not exists</dt><dd>For now, we only check on arff files. If a dataset is claimed to be in such a format, and it can not be parsed, this error is returned.</dd></dl>
<dl><dt>143: Suggested target feature not legal</dt><dd>It is possible to suggest a default target feature (for predictive tasks). However, it should be provided in the data. </dd></dl>
</div>

<!-- [END] Api function description: openml.data.upload -->  



<!-- [START] Api function description: openml.data.delete --> 


<h3 id=openml_data_delete>openml.data.delete</h3>
<p><i>Deletes a dataset. Can only be done if the dataset is not used in tasks</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>POST session_hash</code> (Required)</dt><dd>The session hash to authenticate with</dd></dl>
<dl><dt><code>POST data_id</code> (Required)</dt><dd>The dataset to be deleted</dd></dl>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>350: Please provide session_hash</dt><dd>In order to remove your content, please authenticate (openml.authenticate) and provide session_hash</dd></dl>
<dl><dt>351: Authentication failed</dt><dd>The session_hash was not valid. Please try to login again, or contact api administrators</dd></dl>
<dl><dt>352: Dataset does not exists</dt><dd>The data id could not be linked to an existing dataset.</dd></dl>
<dl><dt>353: Dataset is not owned by you</dt><dd>The dataset was owned by another user. Hence you cannot delete it.</dd></dl>
<dl><dt>354: Dataset is in use by other content. Can not be deleted</dt><dd>The data is used in runs. Delete this other content before deleting this dataset. </dd></dl>
<dl><dt>355: Deleting dataset failed.</dt><dd>Deleting the dataset failed. Please contact support team.</dd></dl>
</div>

<!-- [END] Api function description: openml.data.delete -->  



<!-- [START] Api function description: openml.data.licences --> 


<h3 id=openml_data_licences>openml.data.licences</h3>
<p><i>Gives a list of all data licences used in OpenML</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
None
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data_licences xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:licences&gt;
    &lt;oml:licence&gt;public domain&lt;/oml:licence&gt;
    &lt;oml:licence&gt;UCI&lt;/oml:licence&gt;
  &lt;/oml:licences&gt;
&lt;/oml:data_licences&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
None
</div>

<!-- [END] Api function description: openml.data.licences -->  



<!-- [START] Api function description: openml.data.features --> 


<h3 id=openml_data_features>openml.data.features</h3>
<p><i>Returns the features (attributes) of a given dataset</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET data_id</code> (Required)</dt><dd>The dataset id</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data_features xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;family&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;0&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;product-type&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;1&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;steel&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;2&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;carbon&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;3&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;hardness&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;4&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;temper_rolling&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;5&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;condition&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;6&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;formability&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;7&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;strength&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;8&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;non-ageing&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;9&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;surface-finish&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;10&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;surface-quality&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;11&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;enamelability&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;12&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bc&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;13&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bf&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;14&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bt&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;15&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bw%2Fme&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;16&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bl&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;17&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;m&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;18&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;chrom&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;19&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;phos&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;20&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;cbond&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;21&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;marvi&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;22&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;exptl&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;23&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;ferro&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;24&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;corr&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;25&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;blue%2Fbright%2Fvarn%2Fclean&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;26&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;lustre&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;27&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;jurofm&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;28&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;s&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;29&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;p&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;30&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;shape&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;31&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;thick&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;32&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;width&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;33&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;len&lt;/oml:name&gt;
    &lt;oml:data_type&gt;numeric&lt;/oml:data_type&gt;
    &lt;oml:index&gt;34&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;oil&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;35&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;bore&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;36&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;packing&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;37&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
  &lt;oml:feature&gt;
    &lt;oml:name&gt;class&lt;/oml:name&gt;
    &lt;oml:data_type&gt;nominal&lt;/oml:data_type&gt;
    &lt;oml:index&gt;38&lt;/oml:index&gt;
  &lt;/oml:feature&gt;
&lt;/oml:data_features&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>270: Please provide data_id</dt><dd>Please provide data_id</dd></dl>
<dl><dt>271: Unknown dataset</dt><dd>Data set description with data_id was not found in the database</dd></dl>
<dl><dt>272: No features found</dt><dd>The registered dataset did not contain any features</dd></dl>
<dl><dt>273: Dataset not processed yet</dt><dd>The dataset was not processed yet, no features are available. Please wait for a few minutes. </dd></dl>
<dl><dt>274: Dataset processed with error</dt><dd>The feature extractor has run into an error while processing the dataset. Please check whether it is a valid supported file. </dd></dl>
</div>

<!-- [END] Api function description: openml.data.features -->  



<!-- [START] Api function description: openml.data.qualities --> 


<h3 id=openml_data_qualities>openml.data.qualities</h3>
<p><i>Returns the qualities (meta-features) of a given dataset</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET data_id</code> (Required)</dt><dd>The dataset id</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data_qualities xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;ClassCount&lt;/oml:name&gt;
    &lt;oml:value&gt;6.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;ClassEntropy&lt;/oml:name&gt;
    &lt;oml:value&gt;-1.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DecisionStumpAUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.822828217876869&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DecisionStumpErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;22.828507795100222&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DecisionStumpKappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.4503332218612649&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DefaultAccuracy&lt;/oml:name&gt;
    &lt;oml:value&gt;0.76169265033408&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DefaultTargetNominal&lt;/oml:name&gt;
    &lt;oml:value&gt;1&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;DefaultTargetNumerical&lt;/oml:name&gt;
    &lt;oml:value&gt;0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;Dimensionality&lt;/oml:name&gt;
    &lt;oml:value&gt;0.043429844097995544&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;EquivalentNumberOfAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;-12.218452122298707&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;IncompleteInstanceCount&lt;/oml:name&gt;
    &lt;oml:value&gt;0.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;InstanceCount&lt;/oml:name&gt;
    &lt;oml:value&gt;898.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.00001.AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7880182273644211&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.00001.ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;12.249443207126948&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.00001.kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.6371863763080279&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.0001.AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9270456597451915&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.0001.ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;7.795100222717149&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.0001.kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7894969492796818&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.001.AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9270456597451915&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.001.ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;7.795100222717149&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;J48.001.kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7894969492796818&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MajorityClassSize&lt;/oml:name&gt;
    &lt;oml:value&gt;684&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MaxNominalAttDistinctValues&lt;/oml:name&gt;
    &lt;oml:value&gt;10.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanAttributeEntropy&lt;/oml:name&gt;
    &lt;oml:value&gt;-1.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanKurtosisOfNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;4.6070302750191185&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanMeansOfNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;348.50426818856744&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanMutualInformation&lt;/oml:name&gt;
    &lt;oml:value&gt;0.0818434274645147&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanNominalAttDistinctValues&lt;/oml:name&gt;
    &lt;oml:value&gt;3.21875&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanSkewnessOfNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;2.022468153229902&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MeanStdDevOfNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;405.17326983790934&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MinNominalAttDistinctValues&lt;/oml:name&gt;
    &lt;oml:value&gt;2.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;MinorityClassSize&lt;/oml:name&gt;
    &lt;oml:value&gt;0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NBAUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9594224101963532&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NBErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;13.808463251670378&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NBKappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7185564873649677&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NegativePercentage&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7616926503340757&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NoiseToSignalRatio&lt;/oml:name&gt;
    &lt;oml:value&gt;-13.218452122298709&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumAttributes&lt;/oml:name&gt;
    &lt;oml:value&gt;39.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumBinaryAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;19.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumMissingValues&lt;/oml:name&gt;
    &lt;oml:value&gt;0.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumNominalAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;32.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;6.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfClasses&lt;/oml:name&gt;
    &lt;oml:value&gt;6&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfFeatures&lt;/oml:name&gt;
    &lt;oml:value&gt;39&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfInstances&lt;/oml:name&gt;
    &lt;oml:value&gt;898&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfInstancesWithMissingValues&lt;/oml:name&gt;
    &lt;oml:value&gt;0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfMissingValues&lt;/oml:name&gt;
    &lt;oml:value&gt;0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;NumberOfNumericFeatures&lt;/oml:name&gt;
    &lt;oml:value&gt;6&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;PercentageOfBinaryAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;0.48717948717948717&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;PercentageOfMissingValues&lt;/oml:name&gt;
    &lt;oml:value&gt;0.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;PercentageOfNominalAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;0.8205128205128205&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;PercentageOfNumericAtts&lt;/oml:name&gt;
    &lt;oml:value&gt;0.15384615384615385&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;PositivePercentage&lt;/oml:name&gt;
    &lt;oml:value&gt;0.0&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth1AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.7597968469351692&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth1ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;23.2739420935412&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth1Kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.2894251628951225&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth2AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9666861764236521&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth2ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;6.7928730512249444&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth2Kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.832482668142716&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth3AUC&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9924792906738309&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth3ErrRate&lt;/oml:name&gt;
    &lt;oml:value&gt;2.5612472160356345&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;REPTreeDepth3Kappa&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9353873971951361&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;RandomTreeDepth1AUC_K=0&lt;/oml:name&gt;
    &lt;oml:value&gt;0.813070621364688&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;RandomTreeDepth2AUC_K=0&lt;/oml:name&gt;
    &lt;oml:value&gt;0.8907193338317052&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;RandomTreeDepth3AUC_K=0&lt;/oml:name&gt;
    &lt;oml:value&gt;0.9701947883881082&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
  &lt;oml:quality&gt;
    &lt;oml:name&gt;StdvNominalAttDistinctValues&lt;/oml:name&gt;
    &lt;oml:value&gt;2.0593512132112965&lt;/oml:value&gt;
  &lt;/oml:quality&gt;
&lt;/oml:data_qualities&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>360: Please provide data_id</dt><dd>Please provide data_id</dd></dl>
<dl><dt>361: Unknown dataset</dt><dd>Data set description with data_id was not found in the database</dd></dl>
<dl><dt>362: No qualities found</dt><dd>The registered dataset did not contain any calculated qualities</dd></dl>
<dl><dt>363: Dataset not processed yet</dt><dd>The dataset was not processed yet, no qualities are available. Please wait for a few minutes.</dd></dl>
<dl><dt>364: Dataset processed with error</dt><dd>The quality calculator has run into an error while processing the dataset. Please check whether it is a valid supported file. </dd></dl>
<dl><dt>365: Interval start or end illegal</dt><dd>There was a problem with the interval start or end.</dd></dl>
</div>

<!-- [END] Api function description: openml.data.qualities -->  



<!-- [START] Api function description: openml.data.qualities.list --> 


<h3 id=openml_data_qualities_list>openml.data.qualities.list</h3>
<p><i>Lists all data qualities that are used (i.e., are calculated for at least one dataset)</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
None
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:data_qualities_list xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:quality&gt;ClassCount&lt;/oml:quality&gt;
  &lt;oml:quality&gt;ClassEntropy&lt;/oml:quality&gt;
  &lt;oml:quality&gt;DecisionStumpAUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;DecisionStumpErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;DecisionStumpKappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;DefaultAccuracy&lt;/oml:quality&gt;
  &lt;oml:quality&gt;Dimensionality&lt;/oml:quality&gt;
  &lt;oml:quality&gt;EquivalentNumberOfAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;HoeffdingAdwin.changes&lt;/oml:quality&gt;
  &lt;oml:quality&gt;HoeffdingAdwin.warnings&lt;/oml:quality&gt;
  &lt;oml:quality&gt;HoeffdingDDM.changes&lt;/oml:quality&gt;
  &lt;oml:quality&gt;HoeffdingDDM.warnings&lt;/oml:quality&gt;
  &lt;oml:quality&gt;IncompleteInstanceCount&lt;/oml:quality&gt;
  &lt;oml:quality&gt;InstanceCount&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.00001.AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.00001.ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.00001.kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.0001.AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.0001.ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.0001.kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.001.AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.001.ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;J48.001.kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MajorityClassSize&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MaxNominalAttDistinctValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanAttributeEntropy&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanKurtosisOfNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanMeansOfNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanMutualInformation&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanNominalAttDistinctValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanSkewnessOfNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MeanStdDevOfNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MinNominalAttDistinctValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;MinorityClassSize&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NBAUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NBErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NBKappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NaiveBayesAdwin.changes&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NaiveBayesAdwin.warnings&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NaiveBayesDdm.changes&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NaiveBayesDdm.warnings&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NegativePercentage&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NoiseToSignalRatio&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumAttributes&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumBinaryAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumMissingValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumNominalAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfClasses&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfFeatures&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfInstances&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfInstancesWithMissingValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfMissingValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;NumberOfNumericFeatures&lt;/oml:quality&gt;
  &lt;oml:quality&gt;PercentageOfBinaryAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;PercentageOfMissingValues&lt;/oml:quality&gt;
  &lt;oml:quality&gt;PercentageOfNominalAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;PercentageOfNumericAtts&lt;/oml:quality&gt;
  &lt;oml:quality&gt;PositivePercentage&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth1AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth1ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth1Kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth2AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth2ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth2Kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth3AUC&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth3ErrRate&lt;/oml:quality&gt;
  &lt;oml:quality&gt;REPTreeDepth3Kappa&lt;/oml:quality&gt;
  &lt;oml:quality&gt;RandomTreeDepth1AUC_K=0&lt;/oml:quality&gt;
  &lt;oml:quality&gt;RandomTreeDepth2AUC_K=0&lt;/oml:quality&gt;
  &lt;oml:quality&gt;RandomTreeDepth3AUC_K=0&lt;/oml:quality&gt;
  &lt;oml:quality&gt;StdvNominalAttDistinctValues&lt;/oml:quality&gt;
&lt;/oml:data_qualities_list&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
None
</div>

<!-- [END] Api function description: openml.data.qualities.list -->  




<!-- [START] Api function description: openml.task.search --> 


<h3 id=openml_task_search>openml.task.search</h3>
<p><i>Returns the description of a task</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET task_id</code> (Required)</dt><dd>The task id</dd></dl>
</div>
<h5>Schema's</h5>
<div class="bs-callout bs-callout-info">
<h5>openml.task.search</h5>

A task description<br/>
<a type="button" class="btn btn-primary" href="https://github.com/openml/OpenML/blob/master/XML/Schemas/task.xsd">XSD Schema</a>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:task xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:task_id&gt;1&lt;/oml:task_id&gt;
  &lt;oml:task_type&gt;Supervised Classification&lt;/oml:task_type&gt;
  &lt;oml:input name="source_data"&gt;
    &lt;oml:data_set&gt;
      &lt;oml:data_set_id&gt;1&lt;/oml:data_set_id&gt;
      &lt;oml:target_feature&gt;class&lt;/oml:target_feature&gt;
    &lt;/oml:data_set&gt;
  &lt;/oml:input&gt;
  &lt;oml:input name="estimation_procedure"&gt;
    &lt;oml:estimation_procedure&gt;
      &lt;oml:type&gt;crossvalidation&lt;/oml:type&gt;
      &lt;oml:data_splits_url&gt;
http://openml.liacs.nl/api_splits/get/1/Task_1_splits.arff&lt;/oml:data_splits_url&gt;
      &lt;oml:parameter name="number_repeats"&gt;1&lt;/oml:parameter&gt;
      &lt;oml:parameter name="number_folds"&gt;10&lt;/oml:parameter&gt;
      &lt;oml:parameter name="percentage"/&gt;
      &lt;oml:parameter name="stratified_sampling"&gt;true&lt;/oml:parameter&gt;
    &lt;/oml:estimation_procedure&gt;
  &lt;/oml:input&gt;
  &lt;oml:input name="evaluation_measures"&gt;
    &lt;oml:evaluation_measures&gt;
      &lt;oml:evaluation_measure&gt;predictive_accuracy&lt;/oml:evaluation_measure&gt;
    &lt;/oml:evaluation_measures&gt;
  &lt;/oml:input&gt;
  &lt;oml:output name="predictions"&gt;
    &lt;oml:predictions&gt;
      &lt;oml:format&gt;ARFF&lt;/oml:format&gt;
      &lt;oml:feature name="repeat" type="integer"/&gt;
      &lt;oml:feature name="fold" type="integer"/&gt;
      &lt;oml:feature name="row_id" type="integer"/&gt;
      &lt;oml:feature name="confidence.classname" type="numeric"/&gt;
      &lt;oml:feature name="prediction" type="string"/&gt;
    &lt;/oml:predictions&gt;
  &lt;/oml:output&gt;
&lt;/oml:task&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>150: Please provide task_id</dt><dd>Please provide task_id</dd></dl>
<dl><dt>151: Unknown task</dt><dd>The task with this id was not found in the database</dd></dl>
</div>

<!-- [END] Api function description: openml.task.search -->  



<!-- [START] Api function description: openml.task.evaluations --> 


<h3 id=openml_task_evaluations>openml.task.evaluations</h3>
<p><i>Returns the performance of flows on a given task</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET task_id</code> (Required)</dt><dd>the task id</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:error xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:code&gt;301&lt;/oml:code&gt;
  &lt;oml:message&gt;Unknown task&lt;/oml:message&gt;
&lt;/oml:error&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>300: Please provide task_id</dt><dd>Please provide task_id</dd></dl>
<dl><dt>301: Unknown task</dt><dd>The task with this id was not found in the database</dd></dl>
</div>

<!-- [END] Api function description: openml.task.evaluations -->  



<!-- [START] Api function description: openml.task.types --> 


<h3 id=openml_task_types>openml.task.types</h3>
<p><i>Returns a list of all task types</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
None
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:task_types xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:task_type&gt;
    &lt;oml:id&gt;1&lt;/oml:id&gt;
    &lt;oml:name&gt;Supervised Classification&lt;/oml:name&gt;
    &lt;oml:description&gt;In supervised classification, you are given an input dataset in which instances are labeled with a certain class. The goal is to build a model that predicts the class for future unlabeled instances. The model is evaluated using a train-test procedure, e.g. cross-validation.&lt;br&gt;&lt;br&gt;

To make results by different users comparable, you are given the exact train-test folds to be used, and you need to return at least the predictions generated by your model for each of the test instances. OpenML will use these predictions to calculate a range of evaluation measures on the server.&lt;br&gt;&lt;br&gt;

You can also upload your own evaluation measures, provided that the code for doing so is available from the implementation used. For extremely large datasets, it may be infeasible to upload all predictions. In those cases, you need to compute and provide the evaluations yourself.&lt;br&gt;&lt;br&gt;

Optionally, you can upload the model trained on all the input data. There is no restriction on the file format, but please use a well-known format or PMML.&lt;/oml:description&gt;
    &lt;oml:creator&gt;Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl&lt;/oml:creator&gt;
  &lt;/oml:task_type&gt;
  &lt;oml:task_type&gt;
    &lt;oml:id&gt;2&lt;/oml:id&gt;
    &lt;oml:name&gt;Supervised Regression&lt;/oml:name&gt;
    &lt;oml:description&gt;Given a dataset with a numeric target and a set of train/test splits, e.g. generated by a cross-validation procedure, train a model and return the predictions of that model.&lt;/oml:description&gt;
    &lt;oml:creator&gt;Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl&lt;/oml:creator&gt;
  &lt;/oml:task_type&gt;
  &lt;oml:task_type&gt;
    &lt;oml:id&gt;3&lt;/oml:id&gt;
    &lt;oml:name&gt;Learning Curve&lt;/oml:name&gt;
    &lt;oml:description&gt;Given a dataset with a nominal target, various data samples of increasing size are defined. A model is build for each individual data sample; from this a learning curve can be drawn. &lt;/oml:description&gt;
    &lt;oml:creator&gt;Pavel Brazdil, Jan van Rijn, Joaquin Vanschoren&lt;/oml:creator&gt;
  &lt;/oml:task_type&gt;
  &lt;oml:task_type&gt;
    &lt;oml:id&gt;4&lt;/oml:id&gt;
    &lt;oml:name&gt;Supervised Data Stream Classification&lt;/oml:name&gt;
    &lt;oml:description&gt;Given a dataset with a nominal target, various data samples of increasing size are defined. A model is build for each individual data sample; from this a learning curve can be drawn.&lt;/oml:description&gt;
    &lt;oml:creator&gt;Geoffrey Holmes, Bernhard Pfahringer, Jan van Rijn, Joaquin Vanschoren&lt;/oml:creator&gt;
  &lt;/oml:task_type&gt;
&lt;/oml:task_types&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
None
</div>

<!-- [END] Api function description: openml.task.types -->  



<!-- [START] Api function description: openml.task.types.search --> 


<h3 id=openml_task_types_search>openml.task.types.search</h3>
<p><i>returns a definition (template) of a certain task type</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET task_type_id</code> (Required)</dt><dd>The task type id</dd></dl>
</div>
<h5>Schema's</h5>
<div class="bs-callout bs-callout-info">
<h5>openml.task.types.search</h5>

A description of a task type<br/>
<a type="button" class="btn btn-primary" href="https://github.com/openml/OpenML/blob/master/XML/Schemas/task_type.xsd">XSD Schema</a>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:task_type xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:id&gt;1&lt;/oml:id&gt;
  &lt;oml:name&gt;Supervised Classification&lt;/oml:name&gt;
  &lt;oml:description&gt;In supervised classification, you are given an input dataset in which instances are labeled with a certain class. The goal is to build a model that predicts the class for future unlabeled instances. The model is evaluated using a train-test procedure, e.g. cross-validation.&lt;br&gt;&lt;br&gt;

To make results by different users comparable, you are given the exact train-test folds to be used, and you need to return at least the predictions generated by your model for each of the test instances. OpenML will use these predictions to calculate a range of evaluation measures on the server.&lt;br&gt;&lt;br&gt;

You can also upload your own evaluation measures, provided that the code for doing so is available from the implementation used. For extremely large datasets, it may be infeasible to upload all predictions. In those cases, you need to compute and provide the evaluations yourself.&lt;br&gt;&lt;br&gt;

Optionally, you can upload the model trained on all the input data. There is no restriction on the file format, but please use a well-known format or PMML.&lt;/oml:description&gt;
  &lt;oml:creator&gt;Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl&lt;/oml:creator&gt;
  &lt;oml:contributor&gt;Bo Gao&lt;/oml:contributor&gt;
  &lt;oml:contributor&gt; Simon Fischer&lt;/oml:contributor&gt;
  &lt;oml:contributor&gt; Venkatesh Umaashankar&lt;/oml:contributor&gt;
  &lt;oml:contributor&gt; Michael Berthold&lt;/oml:contributor&gt;
  &lt;oml:contributor&gt; Bernd Wiswedel &lt;/oml:contributor&gt;
  &lt;oml:contributor&gt;Patrick Winter&lt;/oml:contributor&gt;
  &lt;oml:date&gt;24-01-2013&lt;/oml:date&gt;
  &lt;oml:input name="source_data"&gt;
    &lt;oml:data_set&gt;
      &lt;oml:data_set_id&gt;[INPUT:source_data]&lt;/oml:data_set_id&gt;
      &lt;oml:target_feature&gt;[INPUT:target_feature]&lt;/oml:target_feature&gt;
    &lt;/oml:data_set&gt;
  &lt;/oml:input&gt;
  &lt;oml:input name="estimation_procedure"&gt;
    &lt;oml:estimation_procedure&gt;
      &lt;oml:type&gt;[LOOKUP:estimation_procedure.type]&lt;/oml:type&gt;
      &lt;oml:data_splits_url&gt;
[CONSTANT:base_url]api_splits/get/[TASK:id]/Task_[TASK:id]_splits.arff&lt;/oml:data_splits_url&gt;
      &lt;oml:parameter name="number_repeats"&gt;[LOOKUP:estimation_procedure.repeats]&lt;/oml:parameter&gt;
      &lt;oml:parameter name="number_folds"&gt;[LOOKUP:estimation_procedure.folds]&lt;/oml:parameter&gt;
      &lt;oml:parameter name="percentage"&gt;[LOOKUP:estimation_procedure.percentage]&lt;/oml:parameter&gt;
      &lt;oml:parameter name="stratified_sampling"&gt;[LOOKUP:estimation_procedure.stratified_sampling]&lt;/oml:parameter&gt;
    &lt;/oml:estimation_procedure&gt;
  &lt;/oml:input&gt;
  &lt;oml:input name="evaluation_measures"&gt;
    &lt;oml:evaluation_measures&gt;
      &lt;oml:evaluation_measure&gt;[INPUT:evaluation_measures]&lt;/oml:evaluation_measure&gt;
    &lt;/oml:evaluation_measures&gt;
  &lt;/oml:input&gt;
  &lt;oml:output name="predictions"&gt;
    &lt;oml:predictions&gt;
      &lt;oml:format&gt;ARFF&lt;/oml:format&gt;
      &lt;oml:feature name="repeat" type="integer"/&gt;
      &lt;oml:feature name="fold" type="integer"/&gt;
      &lt;oml:feature name="row_id" type="integer"/&gt;
      &lt;oml:feature name="confidence.classname" type="numeric"/&gt;
      &lt;oml:feature name="prediction" type="string"/&gt;
    &lt;/oml:predictions&gt;
  &lt;/oml:output&gt;
&lt;/oml:task_type&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>240: Please provide task_type_id</dt><dd>Please provide task_type_id</dd></dl>
<dl><dt>241: Unknown task type</dt><dd>The task type with this id was not found in the database</dd></dl>
</div>

<!-- [END] Api function description: openml.task.types.search -->  




<!-- [START] Api function description: openml.estimationprocedure.get --> 


<h3 id=openml_estimationprocedure_get>openml.estimationprocedure.get</h3>
<p><i>returns the details of an estimation procedure</i></p>

<h5>Arguments</h5>
<div class="bs-callout">
<dl><dt><code>GET estimationprocedure_id</code> (Required)</dt><dd>The id of the estimation procedure</dd></dl>
</div>
<h5>Example Response</h5>
<div class='highlight'>
<pre class='pre-scrollable'>
<code class='html'>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;oml:estimationprocedure xmlns:oml="http://openml.org/openml"&gt;
  &lt;oml:ttid&gt;1&lt;/oml:ttid&gt;
  &lt;oml:name&gt;10-fold Crossvalidation&lt;/oml:name&gt;
  &lt;oml:type&gt;crossvalidation&lt;/oml:type&gt;
  &lt;oml:repeats&gt;1&lt;/oml:repeats&gt;
  &lt;oml:folds&gt;10&lt;/oml:folds&gt;
  &lt;oml:stratified_sampling&gt;true&lt;/oml:stratified_sampling&gt;
&lt;/oml:estimationprocedure&gt;

</code>
</pre>
</div>
<h5>Error codes</h5>
<div class='bs-callout bs-callout-danger'>
<dl><dt>440: Please provide estimationprocedure_id</dt><dd>Please provide estimationprocedure_id</dd></dl>
<dl><dt>441: estimationprocedure_id not valid</dt><dd>Please provide a valid estimationprocedure_id</dd></dl>
</div>

<!-- [END] Api function description: openml.estimationprocedure.get -->  




