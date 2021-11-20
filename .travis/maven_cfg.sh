#!/bin/bash
set -ev
cd $(dirname $(readlink -f $0))

cp settings.xml $MAVEN_CFG/settings.xml
