FROM ubuntu
ENV TZ="Europe/Warsaw"
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get install -y \
    apt-utils \
    ssh \
    openssh-server \
    tcpdump \
    ffmpeg  \
    python2.7 \
    nginx \
    nano \
    mc \
    pciutils \
    iputils-ping \
    iptables \
    sudo \
    iproute2 \
    traceroute \
    nload \
    tzdata \
    net-tools 


RUN echo "root:root" | chpasswd
RUN echo "PermitRootLogin yes" >> /etc/ssh/sshd_config

RUN mkdir /var/run/sshd

COPY ./impairment2.sh /impairment.sh

EXPOSE 22 80

USER root
RUN chmod 777 /impairment.sh

RUN apt-get install -y openjdk-11-jre

RUN mkdir /root/aui

COPY ./aui-0.0.9-SNAPSHOT.jar /aui.jar 

ARG CONFIG_FILE
ARG PORT
ARG BANDWIDTH
ARG BUFFER_SIZE
ARG DELAY
ARG LOSS
ARG NODE_NUM
ARG KNOWN_NEIGHBOURS
ARG BASE_REDIRECT_URL

ENV CONFIG_FILE ${CONFIG_FILE}
ENV PORT ${PORT}
ENV BANDWIDTH ${BANDWIDTH}
ENV BUFFER_SIZE ${BUFFER_SIZE}
ENV DELAY ${DELAY}
ENV LOSS ${LOSS}
ENV NODE_NUM ${NODE_NUM}
ENV KNOWN_NEIGHBOURS ${KNOWN_NEIGHBOURS}
ENV BASE_REDIRECT_URL ${BASE_REDIRECT_URL}

EXPOSE ${PORT}

COPY config.txt /root/aui/config.txt

ENTRYPOINT java -jar aui.jar \
    --server.port=${PORT} \ 
    --pw.edu.aui.impairment.bandwidth=${BANDWIDTH} \
    --pw.edu.aui.impairment.buffer-size=${BUFFER_SIZE} \
    --pw.edu.aui.impairment.delay=${DELAY} \
    --pw.edu.aui.impairment.loss=${LOSS} \
    --pw.edu.aui.node-number=${NODE_NUM} \
    --pw.edu.aui.neighbours-known=${KNOWN_NEIGHBOURS}\
    --pw.edu.aui.redirect.base=${BASE_REDIRECT_URL}