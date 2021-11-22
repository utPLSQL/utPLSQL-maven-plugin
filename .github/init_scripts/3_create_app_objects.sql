ALTER SESSION SET CURRENT_SCHEMA = APP;

--
-- This is a table used to demonstrate the UNIT test framework.
--
CREATE TABLE TO_TEST_ME
(
  SNAME VARCHAR2(10)
);
/

--
-- This package is used TO demonstrate the utPL/SQL possibilities 
--
CREATE OR REPLACE PACKAGE PKG_TEST_ME AS
  FUNCTION FC_TEST_ME(PPARAM1 IN VARCHAR2) RETURN NUMBER;
  PROCEDURE PR_TEST_ME(PSNAME IN VARCHAR2);
END PKG_TEST_ME;
/

CREATE OR REPLACE PACKAGE BODY PKG_TEST_ME IS
  --
  -- This 
  --
  FUNCTION FC_TEST_ME(PPARAM1 IN VARCHAR2) RETURN NUMBER IS
  BEGIN
    IF PPARAM1 IS NULL THEN
      RETURN NULL;
    ELSIF PPARAM1 = '1' THEN
      RETURN 1;
    ELSE
      RETURN 0;
    END IF;
  END FC_TEST_ME;

  PROCEDURE PR_TEST_ME(PSNAME IN VARCHAR2) IS
  BEGIN
    IF PSNAME IS NULL THEN
      NULL;
    ELSE
      INSERT INTO TO_TEST_ME (SNAME) VALUES (PSNAME);
      COMMIT;
    END IF;
  END PR_TEST_ME;

END PKG_TEST_ME;
/

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

CREATE OR REPLACE PACKAGE BODY TEST_PKG_TEST_ME AS

  ---------------------------------------------------------------------------
  PROCEDURE SETUP_GLOBAL IS
  BEGIN
    -- Put here the code which is valid for all tests and that should be
    -- executed once.
    NULL;
  END SETUP_GLOBAL;

  ---------------------------------------------------------------------------
  PROCEDURE TEARDOWN_GLOBAL IS
  BEGIN
    -- Put here the code that should be called only once after all the test
    -- have executed
    NULL;
  END TEARDOWN_GLOBAL;

  ---------------------------------------------------------------------------
  PROCEDURE SETUP_TEST IS
  BEGIN
    -- Nothing to clean up globally
    NULL;
  END SETUP_TEST;

  PROCEDURE TEARDOWN_TEST IS
  BEGIN
    -- Nothing to clean up globally
    NULL;
  END TEARDOWN_TEST;

  PROCEDURE TEST_FC_INPUT_1 IS
  BEGIN
    -- Ok this is a real test where I check that the function return 1
    -- when called with a '1' parameter
    UT.EXPECT(PKG_TEST_ME.FC_TEST_ME('1')).TO_EQUAL(1);
  END;

  PROCEDURE SETUP_TEST_FC_INPUT_1 IS
  BEGIN
    -- Nothing to be done really
    NULL;
  END;

  PROCEDURE TEARDOWN_TEST_FC_INPUT_1 IS
  BEGIN
    -- Nothing to be done really
    NULL;
  END;

  PROCEDURE TEST_FC_INPUT_0 IS
  BEGIN
    -- Ok this is a real test where I check that the function return 0
    -- when called with a '0' parameter
    UT.EXPECT(PKG_TEST_ME.FC_TEST_ME('0')).TO_EQUAL(0);
  END;

  PROCEDURE TEST_FC_INPUT_NULL IS
  BEGIN
    -- Ok I check that the function return NULL
    -- when called with a NULL parameter
    UT.EXPECT(PKG_TEST_ME.FC_TEST_ME(NULL)).TO_BE_NULL;
  END TEST_FC_INPUT_NULL;

  PROCEDURE TEST_PR_TEST_ME_NULL IS
    VNCOUNT1 PLS_INTEGER;
    VNCOUNT2 PLS_INTEGER;
  BEGIN
    -- In this example I check that the procedure does
    -- not insert anything when passing it a NULL parameter
    SELECT COUNT(1) INTO VNCOUNT1 FROM TO_TEST_ME;
    PKG_TEST_ME.PR_TEST_ME(NULL);
    SELECT COUNT(1) INTO VNCOUNT2 FROM TO_TEST_ME;
    UT.EXPECT(VNCOUNT1).TO_EQUAL(VNCOUNT2);
  END;

  PROCEDURE TEST_PR_TEST_ME_NOT_NULL IS
    VNCOUNT1 PLS_INTEGER;
    VNCOUNT2 PLS_INTEGER;
    VSNAME   TO_TEST_ME.SNAME%TYPE;
  BEGIN
    -- In this test I will check that I do insert a value
    -- when the parameter is not null. I futher check that
    -- the procedure has inserted the value I specified.
    SELECT COUNT(1) INTO VNCOUNT1 FROM TO_TEST_ME;
    VSNAME := TO_CHAR(VNCOUNT1);
    PKG_TEST_ME.PR_TEST_ME(VSNAME);
    SELECT COUNT(1) INTO VNCOUNT2 FROM TO_TEST_ME;
  
    -- Check that I have inserted the value
    UT.EXPECT(VNCOUNT1 + 1).TO_EQUAL(VNCOUNT2);
    SELECT COUNT(1) INTO VNCOUNT2 FROM TO_TEST_ME T WHERE T.SNAME = VSNAME;
  
    -- Check that I inserted the one I said I would insert
    UT.EXPECT(VNCOUNT2).TO_EQUAL(1);
    DELETE FROM TO_TEST_ME T WHERE T.SNAME = VSNAME;
    COMMIT;
  END;

  PROCEDURE TEST_PR_TEST_ME_EXISTS IS
  BEGIN
    -- In case the value exists the procedure should fail with an exception.
    BEGIN
      PKG_TEST_ME.PR_TEST_ME('EXISTS');
      PKG_TEST_ME.PR_TEST_ME('EXISTS');
    EXCEPTION
      WHEN OTHERS THEN
        UT.FAIL('Unexpected exception raised');
    END;
  END;

  PROCEDURE TEST_PR_TEST_ME_CURSOR IS
    TYPE REF_CURSOR IS REF CURSOR;
    VEXPECTED REF_CURSOR;
    VACTUAL   REF_CURSOR;
  BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE TO_TEST_ME';
    OPEN VEXPECTED FOR
      SELECT T.SNAME FROM TO_TEST_ME T;
    OPEN VACTUAL FOR
      SELECT T.SNAME FROM TO_TEST_ME T;
    UT.EXPECT(VEXPECTED).TO_(EQUAL(VACTUAL));
  END;

END;
/
