FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache apache2 apache2-utils bash build-base cmake curl gradle maven \
    gcc g++ make libc-dev cunit cunit-dev && \
    mkdir -p /run/apache2

WORKDIR /app

RUN echo "Hello, World!" > /var/www/localhost/htdocs/index.html && \
    echo "ServerName localhost" >> /etc/apache2/httpd.conf && \
    sed -i 's/^Listen 80$/Listen 8080/' /etc/apache2/httpd.conf && \
    /usr/sbin/httpd -t

EXPOSE 8080

CMD ["/usr/sbin/httpd", "-D", "FOREGROUND"]
