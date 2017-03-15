package com.example.sphinx.greendaote;

import android.app.Application;

import com.example.sphinx.greendaote.entity.DaoMaster;
import com.example.sphinx.greendaote.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Sphinx on 2017/3/14.
 */

public class CommonApp extends Application {
    //设置flag,是否使用数据库加密
    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        //加密写入,对应的解密方法是getEncryptedReadableDb()
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();

        if (daoSession == null) {
            daoSession = new DaoMaster(db).newSession();
        }

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
