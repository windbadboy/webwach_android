package com.badboy.webservice;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by badboy on 3/23/2016.
 */
public class MyDuty extends Activity {
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<pbinfo> mylist=(List<pbinfo>)msg.obj;
            pbAdapter adapter=new pbAdapter(MyDuty.this,R.layout.pbadapter,mylist);
            lvduty.setAdapter(adapter);
        }
    };
private ListView lvduty;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myduty);
        lvduty=(ListView)findViewById(R.id.lvduty);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate=new Date(System.currentTimeMillis()+24 * 60 * 60 * 1000);
        Calendar calendar=Calendar.getInstance();
//        calendar.set(Calendar.DATE,13);
        calendar.setTimeInMillis(System.currentTimeMillis()+24*60*60*1000);
        Log.d("data2",formatter.format(calendar.getTime())+"");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> a= new ArrayList<String>();
                ArrayList<String> b= new ArrayList<String>();
                SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
                String userid=pref.getString("userid", "");
                String roleid=pref.getString("roleid", "");
                a.add("roleid");
                a.add("userid");
                b.add(roleid);
                b.add(userid);
                MyDBUtils myDBUtils=new MyDBUtils();
                InputStream inputStream=myDBUtils.getchecklist("183.64.36.130:6666", "getrunningpbk", 1, a, b);
                List<pbinfo> mylist=getdutylist(inputStream);
                Message msg=new Message();
                msg.obj=mylist;
                handler.sendMessage(msg);

            }
        }).start();

    }
    public List<pbinfo> getdutylist(InputStream inputStream){
        List<pbinfo> mylist=new ArrayList<pbinfo>();
        String rolename="";
        String username="";
        String pbdate="";
        String myweekday="";

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStream, "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();

                        if (name.equalsIgnoreCase("username")) {
                            username = parser.nextText();

                        } else if (name.equalsIgnoreCase("rolename")) {
                            rolename = parser.nextText();
                        } else if (name.equalsIgnoreCase("pbdate")) {
                            pbdate = parser.nextText();
                        } else if (name.equalsIgnoreCase("myweekday")) {
                            myweekday = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("data3","endtag"+parser.getName());
                        if (parser.getName().equalsIgnoreCase("mypbinfo")) {
                            pbinfo temp=new pbinfo(username,rolename,pbdate,myweekday);
                            Log.d("data3",username);
                            mylist.add(temp);


                        }
break;
                }
                eventType = parser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("data3", e.toString());
        }


        return mylist;
    }
}
