FROM public.ecr.aws/bitnami/node:18 as webBuild
WORKDIR /tmp
COPY ./frontend ./frontend
WORKDIR /tmp/frontend
RUN yarn install
RUN yarn build

FROM public.ecr.aws/bitnami/gradle:8 as serverBuild
WORKDIR /tmp
COPY ./backend ./backend
COPY --from=webBuild /tmp/frontend/dist /tmp/backend/src/main/resources/static
WORKDIR /tmp/backend

RUN ./gradlew build -x test

FROM public.ecr.aws/docker/library/openjdk:17-jdk
COPY --from=serverBuild /tmp/backend/build/libs/backend-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
CMD java -jar app.jar