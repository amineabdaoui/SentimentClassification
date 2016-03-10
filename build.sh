#!/bin/bash -e
# A script to build the DEFT jar. Then add it to maven local repo and tomcat classpath

mvn clean package

mvn install:install-file -Dfile=/home/emonet/DEFT/target/deft.jar -DgroupId=org.advanse -DartifactId=deft -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
        
cp target/deft.jar /opt/tomcat/lib/deft.jar
