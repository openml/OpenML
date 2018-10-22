URL to Page Mapping
===================

Most pages in OpenML are represented by a folder in
<root_directory>/openml_OS/views/pages/frontend
The contents of this folder will be parsed in the template
`frontend_main.php` template, as described in [[backend]]. In
this section we explain the way an URL is mapped to a certain OpenML
page.

URL Anatomy
-----------

By default, CodeIgniter (and OpenML) accepts a URL in the following
form:
`http://www.openml.org/index.php/<controller>/<function>/<p1>/<pN>/<free>`
The various parts in the URL are divided by slashes. Every URL starts
with the protocol and server name (in the case of OpenML this is
`http://www.openml.org/`). This is followed by the bootstrap file, which
is always the same, i.e., `index.php`. The next part indicates the
controller that needs to be invoked; typically this is `frontend`,
`rest_api` or `data`, but it can be any file from the `openml_OS` folder
`controllers`. Note that the suffix `.php` should not be included in the
URL.

The next part indicates which function of the controller should be
invoked. This should be a existing, public function from the controller
that is indicated in the controller part. These functions might have one
or more parameters that need to be set. This is the following part of
the URL (indicated by `p1` and `pN`). The parameters can be followed by
anything in free format. Typically, this free format is used to pass on
additional parameters in `name` - `value` format, or just a way of
adding a human readable string to the URL for SEO purposes.

For example, the following URL
`http://www.openml.org/index.php/frontend/page/home` invokes
the function `page` from the `frontend` controller and sets the only
parameter of this function, `$indicator`, to value `home`. The function
`page` loads the content of the specified folder (`$indicator`) into the
main template. In this sense, the function `page` can be seen as some
sort of specialized page loader.

URL Shortening
--------------

Since it is good practice to have URL’s as short as possible, we have
introduced some logic that shortens the URL’s. Most importantly, the URL
part that invokes `index.php` can be removed at no cost, since this file
is **always** invoked. For this, we use Apache’s rewrite engine. Rules
for rewriting URL’s can be found in the `.htaccess` file, but is
suffices to say that any URL in the following format
`http://www.openml.org/index.php/<controller>/<function>/<params>`
can due to the rewrite engine also be requested with
`http://www.openml.org/<controller>/<function>/<params>`

Furthermore, since most of the pages are invoked by the function `page`
of the `frontend` controller (hence, they come with the suffix
`frontend/page/page_name`) we also created a mapping that maps URL’s in
the following form
`http://www.openml.org/<page_name>` 
to
`http://www.openml.org/frontend/page/<page_name>`
Note that Apache’s rewrite engine will also add `index.php` to this. The
exact mapping can be found in `routes.php` config file.

Additional Mappings
-------------------

Additionally, a mapping is created from the following type of URL:
`http://www.openml.org/api/<any_query_string>`
to
`http://www.openml.org/rest_api/<any_query_string>`
This was done for backwards compatibility. Many plugins make calls to
the not-existing `api` controller, which are automatically redirected to
the `rest_api` controller.

Exceptions
----------

It is important to note that not all pages do have a specific page
folder. The page folders are a good way of structuring complex GUI’s
that need to be presented to the user, but in cases where the internal
state changes are more important than the GUI’s, it might be preferable
to make the controller function print the output directly. This happens
for example in the functions of `rest_api.php` and `free_query.php`
(although the former still has some files in the views folder that it
refers to).