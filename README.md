# Simple spring micro service application which consists of:

a - Spring boot

b - Youtube or Google api

c) Kafka

d) Swagger

e) Docker


## Steps to run kafka:
---------------------------------
1 - Clone this repo and go to folder "alo-spring-boot-youtube-kafka"

2 - Open terminal

3 - Run the below commmands:

   To run kafka, ensure that docker desktop is running as its used for running kafka
    
    docker-compose up
    

## Steps to run eureka-server:
---------------------------------
1 - Go to folder "eureka-server"

2 - Open terminal
    
3 - Run the below commmands:

    mvn clean install
    
    mvn spring-boot:run


## Steps to run spring-youtube-service:
---------------------------------
1 - Go to folder "spring-youtube-service"

2 - Open terminal
    
3 - Run the below commmands:

    mvn clean install
    
    mvn spring-boot:run
    
    
## Steps to run spring-boot-kafka-sample:
---------------------------------
1 - Go to folder "spring-boot-kafka-sample"

2 - Open terminal
    
3 - Run the below commmands:

    mvn clean install
    
    mvn spring-boot:run
    
    
d - If all go fine, you can access the URL (http://localhost:4200/) in the browser. From there you can do crud operations.


### Bonus:
If you need to use other databases like MySQL or PostgreSQL: 

a - change the proprerties in "application.properties"

b - include relevant dependency in pom.xml

c - Follow the same above steps


## Good Day!  :)
