package cn.jsyjst.weather.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


import cn.jsyjst.weather.R;
import cn.jsyjst.weather.db.CityCrud;
import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.ui.MainActivity;

/**
 * Created by 残渊 on 2018/5/19.
 */


/**
 * 推荐城市recyclerView适配器
 */
public class RecommendCityAdapter extends RecyclerView.Adapter<RecommendCityAdapter.ViewHolder> {

    private List<String> cityList;
    private MyDatabaseHelper dbHelper;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityTv;

        public ViewHolder(View view) {
            super(view);
            cityTv = view.findViewById(R.id.tv_recommend_city);
        }
    }

    public RecommendCityAdapter(List<String> cityList) {
        this.cityList = cityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_recycler_item, parent, false);
        /**
         * 获得上下文，从而对数据库进行操作
         */
        mContext = parent.getContext();
        final ViewHolder holder = new ViewHolder(view);
        holder.cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                String cityName = cityList.get(position);

                /**
                 * 点击时保存在数据库中
                 */
                CityCrud cityCrud=new CityCrud(view.getContext(),"weather.db",null,2);
                cityCrud.create(cityName,null,null,null);

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("city", cityName);
                intent.putExtra("number", -2);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        boolean isCityTouch = false;
        holder.cityTv.setText(cityList.get(position));
        /**
         * 从数据库中读取城市
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String locationCity = prefs.getString("locationCity", null);
        dbHelper = new MyDatabaseHelper(mContext, "weather.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            /**
             * 当前位置的判断比较特殊，数据库中时没有这个name的，利用本地读取当地城市来判断
             */
            if (locationCity.equals(cursor.getString(cursor.getColumnIndex("name"))) && position == 0) {
                isCityTouch = true;
            } else {
                if (cityList.get(position).equals(cursor.getString(cursor.getColumnIndex("name")))) {
                    isCityTouch = true;
                    break;
                }
            }
        }
        db.close();
        cursor.close();
        /**
         * 如果数据库中有这个城市则显示不可点击
         */
        if (isCityTouch) {
            holder.cityTv.setEnabled(false);
            holder.cityTv.setTextColor(mContext.getResources().getColor(R.color.grey));
        } else {
            holder.cityTv.setEnabled(true);
            holder.cityTv.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}
