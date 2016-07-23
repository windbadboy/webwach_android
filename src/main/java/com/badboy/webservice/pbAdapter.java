package com.badboy.webservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by badboy on 3/23/2016.
 */
public class pbAdapter extends ArrayAdapter<pbinfo> {
    private int resourceId;

    public pbAdapter(Context context,int textViewResourceId, List<pbinfo> objects) {
        super(context,textViewResourceId, objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        pbinfo myuserinfo=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder=new ViewHolder();
            viewHolder.username=(TextView)view.findViewById(R.id.username);
            viewHolder.rolename=(TextView)view.findViewById(R.id.rolename);
            viewHolder.pbdate=(TextView)view.findViewById(R.id.pbdate);
            viewHolder.weekday=(TextView)view.findViewById(R.id.weekday);
            view.setTag(viewHolder);
        }
        else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.username.setText(myuserinfo.getUsername());
        viewHolder.rolename.setText(myuserinfo.getRolename());
        viewHolder.pbdate.setText(myuserinfo.getPbdate());
        viewHolder.weekday.setText(myuserinfo.getWeekday());
        return view;
    }
    class ViewHolder {
        TextView username;
        TextView rolename;
        TextView pbdate;
        TextView weekday;
    }
}
