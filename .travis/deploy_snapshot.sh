#!/bin/bash
set -ev

DATE=`date +%Y%m%d%H%M`
OLD_VERSION=`mvn -q ${MAVEN_HELP_PLUGIN}:evaluate -DforceStdout -Dexpression=project.version`
VERSION=`sed "s/-SNAPSHOT/-${DATE}-SNAPSHOT/g" <<<"${OLD_VERSION}"`

echo "Changing snapshot version, OLD: ${OLD_VERSION} NEW: ${VERSION}"
mvn $MAVEN_VERSION_PLUGIN:set -DnewVersion=${VERSION};

cd utplsql-maven-plugin
mvn deploy -DskipTests -U -Prelease
