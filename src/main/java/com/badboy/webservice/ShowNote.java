package com.badboy.webservice;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by badboy on 3/21/2016.
 */
public class ShowNote extends Activity {
    ListView shownote;
    List<NotificationInfo> mylist=new ArrayList<NotificationInfo>();

    @Override
    protected void onStart() {
        super.onStart();
        getNotification();
        NoteAdapter noteAdapter=new NoteAdapter(this,R.layout.note_adapter,mylist);
        shownote.setAdapter(noteAdapter);
        shownote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationInfo mynoteInfo = mylist.get(position);
                Intent intent = new Intent(ShowNote.this, NotificationActivity.class);
                intent.putExtra("username", mynoteInfo.getSender());
                intent.putExtra("title", mynoteInfo.getNotificationTitle());
                intent.putExtra("mybody", mynoteInfo.getNotificationBody());
                intent.putExtra("sendtime", mynoteInfo.getSendtime());
                intent.putExtra("notificationId", mynoteInfo.getNotificationId());
                intent.putExtra("notificationtype", mynoteInfo.getNotificationType());
                intent.putExtra("userid", mynoteInfo.getUserid());
                setchecklist(mynoteInfo);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shownote);
        shownote=(ListView)findViewById(R.id.shownote);
 //       SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 //       Log.d("data2", formatter.format(new Date(System.currentTimeMillis())));
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(10);


    }
    public void setchecklist(final NotificationInfo mynoteInfo){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> a= new ArrayList<String>();
                ArrayList<String> b= new ArrayList<String>();
                MyDBHelper  dbHelper = new MyDBHelper(ShowNote.this, "mynotification.db", null, 2);
                SQLiteDatabase db= dbHelper.getWritableDatabase();
                Cursor cursor=db.query("mynotification", null, "notificationid=?", new String[]{mynoteInfo.getNotificationId()}, null, null, "notificationid desc");
                if(cursor.moveToNext()){
                    if(cursor.getInt(cursor.getColumnIndex("isread"))==0);{
                        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
                        a.add("userid");
                        a.add("notificationid");
                        b.add(pref.getString("userid", ""));
                        b.add(mynoteInfo.getNotificationId());
                        MyDBUtils myDBUtils=new MyDBUtils();
                        InputStream inputStream=myDBUtils.getchecklist("183.64.36.130:6666","setchecklist",1,a,b);
                        ContentValues contentValues=new ContentValues();
                        contentValues.put("isread",1);
                        db.update("mynotification",contentValues,"notificationid=?",new String[]{mynoteInfo.getNotificationId()});
                    }
                }


            }
        }).start();
    }
    public void getNotification(){

        MyDBHelper  dbHelper = new MyDBHelper(this, "mynotification.db", null, 2);
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        Cursor cursor=db.query("mynotification", null, null, null, null, null, "notificationid desc");
        mylist.clear();
        for(int i=0;i<cursor.getCount();i++)
        if(cursor.moveToNext()){
            String noteTitle=cursor.getString(cursor.getColumnIndex("notificationtitle"));
            String sender=cursor.getString(cursor.getColumnIndex("username"));
            String sendtime;

                sendtime =cursor.getString(cursor.getColumnIndex("senddate"));

            String notificationId=cursor.getString(cursor.getColumnIndex("notificationid"));
            String notificationBody=cursor.getString(cursor.getColumnIndex("notificationbody"));
            String notificationtype=cursor.getString(cursor.getColumnIndex("notificationtype"));
            String userid=cursor.getString(cursor.getColumnIndex("userid"));
            int isread=cursor.getInt(cursor.getColumnIndex("isread"));
            NotificationInfo tempList=new NotificationInfo(noteTitle,sender,sendtime,notificationId,notificationBody,notificationtype,userid,isread);
       //     Log.d("data2", cursor.getCount()+" id:"+notificationId+" isread:"+isread);
            mylist.add(tempList);
        }

    }
}
