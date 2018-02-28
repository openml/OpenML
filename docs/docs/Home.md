### OpenML Components
To make development easier, OpenML has been subdivided into several subprojects with their own repositories, wikis, and issue trackers:
* Website itself and API services: https://github.com/openml/website
* Java library for interfacing with the OpenML API: https://github.com/openml/java
* R package for interfacing with the OpenML API: https://github.com/openml/r
* Python module for interfacing with the OpenML API (stub): https://github.com/openml/python
* WEKA plugin: https://github.com/openml/weka
* RapidMiner plugin: https://github.com/openml/rapidminer
* KNIME plugin: https://github.com/openml/knime

### Suggestions for further integrations
* We need more data. Other people made efforts for hosting and selecting ML data already. 
[[Data-Repositories]] lists them. List must be extended and we need to check how much we already have integrated.

### Local installation of OpenML
Developers who are working on new features may need a [[Local Installation]] for testing purposes. 

### Backend development
The website is built using a PHP/Java backend and a PHP/javascript frontend. 

An overview:
* [[Web APP|WebApp-(PHP)]]: The high-level architecture of the website, including the controllers for different parts of the website (REST API, html, ...) and connections to the database.
* [[Helper functions]]: Mostly written in Java, these functions build search indexes, compute dataset characteristics, generate tasks and evaluate the results of certain tasks.
* [[URL Mapping]] A guide to the basics how a URL maps to internal files. 