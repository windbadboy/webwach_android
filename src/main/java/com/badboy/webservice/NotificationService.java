package com.badboy.webservice;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by badboy on 3/17/2016.
 */
public class NotificationService extends Service{
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<NotificationList> str=(List<NotificationList>)msg.obj;
            int i=msg.what;
            long[] vibrates={0,1000,1000,1000};
            Notification notification;
            Notification.Builder builder=new Notification.Builder(NotificationService.this);
            NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            builder.setContentTitle(str.get(0).gettitle() + "...");
            builder.setTicker("收到" + i + "条新通知");
            builder.setContentText("发送者：" + str.get(i - 1).getusername());
            builder.setSmallIcon(R.drawable.old);
            Intent intent=new Intent(NotificationService.this,ShowNote.class);
            intent.putExtra("username",str.get(i-1).getusername());
            intent.putExtra("title",str.get(i-1).gettitle());
            intent.putExtra("mybody",str.get(i-1).getBody());
            intent.putExtra("sendtime",str.get(i-1).getSendtime());
            Log.d("data","mytitle is "+str.get(i-1).gettitle());
            PendingIntent pi=PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pi);
            notification=builder.build();
            notification.defaults=notification.DEFAULT_SOUND;
            notification.vibrate=vibrates;
            notification.ledARGB= Color.GREEN;
            notification.ledOnMS=1000;
            notification.ledOffMS=1000;
            notification.flags=Notification.FLAG_SHOW_LIGHTS;
            manager.notify(10,notification);

            //Toast.makeText(MainActivity.this,"hello"+str,Toast.LENGTH_LONG).show();
        }
    };
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                getXml(2);


            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour =5 * 60 * 1000; // 这是一小时的毫秒数
        long triggerAtTime = System.currentTimeMillis() + anHour;
        Intent i = new Intent(NotificationService.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(NotificationService.this, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);

    }
    public void getXml(int mychoice){
        List<NotificationList> mylist=new ArrayList<NotificationList>();
        String ServerUrl = "http://183.64.36.130:6666/webservice/WebService1.asmx";
        String soapAction="";
        switch(mychoice) {
            case 1:
                soapAction = "http://tempuri.org/" + "getnotification";
                break;
            case 2:
                soapAction = "http://tempuri.org/" + "getnotification";
                break;
            default:
                break;
        }
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body />";
        String soap2;
        String tps, vps, ts;
        String mreakString = "";
        String requestData="";
        MyDBHelper  dbHelper = new MyDBHelper(this, "mynotification.db", null, 2);

        SQLiteDatabase db= dbHelper.getWritableDatabase();
        int myid=0;
        int mynotificationid=0;
        Cursor cursor=db.query("mynotification",null,null,null,null,null,"notificationid desc");

        if(cursor.moveToNext()){
            myid=cursor.getInt(cursor.getColumnIndex("notificationid"));
        }
        int notificationFirstId=0;

        switch(mychoice){
            case 1:
                mreakString = "<gettestinfo xmlns=\"http://tempuri.org/\">";
                tps = "userid";
                //设置该方法的参数为.net webService中的参数名称
                //vps = setid.getText().toString();
                vps="";
                ts = "<" + tps + ">" + vps + "</" + tps + ">";
                mreakString = mreakString + ts;
                mreakString = mreakString + "</gettestinfo>";
                soap2 = "</soap:Envelope>";
                requestData = soap + mreakString + soap2;
                break;
            case 2:
                soap2 = "</soap:Envelope>";
                requestData = soap + mreakString + soap2;
        }
        try
        {
            URL url = new URL (ServerUrl); //指定服务器地址
            HttpURLConnection con = (HttpURLConnection) url.openConnection();//打开链接
            byte[] bytes = requestData.getBytes ("utf-8"); //指定编码格式，可以解决中文乱码问题
            con.setDoInput (true); //指定该链接是否可以输入
            con.setDoOutput (true); //指定该链接是否可以输出
            con.setUseCaches (false); //指定该链接是否只用caches
            con.setConnectTimeout (6000); // 设置超时时间
            con.setRequestMethod ("POST"); //指定发送方法名，包括Post和Get。
            con.setRequestProperty ("Content-Type", "text/xml;charset=utf-8"); //设置（发送的）内容类型
            con.setRequestProperty ("SOAPAction", soapAction); //指定soapAction
            con.setRequestProperty ("Content-Length", "" + bytes.length); //指定内容长度

            //发送数据
            OutputStream outStream = con.getOutputStream();
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
            //获取数据
            InputStream inputStream = con.getInputStream();
            XmlPullParser parser = Xml.newPullParser();
            try
            {
                parser.setInput (inputStream, "UTF-8");
                int eventType = parser.getEventType();
                String userid="";
                String mytitle="";
                String mybody="";
                String sendtime="";
                String notificationid="";
                String expiredtime="";
                String username="";
                String notificationType="";
                int j=0;

                ContentValues values = new ContentValues();
               // Log.d("data", "above end_document");
                switch(mychoice) {
                    case 2:
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

                                    break;
                                case XmlPullParser.START_TAG:// 开始元素事件

                                    String name = parser.getName();
                                    if (name.equalsIgnoreCase("mytitle")) {
                                        mytitle = parser.nextText();
                                    } else if (name.equalsIgnoreCase("mybody")) {
                                        mybody = parser.nextText();

                                    } else if (name.equalsIgnoreCase("sendtime")) {
                                        sendtime = parser.nextText();

                                    }else if (name.equalsIgnoreCase("userid")) {
                                        userid = parser.nextText();

                                    }else if (name.equalsIgnoreCase("username")) {
                                        username = parser.nextText();

                                    }
                                    else if (name.equalsIgnoreCase("notificationid")) {
                                        notificationid = parser.nextText();
                                        mynotificationid=Integer.parseInt(notificationid);

                                    }else if (name.equalsIgnoreCase("expiredtime")) {
                                        expiredtime = parser.nextText();

                                    }else if (name.equalsIgnoreCase("notificationtype")) {
                                        notificationType = parser.nextText();

                                    }
                                    break;
                                case XmlPullParser.END_TAG:// 结束元素事件
                                    if (parser.getName().equalsIgnoreCase("shownote")) {



                                        if(Integer.parseInt(notificationid)-myid>0) {
                                            notificationFirstId=Integer.parseInt(notificationid);
                                            NotificationList mylist2 = new NotificationList(userid, mytitle, mybody,sendtime,username);
                                            mylist.add(mylist2);
                                            j++;
                                            values.put("notificationTitle", mytitle);
                                            values.put("notificationBody", mybody);
                                            values.put("username", username);
                                            values.put("senddate", sendtime);
                                            values.put("expiredtime", expiredtime);
                                            values.put("notificationid", notificationid);
                                            values.put("userid", userid);
                                            values.put("notificationtype",notificationType);
                                            values.put("isread",0);
                                            db.insert("mynotification", null, values); // 插入第一条数据

                                            values.clear();
                                        }

                                    }
                                    break;

                            }
                            eventType = parser.next();
                        }
                }
                inputStream.close();
                if(notificationFirstId>myid) {
                    Message message = new Message();
                    message.obj = mylist;
                    message.what = j;
                    handler.sendMessage(message);
                }
            }
            catch (Exception e)
            {
                Log.d("data", e.toString());
                e.printStackTrace();
            }

            /**
             * 此类到此结束了，比原来的HttpConnSoap还短，因为这里没有对返回的数据做解析。数据完全都保存在了inputStream中。
             * 而原来的类是将数据解析成了ArrayList
             * <String>格式返回。显然，这样无法解决我们上面的需求（返回值是复杂类型的List）
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("data", e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(NotificationService.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(NotificationService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }
}

