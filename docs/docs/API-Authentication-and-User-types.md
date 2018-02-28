Authentication towards the server goes by means of a so-called api_key (a hexa-decimal string which uniquely identifies a user). Upon interaction with the server, the client passes this api_key to the server, and the server checks the rights of the user. Currently this goes by means of a get or post variable, but in the future we might want to use a header field (because of security). It is recommended to refresh your api_key every month. 

IMPORTANT: Most authentication operations are handled by the ION_Auth library (http://benedmunds.com/ion_auth/). DO NOT alter information directly in the user table, always use the ION_Auth API. 

A user can be part of one or many groups. The following user groups exists:
1. Admin Group: With great power comes great responsibility. Admin users can overrule all security checks on the server, i.e., delete a dataset or run that is not theirs, or even delete a flow that contains runs.
2. Normal Group: Level that is required for read/write interaction with the server. Almost all users are part of this group. 
3. Read-only Group: Level that can be used for read interaction with the server. If a user is part of this group, but not part of 'Normal Group', he is allowed to download content, but can not upload or delete content. 
4. Backend Group: (Work in Progress) Level that has more privileges than 'Normal Group'. Can submit Data Qualities and Evaluations. 

The ION_Auth functions in_group(), add_to_group(), remove_from_group() and get_users_groups() are key towards interaction with these tables. 

Frontend todo: show on user page which groups he belongs to. 
