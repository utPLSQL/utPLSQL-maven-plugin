#!/bin/bash
set -ev

VERSION=$(tr -d "/v/" <<<$TRAVIS_TAG);
mvn $MAVEN_VERSION_PLUGIN:set -DnewVersion=${VERSION};

cd utplsql-maven-plugin
mvn deploy -DskipTests -U -Prelease
