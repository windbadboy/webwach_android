package com.badboy.webservice;

/**
 * Created by badboy on 3/21/2016.
 */
public class NotificationInfo {
    private String notificationTitle;
    private String sender;
    private String sendtime;
    private String notificationId;
    private String notificationBody;
    private String notificationType;
    private String userid;
    private int isread;
    public NotificationInfo(String notificationTitle,String sender,String sendtime,String notificationId,String notificationBody,String notificationType,String userid,int isread){
        this.notificationTitle=notificationTitle;
        this.sender=sender;
        this.sendtime=sendtime;
        this.notificationId=notificationId;
        this.notificationBody=notificationBody;
        this.notificationType=notificationType;
        this.userid=userid;
        this.isread=isread;
    }
    public String getNotificationTitle(){return notificationTitle;}
    public String getSender(){return sender;}
    public String getSendtime(){return sendtime;}
    public String getNotificationId(){return notificationId;}
    public String getNotificationBody(){return notificationBody;}
    public String getNotificationType(){return notificationType;}
    public String getUserid(){return userid;}
    public int getisread(){return isread;}

}
