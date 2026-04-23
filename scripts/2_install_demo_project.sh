docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus:19.8 \
    sys/oracle@${DB_URL} as sysdba @scripts/sql/create_users.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus:19.8 \
    app/pass@${DB_URL} @scripts/sql/create_app_objects.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus:19.8 \
    code_owner/pass@${DB_URL} @scripts/sql/create_source_owner_objects.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus:19.8 \
    tests_owner/pass@${DB_URL} @scripts/sql/create_tests_owner_objects.sql
