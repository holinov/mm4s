FROM debian:jessie

ADD . /mm4s
RUN chmod +x -R /mm4s/bin
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl jq && \
    rm -rf /tmp/*
