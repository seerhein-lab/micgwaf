micgwaf
=======

MInimal Code-Generating Web Application Framework

This is work in progress to explore whether a code generating approach is workable for web applications.
micgwaf is not (yet) ready for production use and APIs will change all the time without notice.

See the [docs](src/docs/index.md) for more information.

### Build status

[![Build Status](https://buildhive.cloudbees.com/job/seerhein-lab/job/micgwaf/badge/icon)](https://buildhive.cloudbees.com/job/seerhein-lab/job/micgwaf/)

### Components

Micgwaf consists of different components

- micgwaf-core: The core implementation
- micgwaf-maven-plugin: Plugin to build micgwaf projects with eclipse
- micgwaf-demo: Demo project for micgwaf

### Checkout the project into eclipse

The following procedure can be followed to checkout this project into eclipse

- use the git repository exploring perspective and clone the micgwaf repository from github
- right-click the cloned repo and select import projects... then select "import as general project"
- change into the project root dir and run "mvn eclipse:eclipse"
- remove the .project file in the project root dir
- select File->Import->Existing Projects into Workspace and select the project root dir as root dir
- import the three selected subprojects
- click File->New->Project and import the project root dir as a project

Now you can use the subprojects for running and compiling and the main project 
for any git operation you may want to execute.