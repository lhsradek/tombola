FROM tomcat:9
LABEL org.opencontainers.image.authors="radek.kadner@gmail.com"
LABEL description="https://hub.docker.com/repository/docker/lhsradek/tombola"
ADD --chown=44 /readme.md /root/bin/readme.txt
RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install apt-utils
RUN apt-get -y install libfakeroot libconfig-yaml-perl perl-modules perl-depends
RUN apt-get -y install build-essential
EXPOSE 8080 8443
