# Socket Mail Client

This project is a mail client application that utilizes the SMTP (Simple Mail Transfer Protocol) and POP3 (Post Office Protocol 3) protocols.
``Author: 18600355@student.hcmus.edu.vn``
***
# Application Diagram
![alt text](https://github.com/taolong-thao/Socket-Client/blob/master/diagram-application.png?raw=true)
## Prerequisites

- JDK (Java Development Kit) 8 or later
- Maven
- Mysql
- Docker (Optional)
#### SQL Script
``src/main/resources/migrate/v1.sql
``
#### Docker compose file for database environment
``
docker-compose up
``
#### build maven for project
```bash
mvn clean install
```
### Server run
#### Prerequisites
- jdk 1.8
```bash
java -jar test-mail-server-1.0.jar
```
