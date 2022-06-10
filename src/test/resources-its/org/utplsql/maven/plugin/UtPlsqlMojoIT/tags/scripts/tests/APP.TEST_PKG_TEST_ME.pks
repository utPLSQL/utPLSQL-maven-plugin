CREATE OR REPLACE PACKAGE TEST_PKG_TEST_ME AS
  -- %suite(TEST_PKG_TEST_ME)
  -- %suitepath(plsql.examples)
  --
  -- This package shows all the possibilities to unit test
  -- your PL/SQL package. NOTE that it is not limited to
  -- testing your package. You can do that on all your 
  -- procedure/functions...
  --

  /**
  * This two parameters are used by the test framework in
  * order to identify the test suite to run
  */

  /*
  * This method is invoked once before any other method.
  * It should contain all the setup code that is relevant
  * for all your test. It might be inserting a register,
  * creating a type, etc...
  */
  -- %beforeall
  PROCEDURE SETUP_GLOBAL;

  /*
  * This method is invoked once after all other method.
  * It can be used to clean up all the resources that
  * you created in your script
  */
  -- %afterall
  PROCEDURE TEARDOWN_GLOBAL;

  /*
  * This method is called once before each test.
  */
  -- %beforeeach
  PROCEDURE SETUP_TEST;

  /*
  * This method is called once after each test.
  */
  -- %aftereach
  PROCEDURE TEARDOWN_TEST;

  /**
  * This is a real test. The main test can declare a setup
  * and teardown method in order to setup and cleanup things
  * for that specific test.
  */
  -- %test
  -- %displayname(Checking if function ('1') returns 1)
  -- %beforetest(SETUP_TEST_FC_INPUT_1)
  -- %aftertest(TEARDOWN_TEST_FC_INPUT_1)
  PROCEDURE TEST_FC_INPUT_1;
  PROCEDURE SETUP_TEST_FC_INPUT_1;
  PROCEDURE TEARDOWN_TEST_FC_INPUT_1;

  -- %test
  -- %displayname(Checking if function ('0') returns 0)
  PROCEDURE TEST_FC_INPUT_0;

  -- %test
  -- %displayname(Checking if function (NULL) returns NULL)
  PROCEDURE TEST_FC_INPUT_NULL;

  -- %test
  -- %displayname(Checking if procedure (NULL) insert)
  PROCEDURE TEST_PR_TEST_ME_NULL;

  -- %test
  -- %displayname(Checking if procedure (NOT NULL) insert)
  -- %rollback(manual)
  PROCEDURE TEST_PR_TEST_ME_NOT_NULL;

  -- %test
  -- %displayname(Checking if procedure (NOT NULL) insert while existing)
  -- %rollback(manual)
  -- %tags(exists)
  PROCEDURE TEST_PR_TEST_ME_EXISTS;
  
  -- %test
  -- %displayname(Demonstrating the use of cursor)
  -- %rollback(manual)
  -- %tags(cursor)
  PROCEDURE TEST_PR_TEST_ME_CURSOR;

END;
/
