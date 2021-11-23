docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus \
    sys/oracle@//127.0.0.1:1521/XE as sysdba @scripts/sql/create_users.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus \
    app/pass@//127.0.0.1:1521/XE @scripts/sql/create_app_objects.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus \
    code_owner/pass@//127.0.0.1:1521/XE @scripts/sql/create_source_owner_objects.sql

docker run --rm -v $(pwd):/work -w /work/  --network host --entrypoint sqlplus truemark/sqlplus \
    tests_owner/pass@//127.0.0.1:1521/XE @scripts/sql/create_tests_owner_objects.sql
