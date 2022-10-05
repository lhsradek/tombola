FROM tomcat:9.0
LABEL org.opencontainers.image.authors="radek.kadner@gmail.com"
LABEL description="https://hub.docker.com/repository/docker/lhsradek/tombola"
ADD --chown=44 /readme.md /root/bin/readme.txt
ADD --chown=44 /elastic-agent-8.4.2-linux-x86_64.tar.gz /root/bin
# RUN cd ~/bin && tar xzvf elastic-agent-8.4.1-linux-x86_64.tar.gz
# ADD --chown=44 /elastic-agent.yml /root/bin/elastic-agent-8.4.1-linux-x86_64
# RUN cd elastic-agent-8.4.1-linux-x86_64
# RUN ./elastic-agent install
# RUN cd ..
# RUN rm elastic-agent-8.4.1-linux-x86_64.tar.gz
EXPOSE 8080 8443
