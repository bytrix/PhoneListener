package com.example.jack.phonelistener;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jack.phonelistener.bean.Calllog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jack on 17-7-2.
 */

public class RecordingAdapter extends BaseAdapter {

    private Context context;
    private List list;
    private int selectItem = -1;

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public RecordingAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Calllog calllog = (Calllog) list.get(position);
        convertView = View.inflate(context, R.layout.adapter_recording, null);
//        String filename = calllog.getFile();
//        String[] split = filename.replace(".3gp", "").split("_");
        String phone = calllog.getPhone();
        long time = calllog.getCreateTime();
        long duration = calllog.getDuration();
        TextView tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
        tv_phone.setText(phone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_time.setText(simpleDateFormat.format(new Date(time)));
        tv_duration.setText(String.valueOf(duration/1000) + "秒");
        if (this.selectItem == position) {
//            tv_phone.setTextColor(Color.RED);
//            convertView.setBackgroundColor(Color.LTGRAY);
            tv_phone.setTypeface(null, Typeface.BOLD);
            tv_phone.setPadding(16, 0, 0, 0);
        } else {
//            tv_phone.setTextColor(Color.BLACK);
//            convertView.setBackgroundColor(Color.TRANSPARENT);
//            tv_phone.setTypeface(null, Typeface.NORMAL);
        }
        return convertView;
    }


}