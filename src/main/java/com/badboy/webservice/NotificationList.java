package com.badboy.webservice;

/**
 * Created by badboy on 3/17/2016.
 */
public class NotificationList {
    private String userid;
    private String title;
    private String body;
    private String sendtime;
    private String username;
    public NotificationList(String userid,String title,String body,String sendtime,String username){
        this.userid=userid;
        this.title=title;
        this.body=body;
        this.sendtime=sendtime;
        this.username=username;
    }
    public String getid(){
        return userid;
    }
    public String gettitle(){
        return title;
    }
    public String getBody(){
        return body;
    }
    public String getSendtime() {return sendtime;}
    public String getusername(){return username;}
}