FROM amazoncorretto:latest

# Install required tools and Apache
RUN yum -y update && \
    yum -y groupinstall "Development Tools" && \
    yum -y install gcc gcc-c++ make cmake curl httpd tar && \
    yum clean all

WORKDIR /app

# Add a simple index.html
RUN echo "Hello, World!" > /var/www/html/index.html

# Configure Apache
RUN echo "ServerName localhost" >> /etc/httpd/conf/httpd.conf && \
    sed -i 's/^Listen 80$/Listen 8080/' /etc/httpd/conf/httpd.conf

# Validate Apache configuration
RUN apachectl configtest

EXPOSE 8080

CMD ["/usr/sbin/httpd", "-D", "FOREGROUND"]
