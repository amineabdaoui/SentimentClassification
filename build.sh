#!/bin/bash -e
# A script to build the sentiment-classification jar. Then add it to maven local repo and tomcat classpath

mvn clean package

# In case you need to clean the local repository (if not updating well)
#mvn dependency:purge-local-repository
mvn install:install-file -Dfile=target/sentiment-classification.jar -DgroupId=fr.advanse.lirmm -DartifactId=SentimentClassification -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
        
sudo cp target/sentiment-classification.jar /opt/tomcat/lib/sentiment-classification.jar
sudo chown tomcat:tomcat /opt/tomcat/lib/sentiment-classification.jar
