

![Overview](https://github.com/alogithub1/alo-spring-boot-youtube-kafka/blob/master/Springboot-youtube-kafka-overview.png)


# A simple spring boot micro service application which consists of the below:

a) Spring boot

b) Eureka discovery

c) Youtube / Google api

d) Kafka

e) Swagger

f) Docker

Note: The numbers like 1,2,3,4 listed in the overview diagram is to run it in a sequential order so that all pieces will work together, otherwise it does not add any value.

"Youtube service" and "Kafka service" use "Eureka server" for service registry.

"Kafka service" gets "Youtube service" info from "Eureka server" and then uses it to fetch youtube videos using Spring boot RestTemplate. Also "Kafka service" sends messages to kafka topic (topic name: test-1) and receives messages from kafka topic (topic name: test-2).

-----------------------------------
## Platform:

Used MacOS, Java 8, Maven 3.6.3, Docker desktop to build and run this app. But it should work fine with windows or linux environment as well.

For project specific dependencies like spring boot version, see relevant pom.xml

-----------------------------------
## Steps to run kafka:

1 - Clone this repo and go to folder "alo-spring-boot-youtube-kafka"

2 - To run kafka, ensure that docker desktop is running as its used for running kafka

3 - Open terminal and run below commmand to bring up Kafka.
    
    docker-compose up
    
   If you get any memory issues, you can clean up already running docker instances by running the below commands:
   
    docker stop $(docker ps -a -q)

    docker system prune -a --volumes
    
Note: There are many ways to install and run Kafka and here we are using docker which is one of the easy way to set it up

-----------------------------------
## Steps to run eureka-server:

1 - Go to folder "eureka-server"

2 - Open terminal and run below commmands:

    mvn clean install
    
    mvn spring-boot:run
    
3 - Check http://localhost:8761/ 

-----------------------------------
## Steps to run spring-youtube-service:

This service uses google api to search youtube vidoes and get results. You will need to setup your keys.
Check https://www.youtube.com/watch?v=V4KqpIX6pdI to see how to set it up.

1 - Go to folder "spring-youtube-service"

2 - Open terminal adn run below commmands:

    mvn clean install -DskipTests
    
    mvn spring-boot:run -DskipTests
    
3 - Check http://localhost:9000/swagger-ui.html Expand operations and you can test the API by clicking "Try it out" option in Swagger UI

-----------------------------------
## Steps to run spring-boot-kafka-sample:

1 - Go to folder "spring-boot-kafka-sample"

2 - Open terminal adn run below commmands:

    mvn clean install -DskipTests
    
    mvn spring-boot:run -DskipTests
    
3 - Check http://localhost:8060/swagger-ui.html Expand operations and you can test the API by clicking "Try it out" option in Swagger UI

* It uses Java ExecutorService to initiate thread pools and process messages concurrently

Incase if you want to check the kafka messages, you may use any tool, i used "Kafka Tool" - http://www.kafkatool.com/


If you encounter any issue or error, NO need to get panic and just check the message on your console and you can easily figure out whats going on.


## Hope you enjoyed reading it! Good Day :)
