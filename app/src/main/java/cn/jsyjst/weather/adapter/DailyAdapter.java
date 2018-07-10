package cn.jsyjst.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.entity.DailyWeather;

/**
 * Created by 残渊 on 2018/5/18.
 */


/**
 * 显示多日天气预报的listView适配器
 */
public class DailyAdapter extends ArrayAdapter<DailyWeather> {

    private int resourceId;
    private  ViewHolder viewHolder;

    public DailyAdapter(Context context, int textViewResourceId, List<DailyWeather> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DailyWeather dailyWeather=getItem(position);
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.dateTv=view.findViewById(R.id.tv_daily_date);
            viewHolder.textTv=view.findViewById(R.id.tv_daily_text);
            viewHolder.highLowTv=view.findViewById(R.id.tv_daily_highLow);
            viewHolder.codeIv=view.findViewById(R.id.iv_daily_weather);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.dateTv.setText(dailyWeather.getDate());
        viewHolder.textTv.setText(dailyWeather.getText());
        viewHolder.highLowTv.setText(dailyWeather.getHighLowTemperature());
        setWeatherImage(dailyWeather.getCode());
        return view;
    }

    class ViewHolder{
        TextView dateTv;
        TextView textTv;
        TextView highLowTv;
        ImageView codeIv;
    }


    /***
     * 根据code的值来为天气配图
     * @param code 天气现象代码
     */
    public  void setWeatherImage(String code){
        if (code.equals("0")) {
             viewHolder.codeIv.setImageResource(R.drawable.iv_0);
        }else if(code.equals("1")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_1);
        }else if(code.equals("2")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_2);
        }else if(code.equals("3")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_3);
        }else if(code.equals("4")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_4);
        }else if(code.equals("5")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_5);
        }else if(code.equals("6")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_6);
        }else if(code.equals("7")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_7);
        }else if(code.equals("8")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_8);
        }else if(code.equals("9")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_9);
        }else if(code.equals("10")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_10);
        }else if(code.equals("11")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_11);
        }else if(code.equals("12")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_12);
        }else if(code.equals("13")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_13);
        }else if(code.equals("14")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_14);
        }else if(code.equals("15")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_15);
        }else if(code.equals("16")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_16);
        }else if(code.equals("17")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_17);
        }else if(code.equals("18")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_18);
        }else if(code.equals("19")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_19);
        }else if(code.equals("30")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_30);
        }else if(code.equals("31")){
            viewHolder.codeIv.setImageResource(R.drawable.iv_31);
        }
    }
}
