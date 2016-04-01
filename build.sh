#!/bin/bash -e
# A script to build the sentiment-classification jar. Then add it to maven local repo and tomcat classpath

mvn clean package

# In case you need to clean the local repository (if not updating well)
#mvn dependency:purge-local-repository
mvn install:install-file -Dfile=target/sentiment-classification.jar -DgroupId=fr.lirmm.advanse -DartifactId=SentimentClassification -Dversion=0.1 -Dpackaging=jar -DgeneratePom=true
