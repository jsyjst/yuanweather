package cn.jsyjst.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.weather.R;


/**
 * Created by 残渊 on 2018/5/20.
 */


/**
 * 查找城市的ListView适配器
 */
public class FindCityAdapter extends ArrayAdapter<String> {
    private int resourceId;

    public FindCityAdapter(Context context, int textViewResourceId, List<String> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String find=getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(parent.getContext()).inflate(resourceId,parent,false);
            holder=new ViewHolder();
            holder.findTv=view.findViewById(R.id.tv_find_city);
            view.setTag(holder);
        }else{
            view=convertView;
            holder=(ViewHolder)view.getTag();
        }
        holder.findTv.setText(find);
        return view;
    }

    class ViewHolder{
        TextView findTv;
    }
}
