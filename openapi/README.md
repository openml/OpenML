# OpenAPI REST API Documentation

In order to facilitate interfacing with OpenML's REST API, it is desribed by means of OpenAPI.
However, in order to ensure that the specification is up-to-date with the current code base and the documentation does not need to be duplicated (once in the OpenAPI specification file and once in-line in the code base), in-line annotations are leveraged to facilitate the maintenance of the documentation. Via the composer package [zircote/swagger-php](https://zircote.github.io/swagger-php/), the annotations are compiled into an OpenAPI specification file (either yaml or json). Instructions on how to freshly generate the OpenAPI specification from the current code base as well as how to create/edit documentation for the REST API is given in the following.

## Getting Started

Prerequisite: In order to generate the up-to-date OpenAPI specification according to the current code base, you need to have the php-cli package installed on your device.

For compiling in-line documentation into an OpenAPI specification file, the zircote/swagger-php package is used. This tool will allow you to search the entire code base and distill an OpenAPI specification from all found in-line comments.

* Open a terminal and change into the `openapi/` directory.
* Install the zircote/swagger-php package by running the command `php composer.phar install`

Now you have everything in place to start compiling an OpenAPI specification file from the code base.

## Generate OpenML's Current OpenAPI Specification

For generating OpenML's up-to-date OpenAPI specification, depending on your operating system, you may choose one of the scripts contained in the `openapi/` directory.
For all operating systems, there are two flavors of the script: one producing a yaml file and one a json file. The contents of both files will be the same except for the format itself.

If you are a Linux of MacOS user, you may use the .sh-Scripts:

```
./generate_json_api.sh # This will compile the OpenAPI specification in JSON format.
./generate_yaml_api.sh # This will compile the OpenAPI specification in YAML format.
```

As a Windows user, you may want to use the following batch scripts:

```
./generate_json_api.bat # This will compile the OpenAPI specification in JSON format.
./generate_yaml_api.bat # This will compile the OpenAPI specification in YAML format.
```

## Instructions for the In-Line Documentation
The inline annotations for PHP work similar as in Java, following the schema @<annotation name>(<annotation content>). In case of the OpenAPI annotations the most important components such as the annotations for get, post, or put methods have distinct annotations. However, all OpenAPI specific annotations are namespaced with an `OA\`. Thus the annotation for get, post, and put correspond to `@OA\Get(...)`, `@OA\Post(...)`, and `@OA\Put(...)` respectively. The information about the path, tags, description, parameters, etc. is then enclosed within the parantheses. While keys such as path, tags, description, and so on which only take a simple value (instead of a complex object) are directly referenced, parameters for instance are specified with the help of distinct annotations again, i.e. `@OA\Parameter(...)`.

A more comprehensive description of what kind of annotations are supported by the OpenAPI specification and more details on the PHP annotations please refer to the official [OpenAPI specification website](https://swagger.io/specification/)  or the [zircote/swagger-php project page](https://zircote.github.io/swagger-php/).

### Important Notice for Arrays

Pay attention when specifying arrays within the PHP-Annotations. Brackets ([]) are not supported by the zircote/swagger-php compiler and need to be replaced by curly brackets ({}). It may seem to be a little awkward in the beginning, but eventually it works out.

Example: Instead of specifying an object like
```
{
	"a": [ 0, 1, 2 ]
}
```
write
```
{
	"a": { 0, 1, 2 }
}
```
instead.


### Unused Schemas

Schemas describe return types of the REST API. Since they can be nested into each other it is not always easy to see which of them are no longer needed. In order to identify orphan schemas, you can run the `unusedSchemas.py` Python script. The script will load the OpenAPI specification located in the current directory and check which schemas are indeed used and which are obsolete. Finally, if there are indeed orphan schemas, it will give out a list of schema names which are not referenced by any other schema or REST method.