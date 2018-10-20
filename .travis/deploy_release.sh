#!/bin/bash
set -ev

VERSION=$(tr -d "/v/" <<<${TRAVIS_TAG})
mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${VERSION}

cd utplsql-maven-plugin
mvn deploy -DskipTests -U -Prelease
