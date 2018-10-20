#!/bin/bash
set -ev

DATE=`date +%Y%m%d%H%M`
#using this solution to get version into variable: https://stackoverflow.com/a/26514030/1470603
OLD_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
VERSION=`sed "s/-SNAPSHOT/-${DATE}-SNAPSHOT/g" <<<"${OLD_VERSION}"`

echo "Changing snapshot version, OLD: ${OLD_VERSION} NEW: ${VERSION}"
mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${VERSION}

cd utplsql-maven-plugin
mvn deploy -DskipTests -U -Prelease
