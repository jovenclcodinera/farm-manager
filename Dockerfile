FROM eclipse-temurin:17-jdk

MAINTAINER baeldung.com

COPY target/farmManager.jar farmManager.jar

ENTRYPOINT ["java","-jar","/farmManager.jar"]