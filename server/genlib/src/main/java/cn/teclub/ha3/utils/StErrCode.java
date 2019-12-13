package cn.teclub.ha3.utils;

/**
 * all error codes
 *
 * <pre>
 *   0x0000 ~ 0x0FFF     	- system-wide / globally;
 *   0x1000 ~ 0x7FFF 		- server side error;
 *   0x8000 ~ 0xFFFF 		- client side error;
 *
 * </pre>
 *
 */
public enum StErrCode {
    SUCCESS(0x00),
    ERR_TIMEOUT(0x01),
    ERR_UNKNOWN(0x02),
    ERR_TODO(0x03),


    //// server-side errors ////
    ERR_SERVER(0x1000),
    ERR_SRV_ACCT_NOT_FOUND(0x1001),
    ERR_SRV_PASS_WRONG(0x1002),
    ERR_SRV_TOKEN_WRONG(0x1003),
    ERR_SRV_AUTH_CODE_NOT_SEND(0x1004),
    ERR_SRV_AUTH_CODE_NOT_FOUND(0x1005),
    ERR_SRV_AUTH_CODE_NOT_EQUAL(0x1006),


    //// client-side errors ////
    ERR_CLIENT(0x8000),
    ERR_CLT_LOGIN_TIMEOUT(0x8001),
    ERR_CLT_LOGIN_NO_RESULT(0x8002),
    ERR_CLT_TOKEN_NOT_FOUND(0x8003),


		/*
		public static final int ERR_TIMEOUT             =    0x01;
        public static final int ERR_UNKNOWN             =    0x02;

        public static final int ERR_TOKEN_INVALID       = 0x10000;
        public static final int ERR_LOGIN_TIMEOUT       = 0x10001;
        public static final int ERR_LOGIN_PASS_WRONG    = 0x10002;
        public static final int ERR_LOGIN_NO_RESULT     = 0x10003;

		 */

    //// LAST ERROR ////
    ERR_FATAL(0xFFFF);


    ////////////////////////////////////////////////////////////////////////

    private final int value;

    StErrCode(int v) {
        this.value = v;
    }

    public int getValue() {
        return value;
    }
}



