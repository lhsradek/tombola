FROM tomcat:9.0
LABEL org.opencontainers.image.authors="radek.kadner@gmail.com"
LABEL description="https://hub.docker.com/repository/docker/lhsradek/tombola"
ADD --chown=44 /readme.md /root/bin/readme.txt
ADD --chown=44 /elastic-agent-*-amd64.deb /root/bin
RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install apt-utils
# RUN apt install /root/bin/elastic-agent-*-amd64.deb
# ADD --chown=44 /elastic-agent.yml /root/bin/elastic-agent-8.4.1-linux-x86_64
# RUN rm /root/bin/elastic-agent-*-amd64.deb
RUN apt-get -y install build-essential
EXPOSE 8080 8443
