package cn.jsyjst.weather.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.entity.City;


/**
 * Created by 残渊 on 2018/5/19.
 */


/**
 * 显示已添加城市的ListView适配器
 */
public class CityAdapter extends ArrayAdapter<City> {

    private int resourceId;

    public CityAdapter(Context context, int textViewResourceId, List<City> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        City city = getItem(position);
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.nameTv = view.findViewById(R.id.tv_manage_city_name);
            holder.temperatureTv = view.findViewById(R.id.tv_manage_city_temperature);
            holder.textTv = view.findViewById(R.id.tv_manage_city_text);
            holder.locationIv = view.findViewById(R.id.iv_manage_city_location);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.nameTv.setText(city.getmCityName());
        holder.temperatureTv.setText(city.getmTemperature());
        holder.textTv.setText(city.getmText());

        /**
         * 判断城市的名字与本地的位置是否一样，一样的话就显示当地位置的照片
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        String locationCity = prefs.getString("locationCity", null);
        if (locationCity != null) {
            if (city.getmCityName().equals(locationCity)) {
                holder.locationIv.setVisibility(View.VISIBLE);
            }else{
                holder.locationIv.setVisibility(View.GONE);
            }
        }
        return view;
    }

    class ViewHolder {
        TextView nameTv;
        TextView temperatureTv;
        TextView textTv;
        ImageView locationIv;
    }
}
