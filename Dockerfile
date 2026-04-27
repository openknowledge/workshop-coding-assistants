# tag::client-build[]
#FROM maven:3.9.9-eclipse-temurin-21 AS client-build
#RUN mkdir -p /usr/customer-manager-client
#WORKDIR /usr

# Cache node in a separate layer
#COPY customer-manager-client/pom.xml customer-manager-client/pom.xml
#RUN mvn -f customer-manager-client/pom.xml initialize

# Cache the dependencies in a separate layer
#COPY customer-manager-client/package.json customer-manager-client/package-lock.json customer-manager-client/
#RUN mvn -f customer-manager-client/pom.xml generate-sources

# Build in a separate layer
#COPY customer-manager-client/ /usr/customer-manager-client/
#RUN mvn -f customer-manager-client/pom.xml install -DskipLinting -DskipTests
# end::client-build[]

# tag::server-build[]
FROM maven:3.9.9-eclipse-temurin-21 AS mvn

# Cache maven dependency
#COPY customer-manager-client/pom.xml /usr/customer-manager-client/
#WORKDIR /usr/customer-manager-client
#RUN mvn -Dskip.npm install
COPY customer-manager-server/pom.xml /usr/customer-manager-server/
COPY customer-manager-server/src/main/checkstyle/java.header.plain /usr/customer-manager-server/src/main/checkstyle/java.header.plain
WORKDIR /usr/customer-manager-server
RUN mvn dependency:resolve dependency:resolve-plugins dependency:go-offline spotless:check

# Copy client jar from client stage
RUN mkdir -p /usr/customer-manager-server && mkdir -p /root/.m2/repository/de/openknowledge
#COPY --from=client-build /root/.m2/repository/de/openknowledge/ /root/.m2/repository/de/openknowledge/

# Build server
COPY customer-manager-server/src src
RUN mvn -o -Dcheckstyle.skip -DskipTests clean install
# end::server-build[]

# tag::runtime[]
FROM eclipse-temurin:21-jre

# Copy executable server jar and start
RUN mkdir -p /usr/app
WORKDIR /usr/app
COPY --from=mvn /usr/customer-manager-server/target/*.jar /usr/app/server.jar
ENTRYPOINT ["java", "-jar", "/usr/app/server.jar"]
# end::runtime[]
