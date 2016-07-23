package com.badboy.webservice;

import android.widget.TextView;

/**
 * Created by 聪 on 2016/3/16.
 */
public class users {
    private String userid;
    private String username;
    private String telshort;
    public users(String userid,String username,String telshort){
        this.userid=userid;
        this.username=username;
        this.telshort=telshort;
    }
    public String getid(){
        return userid;
    }
    public String getname(){
        return username;
    }
    public String getTelshort(){
        return telshort;
    }
}


