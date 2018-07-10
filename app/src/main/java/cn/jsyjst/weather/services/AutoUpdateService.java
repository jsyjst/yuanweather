package cn.jsyjst.weather.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import cn.jsyjst.weather.utils.JsonPrase;
import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.utils.HttpUtil;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static cn.jsyjst.weather.config.ApiInterface.DAILY;
import static cn.jsyjst.weather.config.ApiInterface.HOUR;
import static cn.jsyjst.weather.config.ApiInterface.LIFE;
import static cn.jsyjst.weather.config.ApiInterface.LOCATION;
import static cn.jsyjst.weather.config.ApiInterface.NOW;
import static cn.jsyjst.weather.config.ApiInterface.SHARE;

/**
 * Created by 残渊 on 2018/5/20.
 */


/**
 * 自动更新功能
 */
public class AutoUpdateService extends Service {

    private  AlarmManager manager;
    private  PendingIntent pi;
    private int stop;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        /**
         * 更新数据库中有的城市
         */
        Log.d("AutoService","-------update");
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(AutoUpdateService.this, "weather.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String cityName=cursor.getString(cursor.getColumnIndex("name"));
            if (cityName.equals("当前位置")) {
                findLocationCity();
            } else {
                weatherRequest(cityName, "now");
                weatherRequest(cityName, "hour");
                weatherRequest(cityName, "daily");
                weatherRequest(cityName, "life");
                weatherRequest(cityName, "share");
            }
        }
        /**
         * 定时任务
         */
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        /**
         * 根据设置中的时间来确定自动更新的时间
         */
        String t=intent.getStringExtra("time");
        Log.d("AutoService", t);
        int anHour = Integer.valueOf(t)*60*60 * 1000;
        long time = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        /**
         * 定时器启动时传值给本身
         */
        i.putExtra("time",t);
        pi = PendingIntent.getService(this, 0, i,  FLAG_CANCEL_CURRENT);
        manager.cancel(pi);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void findLocationCity() {
        final Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                double[] data = (double[]) msg.obj;
                getLocation(String.valueOf(data[0]), String.valueOf(data[1]));
            }

        };
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        new Thread() {
            @Override
            public void run() {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {

                    double latitude = location.getLatitude(); // 纬度
                    double longitude = location.getLongitude(); //  经度
                    double[] data = {latitude, longitude};
                    Message msg = handler.obtainMessage();
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void getLocation(final String latitude, String longitude) {
        String address =LOCATION + latitude + ":" + longitude;
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                String cityName = JsonPrase.handLocationCityResponse(response);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("locationCity", cityName);
                editor.apply();

                weatherRequest(cityName, "now");
                weatherRequest(cityName, "hour");
                weatherRequest(cityName, "daily");
                weatherRequest(cityName, "life");
                weatherRequest(cityName, "share");
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void weatherRequest(final String cityName, final String type) {
        String weatherUrl = "";
        if (type.equals("now")) {
            weatherUrl = NOW + cityName;
        } else if (type.equals("share")) {
            weatherUrl =  SHARE+cityName + "今天天气怎么样？";
        } else if (type.equals("hour")) {
            weatherUrl =HOUR + cityName ;
        } else if (type.equals("daily")) {
            weatherUrl = DAILY+ cityName ;
        } else if (type.equals("life")) {
            weatherUrl = LIFE+ cityName ;
        }
        HttpUtil.sendHttpRequest(weatherUrl, new HttpUtil.HttpCallbackListener() {
            SharedPreferences.Editor editor = PreferenceManager.
                    getDefaultSharedPreferences(AutoUpdateService.this).edit();

            @Override
            public void onFinish(final String response) {
                if (type.equals("now")) {
                    /**
                     * 保存到本地
                     */
                    editor.putString(cityName + "Now", response);
                    editor.apply();

                } else if (type.equals("share")) {

                    /**
                     * 保存到本地
                     */
                    editor.putString(cityName + "Share", response);
                    editor.apply();
                } else if (type.equals("daily")) {

                    /**
                     * 保存到本地
                     */
                    editor.putString(cityName + "Daily", response);
                    editor.apply();
                } else if (type.equals("hour")) {

                    editor.putString(cityName + "Hour", response);
                    editor.apply();

                } else if (type.equals("life")) {
                    editor.putString(cityName + "Life", response);
                    editor.apply();


                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 获取关闭自动更新的信息
         */
        Log.d("AutoService", "onDestroy");
    }
}
