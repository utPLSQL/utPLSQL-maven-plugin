docker run --rm -v /home/runner/work/utPLSQL-maven-plugin/utPLSQL-maven-plugin/github_scripts:/github_scripts -w /github_scripts/sql \
    --network host --entrypoint sqlplus truemark/sqlplus \
    sys/oracle@//127.0.0.1:1521/XE as sysdba @create_users.sql

docker run --rm -v /home/runner/work/utPLSQL-maven-plugin/utPLSQL-maven-plugin/github_scripts:/github_scripts -w /github_scripts/sql \
    --network host --entrypoint sqlplus truemark/sqlplus \
    app/pass@//127.0.0.1:1521/XE @create_app_objects.sql

docker run --rm -v /home/runner/work/utPLSQL-maven-plugin/utPLSQL-maven-plugin/github_scripts:/github_scripts -w /github_scripts/sql \
    --network host --entrypoint sqlplus truemark/sqlplus \
    code_owner/pass@//127.0.0.1:1521/XE @create_source_owner_objects.sql

docker run --rm -v /home/runner/work/utPLSQL-maven-plugin/utPLSQL-maven-plugin/github_scripts:/github_scripts -w /github_scripts/sql \
    --network host --entrypoint sqlplus truemark/sqlplus \
    tests_owner/pass@//127.0.0.1:1521/XE @create_tests_owner_objects.sql
