#!/bin/bash
set -ev

VERSION=$(tr -d "/v/" <<<${TRAVIS_TAG})
mvn org.codehaus.mojo:versions-maven-plugin:2.7:set -DnewVersion=${VERSION}

mvn deploy -DskipTests -U -Prelease
