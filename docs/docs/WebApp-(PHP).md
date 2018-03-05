# Backend

The high-level architecture of the website, including the controllers for different parts of the website (REST API, html, ...) and connections to the database.

### Code
The source code is available in the 'website' repository:
https://github.com/openml/website

### Important files and folders

In this section we go through all important files and folder of the
system.

#### Root directory

The root directory of OpenML contains the following files and folders.

-   **system**: This folder contains all files provided by
    CodeIgniter 2.1.3. The contents of this folder is
    beyond the scope of this document, and not relevant for extending
    OpenML. All the files in this folder are in the same state as they
    were provided by Ellislabs, and none of these files should ever be
    changed.

-   **sparks**: Sparks is a package management system for
    Codeigniter that allows for instant installation of libraries into
    the application. This folder contains two libraries provided by
    third party software developers, oauth1 (based on version 1 the
    oauth protocol) and oauth2 (similarly, based on version 2 of the
    oauth protocol). The exact contents of this folder is beyond the
    scope of this document and not relevant for extending OpenML.

-   **openml\_OS**: All files in this folder are written specifically
    for OpenML. When extending the functionality OpenML, usually one of
    the files in this folder needs to be adjusted. As a thorough
    understanding of the contents of this folder is vital for extending
    OpenML, we will discuss the contents of this folder in
    [[URL Mapping]] in more detail.

-   **index.php**: This is the “bootstrap” file of the system.
    Basically, every page request on OpenML goes through this file (with
    the css, images and javascript files as only exception). It then
    determines which CodeIgniter and OpenML files need to be included.
    This file should not be edited.

-   **.htaccess**: This file (which configures the Apache Rewrite
    Engine) makes sure that all URL requests will be directed to
    `index.php`. Without this file, we would need to include `index.php`
    explicitly in every URL request. This file makes sure that all other
    URL requests without `index.php` embedded in it automatically will
    be transformed to `index.php`. Eg.,
    <http://www.openml.org/frontend/page/home> will be rewritten to
    <http://www.openml.org/index.php/frontend/page/home>. This will be
    explained in detail in [[URL Mapping]].

-   **css**: A folder containing all stylesheets. These are important
    for the layout of OpenML.

-   **data**: A folder containing data files, e.g., datasets,
    implementation files, uploaded content. Please note that this folder
    does not necessarily needs to be present in the root directory. The
    OpenML Base Config file determines the
    exact location of this folder.

-   **downloads**: Another data folder, containing files like the most
    recent database snapshot.

-   **img**: A folder containing all static images shown on the webpage.

-   **js**: A folder containing all used Javascript files and libraries,
    including third party libraries like jQuery and datatables.

-   Various other files, like .gitignore, favicon.ico, etc.

#### openml_OS 

This folder is (in CodeIgniter jargon) the “Application folder”, and
contains all files relevant to OpenML. Within this folder, the following
folders should be present: (And also some other folders, but these are
not used by OpenML)

-   **config**: A folder containing all config files. Most notably, it
    contains the file <span>BASE_CONFIG.php</span>, in which all system
    specific variables are set; the config items within this file
    differs over various installations (e.g., on localhost,
    `openml.org`). Most other config files, like
    <span>database.php</span>, will receive their values from
    <span>BASE\_CONFIG.php</span>. Other important config files are
    <span>autoload.php</span>, determining which CodeIgniter / OpenML
    files will be loaded on any request, <span>openML.php</span>,
    containing config items specific to OpenML, and
    <span>routes.php</span>, which will be explained in
    [[URL Mapping]].

-   **controllers**: In the Model/View/Controller design pattern, all
    user interaction goes through controllers. In a webapplication
    setting this means that every time a URL gets requested, exactly one
    controller gets invoked. The exact dynamics of this will be
    explained in [[URL Mapping]].

-   **core**: A folder that contains CodeIgniter specific files. These
    are not relevant for the understanding of OpenML.

-   **helpers**: This folder contains many convenience functions.
    Wikipedia states: “A convenience function is a non-essential
    subroutine in a programming library or framework which is intended
    to ease commonly performed tasks”. For example the
    <span>file_upload_helper.php</span> contains many functions that
    assist with uploading of files. Please note that a helper function
    must be explicitly loaded in either the autoload config or the files
    that uses its functions.

-   **libraries**: Similar to sparks, this folder contains libraries
    specifically written for CodeIgniter. For example, the library used
    for all user management routines is in this folder.

-   **models**: In the Model/View/Controller design pattern, models
    represent the state of the system. In a webapplication setting, you
    could say that a model is the link to the database. In OpenML,
    almost all tables of the database are represented by a model. Each
    model has general functionality applicable to all models (e.g.,
    retrieve all records, retrieve record with constraints, insert
    record) and functionality specific to that model (e.g., retrieve a
    dataset that has certain data properties). Most models extend an
    (abstract) base class, located in the <span>abstract</span> folder.
    This way, all general functionality is programmed and maintained in
    one place.

-   **third\_party**: Although the name might suggests differently, this
    folder contains all OpenML Java libraries.

-   **views**: In the Model/View/Controller design pattern, the views
    are the way information is presented on the screen. In a
    webapplication setting, a view usually is a block of (PHP generated)
    HTML code. The most notable view is <span>frontend\_main.php</span>,
    which is the template file determining the main look and feel of
    OpenML. Every single page also has its own specific view (which is
    parsed within <span>frontend_main.php</span>). These pages can be
    found (categorized by controller and name) in the <span>pages</span>
    folder. More about this structure is explained in
    [[URL Mapping]].

# Frontend

Architecture and libraries involved in generating the frontend functions.

## Code
https://github.com/openml/website/tree/master/openml_OS/views

### High-level
All pages are generated by first loading *frontend_main.php*. This creates the 'shell' in which the content is loaded. It loads all css and javascript libraries, and contains the html for displaying headers and footers.

### Create new page
The preferred method is creating a new folder into the folder
`<root_directory>/openml_OS/views/pages/frontend`
This page can be requested by
`http://www.openml.org/frontend/page/<folder_name>`
or just
`http://www.openml.org/<folder_name>`
This method is preferred for human readable webpages, where the internal
actions are simple, and the output is complex. We will describe the
files that can be in this folder.

-   **pre.php**: Mandatory file. Will be executed first. Do not make
    this file produce any output! Can be used to pre-render data, or set
    some variables that are used in other files.

-   **body.php**: Highly recommended file. Intended for displaying the
    main content of this file. Will be rendered at the right location
    within the template file (`frontend_main.php`).

-   **javascript.php**: Non-mandatory file. Intended for javascript
    function on which `body.php` relies. Will be rendered within a
    javascript block in the header of the page.

-   **post.php**: Non mandatory file. Will only be executed when a POST
    request is done (e.g., when a HTML form was send using the POST
    protocol). Will be executed after `pre.php`, but before the
    rendering process (and thus, before `body.php` and
    `javascript.php`). Should handle the posted input, e.g., file
    uploads.

It is also recommended to add the newly created folder to the mapping in
the `routes.php` config file. This way it can also be requested by the
shortened version of the URL. (Note that we deliberately avoided to
auto-load all pages into this file using a directory scan, as this makes
the webplatform slow. )

For more information, see [[URL Mapping]]. 
