UTPLSQL_DOWNLOAD_URL=$(curl --silent https://api.github.com/repos/utPLSQL/utPLSQL/releases/latest | awk '/browser_download_url/ { print $2 }' | grep ".zip\"" | sed 's/"//g')

curl -Lk "${UTPLSQL_DOWNLOAD_URL}" -o utPLSQL.zip

unzip -q utPLSQL.zip

docker run --rm -v $(pwd)/utPLSQL:/utPLSQL -w /utPLSQL/source --network host \
    --entrypoint sqlplus truemark/sqlplus:19.8 sys/oracle@//127.0.0.1:1521/XE as sysdba @install_headless.sql UT3 UT3 users
