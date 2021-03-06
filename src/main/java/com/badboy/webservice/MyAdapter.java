package com.badboy.webservice;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 聪 on 2016/3/16.
 */
public class MyAdapter extends ArrayAdapter<users> {
    private int resourceId;
    private TextView a;
    public MyAdapter(Context context,int textViewResourceId, List<users> objects) {
        super(context,textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        users myuserinfo=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder=new ViewHolder();
          //  viewHolder.userid=(TextView)view.findViewById(R.id.showuid);
            viewHolder.username=(TextView)view.findViewById(R.id.showuname);
            viewHolder.telshort=(TextView)view.findViewById(R.id.showtelshort);
            viewHolder.pbimage=(ImageView)view.findViewById(R.id.showimage);
            view.setTag(viewHolder);
        }
        else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
      //  viewHolder.userid.setText(myuserinfo.getid());
        viewHolder.username.setText(myuserinfo.getname());
        viewHolder.telshort.setText(myuserinfo.getTelshort());
        Log.d("data",myuserinfo.getPbrole());
        if(myuserinfo.getPbrole().equals("2")) {
            viewHolder.pbimage.setImageResource(R.drawable.xzzb);
        }else if(myuserinfo.getPbrole().equals("3"))
        {
            viewHolder.pbimage.setImageResource(R.drawable.lczblogo);
        }
        return view;
    }
    class ViewHolder {
        TextView userid;
        TextView username;
        TextView telshort;
        TextView tel;
        TextView pbrole;
        ImageView pbimage;
    }
}
