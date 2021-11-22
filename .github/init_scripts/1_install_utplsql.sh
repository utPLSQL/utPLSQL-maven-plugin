UTPLSQL_DOWNLOAD_URL=$(curl --silent https://api.github.com/repos/utPLSQL/utPLSQL/releases/latest | awk '/browser_download_url/ { print $2 }' | grep ".zip\"" | sed 's/"//g')

curl -Lk "${UTPLSQL_DOWNLOAD_URL}" -o utPLSQL.zip

unzip -q utPLSQL.zip

cd utPLSQL/source

sqlplus sys/oracle@//localhost/XE as sysdba @install_headless.sql UT3 UT3 users
