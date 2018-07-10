package cn.jsyjst.weather.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.entity.HoursWeather;
import cn.jsyjst.weather.ui.MainActivity;

/**
 * Created by 残渊 on 2018/5/19.
 */


/**
 * 显示逐时天气的RecyclerView适配器
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.ViewHolder> {

    private List<HoursWeather> mHourList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTv;
        TextView temperatureTv;
        ImageView codeIv;

        public ViewHolder(View view) {
            super(view);
            temperatureTv = view.findViewById(R.id.tv_hour_temperature);
            timeTv = view.findViewById(R.id.tv_hour_time);
            codeIv = view.findViewById(R.id.iv_hour_code);
        }
    }

    public HourAdapter(List<HoursWeather> list) {
        mHourList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hour_recyclerview_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        HoursWeather hoursWeather = mHourList.get(position);
        viewHolder.timeTv.setText(hoursWeather.getmTime());
        viewHolder.temperatureTv.setText(hoursWeather.getmTemperature());
        String code=hoursWeather.getmCode();

        /**
         * 根据code来为天气配图
         */
        if (code.equals("0")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_0);
        } else if (code.equals("1")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_1);
        } else if (code.equals("2")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_2);
        } else if (code.equals("3")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_3);
        } else if (code.equals("4")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_4);
        } else if (code.equals("5")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_5);
        } else if (code.equals("6")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_6);
        } else if (code.equals("7")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_7);
        } else if (code.equals("8")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_8);
        } else if (code.equals("9")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_9);
        } else if (code.equals("10")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_10);
        } else if (code.equals("11")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_11);
        } else if (code.equals("12")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_12);
        } else if (code.equals("13")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_13);
        } else if (code.equals("14")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_14);
        } else if (code.equals("15")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_15);
        } else if (code.equals("16")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_16);
        } else if (code.equals("17")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_17);
        } else if (code.equals("18")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_18);
        } else if (code.equals("19")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_19);
        } else if (code.equals("30")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_30);
        } else if (code.equals("31")) {
            viewHolder.codeIv.setImageResource(R.drawable.iv_31);
        }

    }

    @Override
    public int getItemCount() {
        return mHourList.size();
    }
}

