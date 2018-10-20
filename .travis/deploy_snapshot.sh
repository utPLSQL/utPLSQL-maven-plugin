#!/bin/bash
set -ev

DATE=`date +%Y%m%d%H%M`
OLD_VERSION=`mvn -q org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -DforceStdout -Dexpression=project.version`
VERSION=`sed "s/-SNAPSHOT/-${DATE}-SNAPSHOT/g" <<<"${OLD_VERSION}"`

echo "Changing snapshot version, OLD: ${OLD_VERSION} NEW: ${VERSION}"
mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${VERSION}

cd utplsql-maven-plugin
mvn deploy -DskipTests -U -Prelease
