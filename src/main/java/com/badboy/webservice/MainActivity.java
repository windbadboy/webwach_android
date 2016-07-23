package com.badboy.webservice;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private List<String> srvname;
    final String myurl="http://183.64.36.130:8088/myversion.xml";
    public void checkversion(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                srvname=getxmlcontent(myurl);
                Message msg=new Message();
                msg.obj=srvname;
                msg.what=2;
                myhandler.sendMessage(msg);

            }
        }).start();


    }
    public List<String> getxmlcontent(String myurl){
String srvversion="";
        String description="";
        List<String> mylist=new ArrayList<String>();
        try {
          URL  url = new URL(myurl);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(6000);
            con.setRequestMethod("POST");
            InputStream inputStream=con.getInputStream();

            if(con.getResponseCode()==200) {
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(inputStream, "UTF-8");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String name = parser.getName();
                                if (name.equalsIgnoreCase("version")) {
                                    srvversion = parser.nextText();
                                }else if (name.equalsIgnoreCase("description")) {
                                    description = parser.nextText();
                                }
                                break;
                                case XmlPullParser.END_TAG:
                                    if (parser.getName().equalsIgnoreCase("app")) {

                                        mylist.add(srvversion);
                                        mylist.add(description);


                                    }
                                    break;

                        }
                        eventType = parser.next();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("data2",e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("data2", e.toString());
        }

        return mylist;
    }

    public String getVersionName() throws Exception{
        PackageManager packageManager=getPackageManager();
        PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
        return packageInfo.versionName;
    }
    public String myloginid;
    private ProgressDialog pdialog;
    private Handler myhandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                userinfo myuserinfo = (userinfo) msg.obj;
                if (!myuserinfo.getnetwork()) {
                    if (myuserinfo.getok()) {
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("username", myuserinfo.getUsername());
                        editor.putString("roleid", myuserinfo.getRoleid());
                        editor.commit();
                        Toast.makeText(MainActivity.this, "欢迎登录," + myuserinfo.getUsername() + "(" + myuserinfo.getuserid() + ")", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, MainFrame.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "输入信息有误，无法登录。", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "网络连接错误,请稍后重试.", Toast.LENGTH_LONG).show();
                }
                case 2:
                    try {
                        String verName=getVersionName();
                        List<String> srvVerName=(List<String>)msg.obj;
                        if(!TextUtils.isEmpty(srvVerName.get(0))){
                            if(!verName.equals(srvVerName.get(0))){
                                showUpdataDialog(srvVerName.get(0),srvVerName.get(1));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

        }
    };
    private Button login;
    private EditText userid,pwd;
    private CheckBox remember_me;
    public void showUpdataDialog(String version,String description){
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("发现新版本:"+version);
        builer.setMessage(description);
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                        downLoadApk();



            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("再说", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载新版本");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer("http://183.64.36.130:8088/webwatch.apk", pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}.start();
    }
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了
        startActivity(intent);
    }
    public File getFileFromServer(String tempurl,ProgressDialog pd){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), "webwatch.apk");

            try {
                URL url = new URL(tempurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                pd.setMax(conn.getContentLength());
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    pd.setProgress(total);
                }
                fos.close();
                bis.close();
                is.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;
        }
        else{
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_main);
        checkversion();
        userid=(EditText)findViewById(R.id.userid);
        pwd=(EditText)findViewById(R.id.pwd);
        remember_me=(CheckBox)findViewById(R.id.remember_me);
        myloginid=userid.getText().toString();

        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remember_me.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("userid", userid.getText().toString());
                    try {
                        editor.putString("pwd",AES.encrypt("39393939",pwd.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editor.putBoolean("ischecked", remember_me.isChecked());
                    editor.commit();
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("userid", userid.getText().toString());
                    editor.putString("pwd", "");
                    editor.putBoolean("ischecked", false);
                    editor.commit();
                }
                pdialog=ProgressDialog.show(MainActivity.this,"登录","正在登录",true,false);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userinfo isok=checklogin("checklogin");
                        Message message=new Message();
                        message.obj=isok;
                        message.what=1;
                        myhandler.sendMessage(message);


                    }
                }).start();
            }
        });
        checkrememberme();
    }
    public void checkrememberme(){
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        if(pref.getBoolean("ischecked",false)){
            remember_me.setChecked(true);
            userid.setText(pref.getString("userid", ""));
            String password=pref.getString("pwd", "");

            try {
                password=AES.decrypt("39393939",password);
                pwd.setText(password);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public userinfo checklogin(String method){
        String serverUrl="http://183.64.36.130:6666/webservice/WebService1.asmx";
        //xml header
        String soapAction="http://tempuri.org/"+method;
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body />";
        String tps,vps,ts;
        String mreakString="";
        mreakString = "<" + method + " xmlns=\"http://tempuri.org/\">";
        tps="userid";
        vps=userid.getText().toString();
        ts = "<" + tps + ">" + vps + "</" + tps + ">";
        mreakString = mreakString + ts;
        tps="pwd";
        vps=getMD5(pwd.getText().toString());
        ts = "<" + tps + ">" + vps + "</" + tps + ">";
        mreakString = mreakString + ts;
        mreakString = mreakString + "</" + method + ">";
        String soap2 = "</soap:Envelope>";
        String requestData = soap + mreakString + soap2;
        Log.d("user",requestData);
        String isok="";
        String userid="";
        String username="";
        String roleid="";
        boolean networkerr=false;
        try{
            URL url=new URL(serverUrl);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            byte[] bytes=requestData.getBytes("utf-8");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(6000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
            con.setRequestProperty("SOAPAction", soapAction);
            con.setRequestProperty("Content-Length", "" + bytes.length);
            OutputStream outputStream=con.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            InputStream inputStream=con.getInputStream();

            if(con.getResponseCode()==200) {
                pdialog.dismiss();
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(inputStream, "UTF-8");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String name = parser.getName();
                                if (name.equalsIgnoreCase("isok")) {
                                    isok = parser.nextText();

                                } else if (name.equalsIgnoreCase("userid")) {
                                    userid = parser.nextText();
                                } else if (name.equalsIgnoreCase("username")) {
                                    username = parser.nextText();
                                } else if (name.equalsIgnoreCase("roleid")) {
                                    roleid = parser.nextText();
                                }

                        }
                        eventType = parser.next();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                networkerr=true;
                pdialog.dismiss();
            }
        }catch (Exception e){
            networkerr=true;
            pdialog.dismiss();
            e.printStackTrace();
        }
        boolean mybl1;

        if(Boolean.valueOf(isok).booleanValue()){

            mybl1=true;
        }else{
            mybl1=false;
        }
        return new userinfo(mybl1,userid,username,networkerr,roleid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public String getMD5(String info)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1)
                {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                }
                else
                {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        }
        catch (Exception e)
        {
            return "";
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(this,NotificationService.class);
        stopService(intent);
    }
}