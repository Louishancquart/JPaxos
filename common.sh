FLAGS="-ea -server"
CLASSPATH="-cp bin:lib/logback-classic-1.0.13.jar:lib/logback-core-1.0.13.jar:lib/slf4j-api-1.7.5.jar:lib/ttorrent-core-1.6-SNAPSHOT.jar:lib/commons-io-2.5.jar:lib/simple-http-6.0.1.jar"
LOGGING="-Dlogback.configurationFile=logback.xml"
OPTS="${FLAGS} ${LOGGING} ${CLASSPATH}"
