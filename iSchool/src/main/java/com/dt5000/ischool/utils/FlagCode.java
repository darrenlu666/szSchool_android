package com.dt5000.ischool.utils;

/**
 * 标识辅助类
 * 
 * @author 周锋
 * @date 2016年2月17日 上午10:43:14
 * @ClassInfo com.dt5000.ischool.utils.FlagCode
 * @Description
 */
public interface FlagCode {

	int SUCCESS = 1;
	int FAIL = 0;
	int EXCEPTION = -1;
	int SPECIAL=-11111;

	int CODE_0 = 10000;
	int CODE_1 = 10001;
	int CODE_2 = 10002;
	int CODE_3 = 10003;
	int CODE_4 = 10004;

	int ACTIVITY_REQUEST_CODE_0 = 20000;
	int ACTIVITY_REQUEST_CODE_1 = 20001;
	int ACTIVITY_REQUEST_CODE_2 = 20002;
	int ACTIVITY_REQUEST_CODE_3 = 20003;

	int SUCCESS_CODE = 19;
	int FAIL_CODE = -19;
	int EXCEPTION_CODE = -10;
	int SAVESUCCESS_CODE = 18;
	int SAVEFAIL_CODE = -18;
	int SAVEERROR_CODE = -13;
	int DELETESUCCESS_CODE = 17;
	int DELETEFAIL_CODE = -17;
	int REFRESH_CODE = 16;
	int MORE_CODE = -16;
	int NORMALLOAD_CODE = 15;
	int REFRESH_FLAG = 14;
	int TIMER_CODE = 12;
	int MSG_INIT_CODE = 11;
	int SAVE_FINISH_CODE = 9;
	int CLZ_MSG_COUNT_CODE = 8;

}
