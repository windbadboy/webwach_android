package com.badboy.webservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by badboy on 3/18/2016.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK = "create table mynotification ("
            + "id integer primary key autoincrement, "
            + "notificationid integer, "
            + "notificationtitle text, "
            + "notificationbody text, "
            + "username text, "
            + "userid text, "
            + "notificationtype integer, "
            + "expiredtime integer, "
            + "isread integer, "
            + "senddate text)";
    private Context mContext;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
        Log.d("data","onMyDBHelper");
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("data","onMyDBHelper_create");
        db.execSQL(CREATE_BOOK);
        Log.d("data", "onMyDBHelper_Aftercreate");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL("delete from mynotification");
                db.execSQL("alter table mynotification add column userid text");
        }
    }
}