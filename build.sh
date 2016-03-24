#!/bin/bash -e
# A script to build the sentiment-classification jar. Then add it to maven local repo and tomcat classpath

mvn clean package

# In case you need to clean the local repository (if not updating well)
#mvn dependency:purge-local-repository
mvn install:install-file -Dfile=/home/emonet/java_workspace/SentimentClassification/target/sentiment-classification.jar -DgroupId=org.advanse -DartifactId=SentimentClassification -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
        
cp target/SentimentClassification.jar /opt/tomcat/lib/SentimentClassification.jar
