package com.badboy.webservice;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 聪 on 2016/3/16.
 */
public class MainFrame extends Activity {
    Timer timer = new Timer();
    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            Message message=new Message();
            message.what=2;
            handler.sendMessage(message);

        }
    };
    private ProgressDialog pdialog;
    private TextView todayduty,todaypeople;
    private List<users> usersList=new ArrayList<users>();
    private ImageView numbersicon;
    private TextView mynum;
    private ListView lv,shownote;
    private TextView userid,username,telshort;
    List<users> mylist=new ArrayList<users>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:
                List<users> str = (List<users>) msg.obj;
                MyAdapter adapter = new MyAdapter(MainFrame.this, R.layout.dutyadapter_item, str);
                lv.setAdapter(adapter);
                //Toast.makeText(MainActivity.this,"hello"+str,Toast.LENGTH_LONG).show();
                case 2:
                    MyDBHelper  dbHelper = new MyDBHelper(MainFrame.this, "mynotification.db", null, 2);
                    SQLiteDatabase db= dbHelper.getReadableDatabase();
                    Cursor cursor=db.query("mynotification", null, "isread=?", new String[]{"0"}, null, null, "notificationid desc");
                    int num=cursor.getCount();

                    if(num>0){
                        numbersicon.setVisibility(View.VISIBLE);
                        mynum.setVisibility(View.VISIBLE);
                        mynum.setText(num + "");

                    }else{
                        numbersicon.setVisibility(View.INVISIBLE);
                        mynum.setVisibility(View.INVISIBLE);
                    }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }, 0, 1000 * 60*5);



    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lv=(ListView)findViewById(R.id.dutylv);
      //  userid=(TextView)findViewById(R.id.showuid);
        username=(TextView)findViewById(R.id.showuname);
        numbersicon=(ImageView)findViewById(R.id.numbersicon);
        mynum=(TextView)findViewById(R.id.numbers);
        telshort=(TextView)findViewById(R.id.showtelshort);
        todayduty=(TextView)findViewById(R.id.todayduty);
        pdialog=ProgressDialog.show(MainFrame.this,"加载","正在加载数据...",true,false);
        todaypeople=(TextView)findViewById(R.id.todaypeople);
        gettodaypeople();
        getusers();
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
        GridView gridview = (GridView) findViewById(R.id.GridView);
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.note);
            map.put("ItemText", "通知");
            meumList.add(map);
        HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put("ItemImage", R.drawable.clock);
            map1.put("ItemText", "调班");
            meumList.add(map1);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("ItemImage", R.drawable.duty);
        map2.put("ItemText", "我的值班");
        meumList.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("ItemImage", R.drawable.writenote);
        map3.put("ItemText", "写通知");
        meumList.add(map3);
        HashMap<String, Object> map6 = new HashMap<String, Object>();
        map6.put("ItemImage", R.drawable.uc);
        map6.put("ItemText", "其它值班");
        meumList.add(map6);
        HashMap<String, Object> map7 = new HashMap<String, Object>();
        map7.put("ItemImage", R.drawable.uc);
        map7.put("ItemText", "建造中");
        meumList.add(map7);
        SimpleAdapter saMenuItem = new SimpleAdapter(this,
                meumList, //数据源
                R.layout.menuitem, //xml实现
                new String[]{"ItemImage","ItemText"}, //对应map的Key
                new int[]{R.id.ItemImage,R.id.ItemText});  //对应R的Id

