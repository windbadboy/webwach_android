package com.badboy.webservice;

/**
 * Created by badboy on 3/14/2016.
 */
public class userinfo {
    private boolean isok;
    private String userid;
    private String username;
    private boolean networkerr;
    private String roleid;

    public userinfo(boolean isok,String userid,String username,Boolean networkerr,String roleid){
        this.isok=isok;
        this.userid=userid;
        this.username=username;
        this.networkerr=networkerr;
        this.roleid=roleid;
    }
    public boolean getok(){
        return isok;
    }
    public String getuserid(){
        return userid;
    }
    public String getUsername(){
        return username;
    }
    public Boolean getnetwork(){
        return networkerr;
    }
    public String getRoleid(){return roleid;}
}