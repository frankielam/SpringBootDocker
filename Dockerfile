FROM openjdk
MAINTAINER frankie <frankie.lqf@gmail.com>

ARG JAR_FILE
WORKDIR /app
COPY target/${JAR_FILE} ./app.jar
EXPOSE 8080
CMD ["java", "-jar",  "app.jar"]
