# Important resources API
API docs: www.openml.org/api_docs

Controller: https://github.com/openml/website/blob/master/openml_OS/controllers/Api_new.php

Models: https://github.com/openml/website/tree/master/openml_OS/models/api/v1

Templates: https://github.com/openml/website/tree/master/openml_OS/views/pages/api_new/v1

# Golden Rules for Development
1. **Code Maintainability before anything else**. The code has to be understandable, and if not conflicting with that, short. Avoid code duplications as much as possible.
2. The API controller is the only entity giving access to the API models. Therefore, the responsibility for API access can be handled by the controller
3. Read-Only operations are of the type GET. Operations that make changes in the database are of type POST or DELETE. Important, because this is the way the controller determines to allow users with a given set of privileges to access functions. 
4. Try to avoid direct queries to the database. Instead, use the respective models functions: 'get()', 'getWhere()', 'getById()', insert(), etc (Please make yourself familiar with the basic model: [read-only](https://github.com/openml/website/blob/master/openml_OS/models/abstract/Database_read.php) and [write](https://github.com/openml/website/blob/master/openml_OS/models/abstract/Database_write.php))
5. No external program/script execution during API calls (with one exception: data split generation). This makes the API unnecessarily slow, hard to debug and vulnerable to crashes. If necessary, make a cronjob that executes the program / script
