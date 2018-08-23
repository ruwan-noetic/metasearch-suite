# Meatasearch Suite API


A Tomcat + MySQL Setup for local development of m2s API.

Before you begin


Make sure you have installed docker and docker-compose for your OS.
Clone this repository and go to your working directory. 

git clone https://github.com/noeticlabs/metasearch-suite-api.git
switch to the latest development bracnh

go to the project root directory and do a maven install.

mvn install

to Build using docker (on the terminal)

docker-compose down

docker-compose build

to Run: 

docker-compose up -d m2s_mysql    

docker-compose up m2s_tomcat

try the following uri on the browser:    http://localhost:8080/m2s-api/swagger-ui.html#/

to stop tomcat (on the terminal)

ctrl + c  

to stop mysql (on the terminal)

docker-compose stop  gha_mysql Debug M2S with Docker

-- Debug saas project

In Eclipse/STS, go to > debug configurations 

select Remote Java Application 

give an appropriate name. ex: M2S_SAAS_DEBUG 

select project by browsing. i.e internal

and give 8787 as debugging port

then hit apply button.

to debug the api project you can create another debug profile with a different name as above, and give m2s-external-api as the project.
