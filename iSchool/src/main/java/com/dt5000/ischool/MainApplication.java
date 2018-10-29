package com.dt5000.ischool;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

import com.dt5000.ischool.db.greendao.DaoMaster;
import com.dt5000.ischool.db.greendao.DaoSession;
import com.dt5000.ischool.net.RetrofitService;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 主Application
 *
 * @author 周锋
 * @date 2016年1月7日 下午1:21:42
 * @ClassInfo com.dt5000.ischool.MainApplication
 * @Description
 */
public class MainApplication extends MultiDexApplication {

    /**
     * Activity栈，用于存放TabActiviy中所有页面
     */
    public static List<Activity> activityList = new ArrayList<Activity>();
    private static final String DB_NAME = "userInfo-db";
    private static DaoSession mDaoSession;

    private static MainApplication application = new MainApplication();

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化极光推送服务
        JPushInterface.setDebugMode(false);
        JPushInterface.init(getApplicationContext());
        initGreenDao();
        RetrofitService.init();
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), DB_NAME);
        Database database = helper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static MainApplication getApplication() {
        return application;
    }
}