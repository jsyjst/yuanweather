package cn.jsyjst.weather.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.adapter.CityAdapter;
import cn.jsyjst.weather.db.CityCrud;
import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.entity.City;
import cn.jsyjst.weather.widget.DelListView;


/**
 * 管理城市的功能，即将已经添加的城市显示出来，并可以删除已添加的城市
 */
public class ManageCityActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addBtn;
    private Button backBtn;
    private List<City> cityList = new ArrayList<>();
    private CityCrud cityCrud;
    private CityAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);

        /**
         * 初始化各控件
         */
        addBtn = (Button) findViewById(R.id.btn_manage_add);
        backBtn = (Button) findViewById(R.id.btn_manage_back);
        cityCrud = new CityCrud(this, "weather.db", null, 2);
        cityList = cityCrud.getCityList();
        adapter = new CityAdapter(this, R.layout.city_listview_item, cityList);
        DelListView listView = (DelListView) findViewById(R.id.lv_manage_city);
        listView.setAdapter(adapter);
        /**
         * 删除功能
         */
        listView.setDelButtonClickListener(new DelListView.DelButtonClickListener() {
            @Override
            public void clickHappened(int position) {
                City city = cityList.get(position);
                String name = city.getmCityName();
                cityCrud.isHasCity(name);
                adapter.remove(adapter.getItem(position));
                /**
                 * 从本地读取当地城市
                 */
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(ManageCityActivity.this);
                String locationName=prefs.getString("locationCity",null);
                /**
                 * 当删除当地城市后，发送广播提示小部件更新信息
                 */
                if(name.equals(locationName)){
                    /**
                     * 发送广播，令小部件更新
                     */
                    Intent broadIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
                    sendBroadcast(broadIntent);
                }
                /**
                 * 如果添加城市的列表为空，跳转到添加城市的界面
                 */
                if (!isHasCity()) {
                    Intent intent = new Intent(ManageCityActivity.this, AddCityActivity.class);
                    startActivity(intent);
                }
            }
        });


        /**
         * 点击事件
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = cityList.get(i).getmCityName();
                Intent intent = new Intent(ManageCityActivity.this, MainActivity.class);
                intent.putExtra("number", i);
                startActivity(intent);
            }
        });
        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_manage_add:
                Intent addIntent = new Intent(ManageCityActivity.this, AddCityActivity.class);
                startActivity(addIntent);
                break;
            case R.id.btn_manage_back:
                Intent intent = new Intent(ManageCityActivity.this, MainActivity.class);
                intent.putExtra("number", -1);
                startActivity(intent);
        }
    }

    /**
     * 判断添加城市列表是否为空
     * @return
     */
    private boolean isHasCity() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ManageCityActivity.this, "weather.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name != null) {
                db.close();
                cursor.close();
                return true;
            }
        }
        db.close();
        cursor.close();
        return false;
    }
    @Override
    public void onBackPressed(){
        Intent intent=new Intent(ManageCityActivity.this,MainActivity.class);
        intent.putExtra("number",-1);
        startActivity(intent);
    }
}
