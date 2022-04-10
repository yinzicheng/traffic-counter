FROM openjdk:11

ENV APP_DIR=/usr/src/traffic-counter
WORKDIR $APP_DIR

COPY docker/install-sbt.sh .
RUN ./install-sbt.sh

COPY . $APP_DIR
RUN sbt clean test assembly

ENTRYPOINT ["java", "-jar", "target/scala-2.13/trafficcounter.jar"]