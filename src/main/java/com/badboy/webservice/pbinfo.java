package com.badboy.webservice;

/**
 * Created by badboy on 3/23/2016.
 */
public class pbinfo {

    private String username;
    private String rolename;
    private String pbdate;
    private String weekday;

    public pbinfo(String username,String rolename,String pbdate,String weekday){
        this.rolename=rolename;
        this.username=username;
        this.pbdate=pbdate;
        this.weekday=weekday;

    }

    public String getRolename(){
        return rolename;
    }
    public String getUsername(){
        return username;
    }
    public String getPbdate(){
        return pbdate;
    }
    public String getWeekday(){return weekday;}
}
