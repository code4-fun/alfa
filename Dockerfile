FROM maslick/minimalka:jdk11
EXPOSE 8080
RUN mkdir ./app
COPY ./alfa-0.0.1-SNAPSHOT.jar ./app
CMD java -jar ./app/alfa-0.0.1-SNAPSHOT.jar