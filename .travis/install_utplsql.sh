#!/bin/bash
set -ev
cd $(dirname $(readlink -f $0))

# Download the specified version of utPLSQL.
UTPLSQL_VERSION="v3.0.4"
UTPLSQL_FILE="utPLSQL"
curl -L -O "https://github.com/utPLSQL/utPLSQL/releases/download/$UTPLSQL_VERSION/$UTPLSQL_FILE.tar.gz"

# Download develop branch of utPLSQL.
#UTPLSQL_VERSION="develop"
#UTPLSQL_FILE="utPLSQL"
#git clone -b develop --single-branch https://github.com/utPLSQL/utPLSQL.git
# tar -czf $UTPLSQL_FILE.tar.gz $UTPLSQL_FILE && rm -rf $UTPLSQL_FILE

# Create a temporary install script.
cat > install.sh.tmp <<EOF
tar -xzf ${UTPLSQL_FILE}.tar.gz && rm ${UTPLSQL_FILE}.tar.gz
cd ${UTPLSQL_FILE}/source
sqlplus -S -L sys/oracle@//127.0.0.1:1521/xe AS SYSDBA @install_headless.sql $1 $2 users

sqlplus -S -L sys/oracle@//127.0.0.1:1521/xe AS SYSDBA << SQL
grant execute any procedure to $1;
grant create any procedure to $1;
grant execute on dbms_lob to $1;
grant execute on dbms_sql to $1;
grant execute on dbms_xmlgen to $1;
grant execute on dbms_lock to $1;  


exit
SQL
EOF

# Copy utPLSQL files to the container and install it.
docker cp ./$UTPLSQL_FILE.tar.gz $ORACLE_VERSION:/$UTPLSQL_FILE.tar.gz
# docker cp ./$UTPLSQL_FILE $ORACLE_VERSION:/$UTPLSQL_FILE
docker cp ./install.sh.tmp $ORACLE_VERSION:/install.sh

# Remove temporary files.
# rm $UTPLSQL_FILE.tar.gz
rm -rf $UTPLSQL_FILE
rm install.sh.tmp

# Execute the utPLSQL installation inside the container.
docker exec $ORACLE_VERSION bash install.sh
