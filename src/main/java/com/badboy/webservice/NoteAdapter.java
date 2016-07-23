package com.badboy.webservice;

import android.content.Context;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by badboy on 3/21/2016.
 */
public class NoteAdapter extends ArrayAdapter<NotificationInfo> {
    private int resourceId;

    public NoteAdapter(Context context,int textViewResourceId, List<NotificationInfo> objects) {
        super(context,textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationInfo myuserinfo=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder=new ViewHolder();
            viewHolder.notetitle1=(TextView)view.findViewById(R.id.notetitle);
            viewHolder.sender1=(TextView)view.findViewById(R.id.sender);
            viewHolder.sendtime1=(TextView)view.findViewById(R.id.sendtime);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.notetitle1.setText(myuserinfo.getNotificationTitle());
        viewHolder.sender1.setText("发送者:" + myuserinfo.getSender());
        Log.d("data2",myuserinfo.getNotificationId()+" isread:"+myuserinfo.getisread());
        if(myuserinfo.getisread()==0) {
            viewHolder.sendtime1.setTextColor(android.graphics.Color.RED);
            viewHolder.sendtime1.setText("未读 "+myuserinfo.getSendtime());
        }else{
            viewHolder.sendtime1.setTextColor(Color.GRAY);
            viewHolder.sendtime1.setText("已读 "+myuserinfo.getSendtime());
        }
        return view;
    }

class ViewHolder {
    TextView notetitle1;
    TextView sender1;
    TextView sendtime1;
}
}