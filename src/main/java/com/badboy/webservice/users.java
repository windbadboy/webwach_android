package com.badboy.webservice;

import android.widget.TextView;

/**
 * Created by 聪 on 2016/3/16.
 */
public class users {
    private String userid;
    private String username;
    private String telshort;
    private String tel;
    private String pbrole;

    public users(String userid,String username,String telshort,String tel,String pbrole){
        this.userid=userid;
        this.username=username;
        this.telshort=telshort;
        this.tel=tel;
        this.pbrole=pbrole;
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
    public String getTel(){
        return tel;
    }
    public String getPbrole(){return pbrole;}

}


