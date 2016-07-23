package com.badboy.webservice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by badboy on 3/22/2016.
 */
public class writenote extends Activity {
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String isok=(String)msg.obj;

                Log.d("data2",isok+"");
                Toast.makeText(writenote.this, "通知发送成功", Toast.LENGTH_SHORT).show();
            notetitle.setText("");
            notebody.setText("");

        }
    };
    private EditText notetitle,notebody;
    private Button writebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writenote);
        notetitle=(EditText)findViewById(R.id.notetitle);
        notebody=(EditText)findViewById(R.id.notebody);
        writebutton=(Button)findViewById(R.id.writebutton);
        writebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> a= new ArrayList<String>();
                        ArrayList<String> b= new ArrayList<String>();
                        a.add("userid");
                        a.add("username");
                        a.add("notetitle");
                        a.add("notebody");
                        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
                        String userid=pref.getString("userid", "");
                        String username=pref.getString("username", "");
                        b.add(userid);
                        b.add(username);
                        b.add(notetitle.getText().toString());
                        b.add(notebody.getText().toString());
                        MyDBUtils myDBUtils=new MyDBUtils();
                        InputStream inputStream = myDBUtils.getchecklist("183.64.36.130:6666","writenote",1,a,b);
                        getresult(inputStream);
                    }
                }).start();
            }
        });
    }
    public void getresult(InputStream inputStream){
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setInput(inputStream, "UTF-8");
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
                        if (name.equalsIgnoreCase("iswriteok")) {
                            userid =parser.nextText()+"1";


                        }
                        break;


                }
                eventType = parser.next();
            }

            inputStream.close();
            Message message = new Message();
            message.obj = userid;

            handler.sendMessage(message);

        }
        catch (Exception e)
        {
            Log.d("data23", e.toString());
            e.printStackTrace();
        }
    }
}
