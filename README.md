# A simple spring micro service application which consists of the below:

a) Spring boot

b) Eureka discovery

c) Youtube or Google api

d) Kafka

e) Swagger

f) Docker


## Platform:
---------------------------------
Used MacOS, Java 8, Maven 3.6.3 to build and run this app. But it should work fine with windows or linux environment as well.

For other project specific ones, check relevant pom.xml

## Steps to run kafka:
---------------------------------
1 - Clone this repo and go to folder "alo-spring-boot-youtube-kafka"

2 - To run kafka, ensure that docker desktop is running as its used for running kafka

3 - Open terminal and run below commmand:
    
    docker-compose up
    
   If you get any memory issues, you can clean up already running docker instances by running the below:
   
    docker stop $(docker ps -a -q)

    docker system prune -a --volumes
    

## Steps to run eureka-server:
---------------------------------
1 - Go to folder "eureka-server"

2 - Open terminal and run below commmands:

    mvn clean install
    
    mvn spring-boot:run
    
3 - Check http://localhost:8761/


## Steps to run spring-youtube-service:
---------------------------------
1 - Go to folder "spring-youtube-service"

2 - Open terminal adn run below commmands:

    mvn clean install -DskipTests
    
    mvn spring-boot:run -DskipTests
    
    
## Steps to run spring-boot-kafka-sample:
---------------------------------
1 - Go to folder "spring-boot-kafka-sample"

2 - Open terminal adn run below commmands:

    mvn clean install -DskipTests
    
    mvn spring-boot:run -DskipTests
    
    
d - If all go fine, you can access the URL (http://localhost:4200/) in the browser. From there you can do crud operations.


### Bonus:



## Good Day!  :)
