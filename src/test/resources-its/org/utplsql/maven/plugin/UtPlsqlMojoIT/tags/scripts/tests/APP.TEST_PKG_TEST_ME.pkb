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