//添加Item到网格中
        gridview.setAdapter(saMenuItem);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                Intent intent;
                                                switch (arg2){
                                                    case 0:
                                                        intent=new Intent(MainFrame.this,ShowNote.class);
                                                        startActivity(intent);
                                                        break;
                                                    case 2:
                                                        intent=new Intent(MainFrame.this,MyDuty.class);
                                                        startActivity(intent);
                                                        break;
                                                    case 3:
                                                        intent=new Intent(MainFrame.this,writenote.class);
                                                        startActivity(intent);
                                                        break;

                                                }

                                            }
                                        }
        );

    }
    public void getusers(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                getXml(2);
               // getXml(5);
            }
        }).start();
    }
    public void gettodaypeople(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getXml(3);
            }
        }).start();
    }
    public void getXml(int mychoice){

        String ServerUrl = "http://183.64.36.130:6666/webservice/WebService1.asmx";
        String soapAction="";
        switch(mychoice) {
            case 1:
                soapAction = "http://tempuri.org/" + "gettestinfo";
                break;








            case 2:
                soapAction = "http://tempuri.org/" + "gettodaydutybest";
                break;
            case 3:
                soapAction = "http://tempuri.org/" + "gettodaypeople";
                break;
            case 5:
                soapAction = "http://tempuri.org/" + "gettodaydutyweek";
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
       pdialog.dismiss();
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
            case 3:
                soap2 = "</soap:Envelope>";
                requestData = soap + mreakString + soap2;
            case 5:
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
            outStream.write (bytes);
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
                String username="";
                String telshort="";
                String pbdate="";
                String tel="";
                String pbrole="";
                String zyrs="";
                switch(mychoice) {
                    case 1:
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                                    break;
                                case XmlPullParser.START_TAG:// 开始元素事件
                                    String name = parser.getName();
                                    if (name.equalsIgnoreCase("id")) {
                                        userid = parser.nextText();
                                    } else if (name.equalsIgnoreCase("name")) {
                                        username = parser.nextText();

                                    } else if (name.equalsIgnoreCase("telshort")) {
                                        telshort = parser.nextText();

                                    }else if (name.equalsIgnoreCase("tel")) {
                                        tel = parser.nextText();

                                    }else if (name.equalsIgnoreCase("pbrole")) {
                                        pbrole = parser.nextText();

                                    }

                                    break;
                                case XmlPullParser.END_TAG:// 结束元素事件
                                    if (parser.getName().equalsIgnoreCase("Table")) {
                                        users myuserinfo = new users(userid, username, telshort,tel,pbrole);
                                        mylist.add(myuserinfo);


                                    }
                                    break;
                            }
                            eventType = parser.next();
                        }
                    case 2:
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                                    break;
                                case XmlPullParser.START_TAG:// 开始元素事件
                                    String name = parser.getName();
                                    if (name.equalsIgnoreCase("pbdate")) {
                                        pbdate = parser.nextText();
                                    } else if (name.equalsIgnoreCase("name")) {
                                        username = parser.nextText();

                                    } else if (name.equalsIgnoreCase("telshort")) {
                                        telshort = parser.nextText();

                                    }else if (name.equalsIgnoreCase("tel")) {
                                        tel = parser.nextText();

                                    }else if (name.equalsIgnoreCase("pbrole")) {
                                        pbrole = parser.nextText();

                                    }
                                    break;
                                case XmlPullParser.END_TAG:// 结束元素事件
                                    if (parser.getName().equalsIgnoreCase("Table")) {
                                        if(pbrole.equals("2") || pbrole.equals("3")) {
                                            users myuserinfo = new users("", username, telshort, tel, pbrole);

                                            mylist.add(myuserinfo);
                                        }


                                    }
                                    break;
                            }
                            todayduty.setText("今日值班("+pbdate+")");
                            eventType = parser.next();
                        }
                    case 3:
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                                    break;
                                case XmlPullParser.START_TAG:// 开始元素事件
                                    String name = parser.getName();
                                    if (name.equalsIgnoreCase("zyrs")) {
                                        zyrs = parser.nextText();
                                    }
                                    break;
                                case XmlPullParser.END_TAG:// 结束元素事件
                                    if (parser.getName().equalsIgnoreCase("Table")) {
                                        todaypeople.setText("在院人数:"+zyrs);


                                    }
                                    break;
                            }
                            todayduty.setText("今日值班("+pbdate+")");
                            eventType = parser.next();
                        }

                }
                inputStream.close();

                Message message=new Message();
                message.obj=mylist;
                message.what=1;
                handler.sendMessage(message);
            }
            catch (Exception e)
            {
                pdialog.dismiss();
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(this,NotificationService.class);
        stopService(intent);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(MainFrame.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainFrame.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
        timer.cancel();

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
