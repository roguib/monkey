# from /Users/roguib/repos/monkey run  docker build -t playground-ws -f microservices/playground-ws/Dockerfile .

# 1st stage, build the app
FROM container-registry.oracle.com/java/openjdk:21 as build

ARG BASE_URL_PROTOCOL
ARG BASE_URL_DOMAIN
ARG BASE_URL_PORT
ARG BASE_URL_PATH
ARG $WEBSOCKET_PROTOCOL

RUN echo "BASE_URL_PROTOCOL variable is $BASE_URL_PROTOCOL"
RUN echo "BASE_URL_DOMAIN variable is $BASE_URL_DOMAIN"
RUN echo "BASE_URL_PORT variable is $BASE_URL_PORT"
RUN echo "BASE_URL_PATH variable is $BASE_URL_PATH"
RUN echo "WEBSOCKET_PROTOCOL variable is $WEBSOCKET_PROTOCOL"

# Install maven
WORKDIR /usr/share
RUN set -x && \
    curl -O https://archive.apache.org/dist/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz && \
    tar -xvf apache-maven-*-bin.tar.gz  && \
    rm apache-maven-*-bin.tar.gz && \
    mv apache-maven-* maven && \
    ln -s /usr/share/maven/bin/mvn /bin/

WORKDIR /web
ADD web/playground playground

WORKDIR /helidon
ADD microservices/playground-ws/src src

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD microservices/playground-ws/pom.xml .
RUN mvn -Dbase.url.protocol="$BASE_URL_PROTOCOL" -Dbase.url.domain="$BASE_URL_DOMAIN" -Dbase.url.port="$BASE_URL_PORT" \
    -Dbase.url.path="$BASE_URL_PATH" -Dwebsocket.protocol="$WEBSOCKET_PROTOCOL" package -Prelease -Dmaven.test.skip

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD microservices/playground-ws/src src
RUN mvn -Dbase.url.protocol="$BASE_URL_PROTOCOL" -Dbase.url.domain="$BASE_URL_DOMAIN" -Dbase.url.port="$BASE_URL_PORT" \
    -Dbase.url.path="$BASE_URL_PATH" -Dwebsocket.protocol="$WEBSOCKET_PROTOCOL" package -Prelease -Dmaven.test.skip

RUN echo "done!"

# 2nd stage, build the runtime image
FROM container-registry.oracle.com/java/openjdk:21
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/playground-ws.jar ./
COPY --from=build /helidon/target/libs ./libs

CMD ["java", "-jar", "playground-ws.jar"]

EXPOSE 7001

# run the image docker run -d -p 7001:7001 playground-ws:latest
# get logs docker logs <id>
# get logs docker real time logs -f <id>
# lists containers, even stoped ones docker ps -a