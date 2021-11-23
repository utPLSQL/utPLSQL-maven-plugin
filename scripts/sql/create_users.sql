whenever sqlerror exit failure rollback
whenever oserror exit failure rollback
set echo off
set verify off

define UTPLSQL_USER = 'UT3';
define APP_USER = 'APP';
define CODE_OWNER = 'CODE_OWNER';
define TESTS_OWNER = 'TESTS_OWNER';
define DB_PASS = 'pass';

grant execute any procedure to &UTPLSQL_USER;
grant create any procedure to &UTPLSQL_USER;
grant execute on dbms_lob to &UTPLSQL_USER;
grant execute on dbms_sql to &UTPLSQL_USER;
grant execute on dbms_xmlgen to &UTPLSQL_USER;
grant execute on dbms_lock to &UTPLSQL_USER;  

create user &APP_USER identified by &DB_PASS quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view to &APP_USER;
grant select any dictionary to &APP_USER;

create user &CODE_OWNER identified by &DB_PASS quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view to &CODE_OWNER;

create user &TESTS_OWNER identified by &DB_PASS quota unlimited on USERS default tablespace USERS;
grant create session, create procedure, create type, create table, create sequence, create view, create synonym to &TESTS_OWNER;
grant select any dictionary to &TESTS_OWNER;
grant select any table, delete any table, drop any table to &TESTS_OWNER;
grant execute any procedure to &TESTS_OWNER;
