# syntax=docker/dockerfile:1.7

# tag::client-build[]
FROM maven:3.9.9-eclipse-temurin-21 AS client-build
RUN mkdir -p /usr/customer-management-client
WORKDIR /usr

# Cache node in a separate layer
COPY customer-management-client/pom.xml customer-management-client/pom.xml
RUN --mount=type=cache,target=/root/.m2/repository,id=mvn-cache,sharing=locked \
    mvn -f customer-management-client/pom.xml initialize

# Cache the npm dependencies (npm cache mount; node_modules itself remains in layer)
COPY customer-management-client/package.json customer-management-client/package-lock.json customer-management-client/
RUN --mount=type=cache,target=/root/.m2/repository,id=mvn-cache,sharing=locked \
    --mount=type=cache,target=/root/.npm,id=npm-cache,sharing=locked \
    mvn -f customer-management-client/pom.xml generate-sources

# Build (only build-relevant sources)
COPY customer-management-client/src            /usr/customer-management-client/src
COPY customer-management-client/public         /usr/customer-management-client/public
COPY customer-management-client/index.html     /usr/customer-management-client/index.html
COPY customer-management-client/vite.config.ts /usr/customer-management-client/vite.config.ts
COPY customer-management-client/tsconfig.app.json  /usr/customer-management-client/tsconfig.app.json
COPY customer-management-client/tsconfig.node.json /usr/customer-management-client/tsconfig.node.json
COPY customer-management-client/.prettierrc     /usr/customer-management-client/.prettierrc
COPY customer-management-client/.prettierignore /usr/customer-management-client/.prettierignore
# Override tsconfig.json: drop the test project reference -- tests don't run in the container,
# and we don't copy `tests/` to keep the build layer cacheable when test files change.
RUN echo '{"files":[],"references":[{"path":"./tsconfig.app.json"},{"path":"./tsconfig.node.json"}]}' \
    > /usr/customer-management-client/tsconfig.json
# Cache mount on /usr/customer-management-client/target so the install output (client jar) lands
# in the layer AND the maven cache mount holds the downloaded deps. We mount the maven
# repo cache so re-downloads are avoided when pom.xml changes invalidate the install layer.
RUN --mount=type=cache,target=/root/.m2/repository,id=mvn-cache,sharing=locked \
    --mount=type=cache,target=/root/.npm,id=npm-cache,sharing=locked \
    mvn -f customer-management-client/pom.xml install -DskipLinting -DskipTests \
 && mkdir -p /export/.m2/repository/de/openknowledge \
 && cp -r /root/.m2/repository/de/openknowledge/. /export/.m2/repository/de/openknowledge/
# end::client-build[]

# tag::server-build[]
FROM maven:3.9.9-eclipse-temurin-21 AS mvn

ARG CLIENT_GROUP_PATH=de/openknowledge
ARG CLIENT_ARTIFACT=customer-management-client
ARG CLIENT_VERSION=0.0.1-SNAPSHOT

# Stub jar + dependency resolution run together in ONE RUN: a cache mount on
# /root/.m2/repository overlays anything the layer placed there, so the stub jar must
# be created inside the same shell context as the mvn invocation that consumes it.
# This lets BuildKit start the server stage in parallel with client-build (the COPY
# only depends on customer-management-client/pom.xml, not on the client-build output).
COPY customer-management-client/pom.xml /tmp/client-pom.xml
COPY customer-management-server/pom.xml /usr/customer-management-server/
COPY customer-management-server/src/main/checkstyle/java.header.plain /usr/customer-management-server/src/main/checkstyle/java.header.plain
WORKDIR /usr/customer-management-server
RUN --mount=type=cache,target=/root/.m2/repository,id=mvn-cache,sharing=locked \
    CLIENT_REPO_PATH=/root/.m2/repository/${CLIENT_GROUP_PATH}/${CLIENT_ARTIFACT}/${CLIENT_VERSION} \
 && mkdir -p ${CLIENT_REPO_PATH} \
 && cp /tmp/client-pom.xml ${CLIENT_REPO_PATH}/${CLIENT_ARTIFACT}-${CLIENT_VERSION}.pom \
 && (cd /tmp && jar cf ${CLIENT_REPO_PATH}/${CLIENT_ARTIFACT}-${CLIENT_VERSION}.jar client-pom.xml) \
 && mvn dependency:resolve dependency:resolve-plugins dependency:go-offline spotless:check

# Stage the real client jar to a layer location -- a subsequent cache mount on
# /root/.m2/repository would otherwise hide a direct COPY into the maven repo.
COPY --from=client-build /export/.m2/repository/${CLIENT_GROUP_PATH}/ /tmp/client-repo/${CLIENT_GROUP_PATH}/

# Build server (only main sources -- tests are skipped anyway). Inside the cache mount,
# copy the real client jar over the stub jar, then run the offline install.
COPY customer-management-server/src/main src/main
RUN --mount=type=cache,target=/root/.m2/repository,id=mvn-cache,sharing=locked \
    mkdir -p /root/.m2/repository/${CLIENT_GROUP_PATH} \
 && cp -r /tmp/client-repo/${CLIENT_GROUP_PATH}/. /root/.m2/repository/${CLIENT_GROUP_PATH}/ \
 && mvn -o -Dcheckstyle.skip -DskipTests clean install
# end::server-build[]

# tag::runtime[]
FROM eclipse-temurin:21-jre

# Copy executable server jar and start
RUN mkdir -p /usr/app
WORKDIR /usr/app
COPY --from=mvn /usr/customer-management-server/target/*.jar /usr/app/server.jar
ENTRYPOINT ["java", "-jar", "/usr/app/server.jar"]
# end::runtime[]
