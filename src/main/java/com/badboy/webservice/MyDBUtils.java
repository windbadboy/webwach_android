package com.badboy.webservice;

import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by badboy on 3/22/2016.
 */
public class MyDBUtils {
    private ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayList<String> brrayList = new ArrayList<String>();
    public InputStream getchecklist(String serverAddr, String methodName, int mychoice, ArrayList<String> myparameters,ArrayList<String> myvalues) {
        List<users> mylist = new ArrayList<users>();
        String ServerUrl = "http://" + serverAddr + "/webservice/WebService1.asmx";
        String soapAction = "";
        switch (mychoice) {
            case 1:
                soapAction = "http://tempuri.org/" + methodName;
                break;


            case 2:
                soapAction = "http://tempuri.org/" + methodName;
                break;
            default:
                break;
        }
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body />";
        String soap2;
        String tps,vps,ts;
        String mreakString = "";
        String requestData = "";
        Log.d("data",soap+methodName+mychoice);
        switch (mychoice) {
            case 1:
                mreakString = "<"+methodName+" xmlns=\"http://tempuri.org/\">";
                for (int i = 0; i < myparameters.size(); i++) {
                    tps = myparameters.get(i).toString();
                    //设置该方法的参数为.net webService中的参数名称
                    vps = myvalues.get(i).toString();
                    ts = "<" + tps + ">" + vps + "</" + tps + ">";
                    mreakString = mreakString + ts;
                }
                mreakString = mreakString + "</"+methodName+">";
                soap2 = "</soap:Envelope>";
                requestData = soap + mreakString + soap2;
                break;
            case 2:
                soap2 = "</soap:Envelope>";
                requestData = soap + mreakString + soap2;
                break;
        }
        Log.d("data",requestData);
        try {
            URL url = new URL(ServerUrl); //指定服务器地址
            HttpURLConnection con = (HttpURLConnection) url.openConnection();//打开链接
            byte[] bytes = requestData.getBytes("utf-8"); //指定编码格式，可以解决中文乱码问题
            con.setDoInput(true); //指定该链接是否可以输入
            con.setDoOutput(true); //指定该链接是否可以输出
            con.setUseCaches(false); //指定该链接是否只用caches
            con.setConnectTimeout(6000); // 设置超时时间
            con.setRequestMethod("POST"); //指定发送方法名，包括Post和Get。
            con.setRequestProperty("Content-Type", "text/xml;charset=utf-8"); //设置（发送的）内容类型
            con.setRequestProperty("SOAPAction", soapAction); //指定soapAction
            con.setRequestProperty("Content-Length", "" + bytes.length); //指定内容长度

            //发送数据
            OutputStream outStream = con.getOutputStream();
            outStream.write(bytes);
            outStream.flush();
            outStream.close();

            //获取数据
            InputStream inputStream = con.getInputStream();
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
