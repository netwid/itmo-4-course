FROM openjdk:8

RUN cd /opt \
&& wget https://s3.amazonaws.com/new-mule-artifacts/mule-ee-distribution-standalone-4.4.0.zip \
&& unzip *.zip \
&& ln -s /opt/mule-enterprise-standalone-4.4.0 /opt/mule \
&& rm /opt/mule-ee-distribution-standalone-4.4.0.zip

# Define environment variables.
ENV MULE_HOME /opt/mule

# Define mount points.
VOLUME ["/opt/mule/logs", "/opt/mule/apps", "/opt/mule/domains"]

# Define working directory.
WORKDIR /opt/mule

ADD lab4.jar /opt/mule/apps/

CMD ["/opt/mule/bin/mule"]