# Angular6 + Springboot rest crud + h2 in-memory db


FYI note: 

Used Mac + Java 1.8 + Maven 3.5.4 + npm 6.4.1 to run this app, but it should work on any OS



This repo has:

a - Angular 6 client (Angular6SpringBoot-Client),

b - Springboot rest crud api (SpringBootRestAPIsH2) which uses H2 in memory database


To clone this repo:

git clone https://github.com/alogithub1/Angular6_SpringBoot_H2.git


## Running SpringBootRestAPIsH2:
---------------------------------
a - Go to folder "SpringBootRestAPIsH2"

b - Open terminal

c - Run the below commmands:

    mvn clean install
    
    mvn spring-boot:run
    
d - If all go fine, you should be able to access H2 db console (http://localhost:8080/h2_console) in the browser


## Running Angular6SpringBoot-Client:
---------------------------------
a - Go to folder "Angular6SpringBoot-Client"

b - Open terminal

c - Run the below commmands:

    npm install
    
    npm start
    
d - If all go fine, you can access the URL (http://localhost:4200/) in the browser. From there you can do crud operations.


### Bonus:
If you need to use other databases like MySQL or PostgreSQL: 

a - change the proprerties in "application.properties"

b - include relevant dependency in pom.xml

c - Follow the same above steps


### Reference and credit goes to:
https://grokonez.com/frontend/angular/angular-6/angular-6-h2-in-memory-database-spring-boot-example-spring-data-jpa-restapis-crud-example


## Good Day!  :)
