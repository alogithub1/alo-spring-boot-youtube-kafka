# Simple spring micro service application which consists of:
a - Spring boot
b - Youtube or Google api
c) Kafka
d) Swagger
e) Docker


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
