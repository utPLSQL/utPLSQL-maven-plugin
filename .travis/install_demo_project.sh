#!/bin/bash
set -ev

PROJECT_FILES_SRC="utplsql-maven-plugin/src/test/resources/simple-project"
PROJECT_FILES="resources"

cat > demo_project.sh.tmp <<EOF
sqlplus -S -L sys/oracle@//127.0.0.1:1521/xe AS SYSDBA <<SQL
create user ${DB_USER} identified by ${DB_PASS} quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view to ${DB_USER};
grant select any dictionary to ${DB_USER};
exit
SQL

cd ${PROJECT_FILES}
sqlplus -S -L ${DB_USER}/${DB_PASS}@//127.0.0.1:1521/xe <<SQL
whenever sqlerror exit failure rollback
whenever oserror  exit failure rollback

@scripts/sources/TO_TEST_ME.tab
@scripts/sources/APP.PKG_TEST_ME.spc
@scripts/sources/APP.PKG_TEST_ME.bdy

@scripts/tests/APP.TEST_PKG_TEST_ME.spc
@scripts/tests/APP.TEST_PKG_TEST_ME.bdy

exit
SQL
EOF

docker cp ./$PROJECT_FILES_SRC $ORACLE_VERSION:/$PROJECT_FILES
docker cp ./demo_project.sh.tmp $ORACLE_VERSION:/demo_project.sh
docker exec $ORACLE_VERSION bash demo_project.sh
