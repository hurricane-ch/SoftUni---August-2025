FROM alpine:edge

MAINTAINER Ignat Krushev <ignat.krushev@atechtrade.org>

RUN apk --no-cache add bash openjdk17 curl ttf-dejavu

RUN set -x \
    && addgroup rent \
    && adduser -S -G rent -h /home/rent -s /bin/false rent \
    && mkdir -p /home/rent/logs \
    && mkdir -p /home/rent/storage \
    && chown -R rent:rent /home/rent

WORKDIR /home/rent
ENV PATH /home/rent:$PATH

ADD rent-resource/target /home/rent/rent-resource/target
ADD rent-core/src/main/resources /home/rent/rent-core/src/main/resources

#ADD rent-resource /home/rent/rent-resource
#ADD rent-core /home/rent/rent-core
#ADD pom.xml /home/rent/pom.xml
#ADD .mvn /home/rent/.mvn
#ADD mvnw /home/rent/mvnw

#RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "-ea", "rent-resource/target/rent-be.jar"]

#ENV PROFILES=postgres,logging-production
#CMD ["-jar", "-ea", "-Dspring.profiles.active=${PROFILES}", "rent-resource/target/rent-be.jar"]

#CMD ["ls", "-la", "/home/rent"]
