whenever sqlerror exit failure rollback
whenever oserror  exit failure rollback

create synonym TO_TEST_ME for CODE_OWNER.TO_TEST_ME;
create synonym PKG_TEST_ME for CODE_OWNER.PKG_TEST_ME;

@src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/owner_param/scripts/test/bar/packages/TEST_PKG_TEST_ME.pks
@src/test/resources-its/org/utplsql/maven/plugin/UtPlsqlMojoIT/owner_param/scripts/test/bar/package_bodies/TEST_PKG_TEST_ME.pkb
