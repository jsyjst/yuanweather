package cn.jsyjst.weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.db.CityCrud;
import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.ui.MainActivity;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

/**
 * 桌面小部件功能
 */
public class WeatherWidget extends AppWidgetProvider {

    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;
    private String code;
    /**
     * 判断当地位置在数据库中的位置，从而实现点击后跳转到当地城市的页面
     */
    private int index;
    private String temperature;
    private String text;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        /**
         * 从本地读取本地位置
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String locationCity = prefs.getString("locationCity", null);
        views.setTextViewText(R.id.tv_widget_city, locationCity + "区");


        /**
         * 读取数据库，如果当地城市有在数据库，则得到其位置
         */
        index = 0;
        boolean isHasCity = false;
        dbHelper = new MyDatabaseHelper(context, "weather.db", null, 2);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (locationCity.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                temperature = cursor.getString(cursor.getColumnIndexOrThrow("temperature"));
                text = cursor.getString(cursor.getColumnIndexOrThrow("text"));
                code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
                isHasCity = true;
                break;
            } else {
                index++;
            }
        }
        db.close();
        cursor.close();
        /**
         * 判断数据库中有没当地位置的天气，即有没添加当地城市的天气
         * 如果没有就不显示天气信息
         */
        if (isHasCity) {
            views.setTextViewText(R.id.tv_widget_temperature, temperature + "℃");
            views.setTextViewText(R.id.tv_widget_text, text);

            /**
             * 为天气配图
             */

            if (code.equals("0")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_0);
            } else if (code.equals("1")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_1);
            } else if (code.equals("2")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_2);
            } else if (code.equals("3")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_3);
            } else if (code.equals("4")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_4);
            } else if (code.equals("5")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_5);
            } else if (code.equals("6")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_6);
            } else if (code.equals("7")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_7);
            } else if (code.equals("8")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_8);
            } else if (code.equals("9")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_9);
            } else if (code.equals("10")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_10);
            } else if (code.equals("11")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_11);
            } else if (code.equals("12")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_12);
            } else if (code.equals("13")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_13);
            } else if (code.equals("14")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_14);
            } else if (code.equals("15")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_15);
            } else if (code.equals("16")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_16);
            } else if (code.equals("17")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_17);
            } else if (code.equals("18")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_18);
            } else if (code.equals("19")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_19);
            } else if (code.equals("30")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_30);
            } else if (code.equals("31")) {
                views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_31);
            }

        } else {
            index=0;
            views.setImageViewResource(R.id.iv_widget_code, R.drawable.iv_5);
            views.setTextViewText(R.id.tv_widget_temperature, "");
            views.setTextViewText(R.id.tv_widget_text, "");
        }


        /**
         * 日期周几处理
         */
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M月dd日");
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
        String date = dateFormat.format(now);
        String week = weekFormat.format(now);
        views.setTextViewText(R.id.tv_widget_date,
                date + week);

        /**
         * 桌面小部件的点击效果,将城市名传过去，更新天气
         */
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("number", index);
        /**
         * 因为Intent带有数据，则需要将最后一个参数的值设为：FLAG_CANCEL_CURRENT
         */
        PendingIntent i = PendingIntent.getActivity(context, 0, intent, FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.rv_app_widget, i);

        /**
         * 更新到所有小部件中
         */
        ComponentName componentName = new ComponentName(context, WeatherWidget.class);
        appWidgetManager.updateAppWidget(componentName, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context, appWidgetManager);
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }

    /**
     * 接受主活动发过来的广播
     *
     * @param context
     * @param intent
     */
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        updateAppWidget(context, appWidgetManager);
    }

}

