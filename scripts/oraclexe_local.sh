docker run --name ora-utplsql -p 1521:1521 -e ORACLE_PASSWORD=oracle -v ../init_scripts:/container-entrypoint-initdb.d gvenzl/oracle-xe:18.4.0-slim
