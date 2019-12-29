#!/bin/bash
set -ev

DB_URL="//127.0.0.1:1521/XE"
SQLPLUS_IMAGE=${DOCKER_REPO}:${ORACLE_VERSION}
VOLUME="/project"

docker run --rm -v $(pwd):${VOLUME} -w ${VOLUME} --network host --entrypoint sqlplus ${SQLPLUS_IMAGE} \
    sys/oracle@${DB_URL} as sysdba @.travis/sql/create_users.sql

docker run --rm -v $(pwd):${VOLUME} -w ${VOLUME} --network host --entrypoint sqlplus ${SQLPLUS_IMAGE} \
    app/pass@${DB_URL} @.travis/sql/create_app_objects.sql

docker run --rm -v $(pwd):${VOLUME} -w ${VOLUME} --network host --entrypoint sqlplus ${SQLPLUS_IMAGE} \
    code_owner/pass@${DB_URL} @.travis/sql/create_source_owner_objects.sql

docker run --rm -v $(pwd):${VOLUME} -w ${VOLUME} --network host --entrypoint sqlplus ${SQLPLUS_IMAGE} \
    tests_owner/pass@${DB_URL} @.travis/sql/create_tests_owner_objects.sql
