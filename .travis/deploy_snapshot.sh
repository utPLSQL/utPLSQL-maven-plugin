#!/bin/bash
set -ev

mvn deploy -DskipTests -U -Prelease
