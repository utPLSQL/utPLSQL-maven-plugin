#!/bin/bash
set -ev

PROJECT_FILES_SRC="utplsql-maven-plugin-it/src/it/resources/owner-param-project"
PROJECT_FILES="resources-owner"
DB_CODE_USER=CODE_OWNER
DB_TESTS_USER=TESTS_OWNER
DB_PASS=pass

cat > demo_project.sh.tmp <<EOF
sqlplus -S -L sys/oracle@//127.0.0.1:1521/xe AS SYSDBA <<SQL
create user ${DB_CODE_USER} identified by ${DB_PASS} quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view to ${DB_CODE_USER};

create user ${DB_TESTS_USER} identified by ${DB_PASS} quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view, create synonym to ${DB_TESTS_USER};
grant select any dictionary to ${DB_TESTS_USER};
grant select any table, delete any table, drop any table to ${DB_TESTS_USER};
grant execute any procedure to ${DB_TESTS_USER};
exit
SQL

cd ${PROJECT_FILES}
sqlplus -S -L ${DB_CODE_USER}/${DB_PASS}@//127.0.0.1:1521/xe <<SQL
whenever sqlerror exit failure rollback
whenever oserror  exit failure rollback

@scripts/sources/foo/tables/TO_TEST_ME.tab
@scripts/sources/foo/packages/PKG_TEST_ME.sql
@scripts/sources/foo/package_bodies/PKG_TEST_ME.sql

exit
SQL

sqlplus -S -L ${DB_TESTS_USER}/${DB_PASS}@//127.0.0.1:1521/xe <<SQL
whenever sqlerror exit failure rollback
whenever oserror  exit failure rollback

create synonym TO_TEST_ME for ${DB_CODE_USER}.TO_TEST_ME;
create synonym PKG_TEST_ME for ${DB_CODE_USER}.PKG_TEST_ME;
@scripts/test/bar/packages/TEST_PKG_TEST_ME.sql
@scripts/test/bar/package_bodies/TEST_PKG_TEST_ME.sql

exit
SQL
EOF

docker cp ./$PROJECT_FILES_SRC $ORACLE_VERSION:/$PROJECT_FILES
docker cp ./demo_project.sh.tmp $ORACLE_VERSION:/demo_project.sh
docker exec $ORACLE_VERSION bash demo_project.sh
