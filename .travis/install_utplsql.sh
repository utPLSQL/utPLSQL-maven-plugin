#!/bin/bash
set -ev
cd $(dirname $(readlink -f $0))

DB_URL="//127.0.0.1:1521/XE"
UTPLSQL_VERSION="v3.1.9"
UTPLSQL_DIR="utPLSQL"
SQLPLUS_IMAGE=${DOCKER_REPO}:${ORACLE_VERSION}
VOLUME="/utPLSQL"

git clone --depth=1 --branch=${UTPLSQL_VERSION} https://github.com/utPLSQL/utPLSQL.git ${UTPLSQL_DIR}

docker run --rm -v $(pwd)/${UTPLSQL_DIR}:${VOLUME} -w ${VOLUME}/source --network host --entrypoint sqlplus ${SQLPLUS_IMAGE} \
    sys/oracle@${DB_URL} as sysdba @install_headless.sql ${DB_UT3_USER} ${DB_UT3_PASS} users
