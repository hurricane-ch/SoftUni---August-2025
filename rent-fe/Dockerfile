FROM nginx:alpine

MAINTAINER Ignat Krushev <ignat.krushev@atechtrade.org>

RUN apk --no-cache add bash curl nodejs npm chromium

ENV CHROME_BIN=/usr/bin/chromium-browser
ARG ANGULAR_PROFILES_ACTIVE=$ANGULAR_PROFILES_ACTIVE
#ARG NGINX_CONFIG_FILE=nginx-$ANGULAR_PROFILES_ACTIVE.conf

RUN set -x \
    && addgroup at-rent \
    && adduser -S -G at-rent -h /home/at-rent -s /bin/false at-rent \
    && mkdir -p /home/at-rent/logs \
    && chown -R at-rent:at-rent /home/at-rent

WORKDIR /home/at-rent
ENV PATH /home/at-rent:$PATH

ADD src /home/at-rent/src
ADD  *.js *.json /home/at-rent/
ADD nginx-custom.conf /etc/nginx/conf.d/default.conf

RUN ["npm", "config", "ls"]
RUN ["npm", "install"]
#RUN ["npm", "run", "eslint"]
#RUN ["npm", "run", "sonar"]
RUN ["npm", "test", "--no-watch --no-sandbox --code-coverage"]
RUN ["npm", "run", "generate-build-info"]
RUN npx ng run at-rent-fe:build:$ANGULAR_PROFILES_ACTIVE

RUN ["rm", "-rf", "/usr/share/nginx/html/at-rent"]
RUN ["cp", "-ar", "/home/at-rent/dist/at-rent-fe/browser", "/usr/share/nginx/html/at-rent"]

EXPOSE 80 443

#ENTRYPOINT nginx -g 'daemon off;'
#CMD ["nginx", "-g", "'daemon off;'"]
#CMD ["ls", "-la", "/home/at-rent"]
