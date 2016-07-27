package com.badboy.webservice;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by badboy on 3/20/2016.
 */
public class NotificationActivity extends Activity{
    TimerTask task=new TimerTask() {
        @Override
        public void run() {

            addchecklist();
        }
    };
    private ListView lv;
    Timer timer = new Timer();
    private String noteid;
    private String noteType;
    private String userid;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<users> mylist=(List<users>)msg.obj;
            MyAdapter myAdapter=new MyAdapter(NotificationActivity.this,R.layout.dutyadapter_item,mylist);
            lv.setAdapter(myAdapter);


        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationactivity);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        TextView mytv=(TextView)findViewById(R.id.username);
        TextView sendtime=(TextView)findViewById(R.id.notesendtime);
        TextView title=(TextView)findViewById(R.id.title);
        TextView mybody=(TextView)findViewById(R.id.mybody);
        lv=(ListView)findViewById(R.id.checklist);
        mytv.setText("发件人:" + username);
        sendtime.setText("发送日期:" +  intent.getStringExtra("sendtime"));
        title.setText(intent.getStringExtra("title"));
        mybody.setText(intent.getStringExtra("mybody"));
        noteid=intent.getStringExtra("notificationId");
        noteType=intent.getStringExtra("notificationtype");
        userid=intent.getStringExtra("userid");
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(10);
        timer.schedule(task,0,1000*10);



    }
    public void addchecklist(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 ArrayList<String> a= new ArrayList<String>();
                 ArrayList<String> b= new ArrayList<String>();
                 a.add("typeid");
                 a.add("notificationid");
                 b.add(noteType);
                 b.add(noteid);
                 MyDBUtils myDBUtils=new MyDBUtils();
                 InputStream inputStream = myDBUtils.getchecklist("183.64.36.130:6666","getchecklist",1,a,b);
                 Log.d("data", "belowchecklist");
                    getxml(inputStream);
             }
         }).start();
    }

    public void getxml(InputStream inputStream){
        List<users> mylist=new ArrayList<users>();
        XmlPullParser parser = Xml.newPullParser();
        Log.d("data", "abovetry");
        try
        {
            Log.d("data", "aboveinputStream");
            parser.setInput (inputStream, "UTF-8");
            Log.d("data", "belowinputStream");
            int eventType = parser.getEventType();
            String userid="";
            String mytitle="";
            String mybody="";
            String sendtime="";
            String notificationid="";
            String expiredtime="";
            String username="";
            String notificationType="";
            String checktime="";
            int j=0;

            ContentValues values = new ContentValues();
            // Log.d("data", "above end_document");
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

                                break;
                            case XmlPullParser.START_TAG:// 开始元素事件

                                String name = parser.getName();
                                 if (name.equalsIgnoreCase("userid")) {
                                    userid = parser.nextText();


                                }else if (name.equalsIgnoreCase("username")) {
                                    username = parser.nextText();

                                }else if (name.equalsIgnoreCase("checktime")) {
                                     checktime = parser.nextText();

                                 }
                                break;
                            case XmlPullParser.END_TAG:// 结束元素事件
                                if (parser.getName().equalsIgnoreCase("loginresult")) {
                                        users templist = new users(userid, username,"已读("+checktime+")","","");
                                        mylist.add(templist);
                                        j++;



                                }
                                break;

                        }
                        eventType = parser.next();
                    }

            inputStream.close();
                Message message = new Message();
                message.obj = mylist;
                message.what = j;

                handler.sendMessage(message);

        }
        catch (Exception e)
        {
            Log.d("data23", e.toString());
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        timer.cancel();
    }
}
