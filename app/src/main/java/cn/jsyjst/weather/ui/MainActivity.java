package cn.jsyjst.weather.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.fragment.WeatherFragment;
import cn.jsyjst.weather.services.AutoUpdateService;
import cn.jsyjst.weather.R;


/**
 * 显示天气信息的主界面
 */
public class MainActivity extends AppCompatActivity {

    private List<WeatherFragment> fragmentList = new ArrayList<>();
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private ViewPager pager;
    /**
     * 总页面个数
     */
    private  int allPageNum;
    /**
     * 用来接受其他活动传过来的值
     */
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 申请权限
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            /**
             * 判断数据库中有没数据，如果没有则跳转到添加城市的界面
             * 有则加载城市天气界面
             */
            allPageNum=isHasCity();
            if (allPageNum!=0) {
                initViewPage();
                /**
                 * 小部件点击后执行
                 */
                Intent intent = getIntent();
                int index = intent.getIntExtra("number", -1);
                pager.setCurrentItem(index);
            } else {
                /**
                 * 跳转到添加城市的界面
                 */
                intentAddCityActivity();
            }
        }
    }

    /**
     * 用viewPage动态加载fragment
     */
    private void initViewPage() {

        allPageNum=isHasCity();
        if(allPageNum==0){
            intentAddCityActivity();
        }
        /**
         * 记录城市的index
         */
        int pageNum=0;
        /**
         * 从数据库中中读取已添加城市
         */
        dbHelper = new MyDatabaseHelper(this, "weather.db", null, 2);
        db = dbHelper.getWritableDatabase();
        cursor = db.query("City", null, null, null, null, null, null, null);

        fragmentList.clear();
        while (cursor.moveToNext()) {
            pageNum++;
            String cityName = cursor.getString(cursor.getColumnIndex("name"));
            WeatherFragment fragment = new WeatherFragment();
            /**
             * 传值给fragment
             */
            fragment.setCityName(cityName);
            fragment.setNum(pageNum);
            fragment.setAllNum(allPageNum);
            fragmentList.add(fragment);
        }
        db.close();
        cursor.close();
        pager = (ViewPager) findViewById(R.id.page_city);
        /**
         * 适配器,得用FragmentStatePagerAdapter，因为是实现多个动态加载的fragment
         */
        pager.setAdapter(new FragmentStatePagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragmentList.get(arg0);
            }

        });

    }

    /**
     * 动态设置位置权限,一开始数据库中没有数据,即还没添加城市，所以跳转到添加城市的界面
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentAddCityActivity();
                } else {
                    finish();
                }
                break;
            default:
        }

    }

    /**
     * 跳转到选择添加城市的界面
     */
    private void intentAddCityActivity() {
        Intent intent = new Intent(MainActivity.this, AddCityActivity.class);
        startActivity(intent);
    }

    /**
     * 获得数据库中City表的总列数，对应了已添加的城市在viewPage的位置
     *
     * @return
     */
    private int isHasCity() {
        int i=0;
        dbHelper = new MyDatabaseHelper(this, "weather.db", null, 2);
        db = dbHelper.getWritableDatabase();
        cursor = db.query("City", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            if (name != null) {
               i++;
            }
        }
        cursor.close();
        db.close();
        return i;
    }

    /**
     * 如果没有重写这个方法，假如活动方向为A->B->C->A,则A接受不到C传过来的值
     *
     * @param intent
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    /**
     * 当管理城市和添加城市的活动点击后会执行这个方法
     * 如果管理城市点击城市列表时i为该城市在数据库中的位置，故会执行 pager.setCurrentItem(i)，跳转到对应城市的页面;
     * 如果是添加城市点击后，i=-2。
     * 其他调用这个 方法时，i=-3或i=-1.
     */
    @Override
    public void onRestart() {
        super.onRestart();

        Intent intent = getIntent();
        i = intent.getIntExtra("number", -3);
        initViewPage();
        if (i != -1 && i != -3) {
            if (i == -2) {
                pager.setCurrentItem(fragmentList.size() - 1);
            } else {
                pager.setCurrentItem(i);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}