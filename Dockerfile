# For reference only

FROM websphere-liberty
ADD target/LocalCartKafkaProducer.war /opt/ibm/wlp/usr/servers/defaultServer/dropins/
ENV LICENSE accept
EXPOSE 9080

## Running the container locally
# mvn clean install
# docker build -t LocalCartKafkaProducer:latest .
# docker run -d --name myjavacontainer LocalCartKafkaProducer
# docker run -p 9080:9080 --name myjavacontainer LocalCartKafkaProducer
# Visit http://localhost:9080/LocalCartKafkaProducer/

## Push container to Bluemix
# Install cli and dependencies: https://console.ng.bluemix.net/docs/containers/container_cli_cfic_install.html#container_cli_cfic_install
# docker tag LocalCartKafkaProducer:latest registry.ng.bluemix.net/<my_namespace>/LocalCartKafkaProducer:latest
# docker push registry.ng.bluemix.net/<my_namespace>/LocalCartKafkaProducer:latest
# bx ic images # Verify new image
