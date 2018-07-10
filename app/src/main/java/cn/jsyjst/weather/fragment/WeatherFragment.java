package cn.jsyjst.weather.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.weather.R;
import cn.jsyjst.weather.adapter.DailyAdapter;
import cn.jsyjst.weather.adapter.HourAdapter;
import cn.jsyjst.weather.adapter.LifeAdapter;
import cn.jsyjst.weather.utils.JsonPrase;
import cn.jsyjst.weather.db.CityCrud;
import cn.jsyjst.weather.db.MyDatabaseHelper;
import cn.jsyjst.weather.entity.DailyWeather;
import cn.jsyjst.weather.entity.HoursWeather;
import cn.jsyjst.weather.entity.Life;
import cn.jsyjst.weather.entity.Now;
import cn.jsyjst.weather.entity.TempLine;
import cn.jsyjst.weather.ui.ManageCityActivity;
import cn.jsyjst.weather.ui.SetActivity;
import cn.jsyjst.weather.utils.HttpUtil;
import cn.jsyjst.weather.widget.TempLineView;

import static cn.jsyjst.weather.config.ApiInterface.DAILY;
import static cn.jsyjst.weather.config.ApiInterface.HOUR;
import static cn.jsyjst.weather.config.ApiInterface.LIFE;
import static cn.jsyjst.weather.config.ApiInterface.LOCATION;
import static cn.jsyjst.weather.config.ApiInterface.NOW;
import static cn.jsyjst.weather.config.ApiInterface.SHARE;

/**
 * Created by 残渊 on 2018/5/22.
 */

public class WeatherFragment extends Fragment {


    /**
     * 接受活动传过来的城市名
     */
    private String mCity;
    /**
     * 接受活动传过来的页面数
     */
    private int mNum;
    /**
     * 接受活动传过来的数据库的列的总个数，即添加城市中的总个数
     */
    private int mAllNum;
    /**
     * 当地位置经度
     */
    private double longitude;
    /**
     * 当地位置纬度
     */
    private double latitude;


    private TextView feelsTv;
    private TextView humidityTv;
    private TextView windTv;
    private TextView pressureTv;

    /**
     * 分享按钮
     */
    private Button shareBtn;

    /**
     * 记录当地位置的城市名，从而进行位置照片的操作
     */
    private TextView cityTv;
    private Button addedBtn;
    private Button setBtn;
    /**
     * 当日温度
     */
    private TextView temperatureTv;
    /**
     * 当日天气
     */
    private TextView textTv;
    /**
     * 最高温和最低温
     */
    private TextView highLowTemperatureTv;
    /**
     * 天气图片
     */
    private ImageView codeIv;
    /**
     * 位置图片
     */
    private ImageView locationIv;
    /**
     * 页面数
     */
    private TextView pageNumTv;
    /**
     * 记录当地城市
     */
    private String locationCity;

    private ListView listView;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefresh;

    private LocationManager locationManager;

    private Handler handler;

    private SharedPreferences prefs;
    private ScrollView scrollView;
    private RecyclerView hourRecyclerView;
    /**
     * 分享内容，利用了知心api的接口
     */
    private String shareText;

    /**
     * 多日天气实现的list
     */
    private List<DailyWeather> dailyList = new ArrayList<>();


    /**
     * 生活指数实现的list
     */
    private List<Life> lifeList = new ArrayList<>();
    private List<String> briefList = new ArrayList<>();
    /**
     * 每小时天气的实现的list
     */
    private List<HoursWeather> hourList = new ArrayList<>();

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    /**
     * 用来判断是否有网络
     */
    private boolean isNetwork = false;
    /**
     * i用来控制刷新提示
     */
    private int i = 0;

    private SharedPreferences.Editor editor;

    private View view;

    private Context context;

    /**
     * 自定义的温度折线图
     */
    private TempLineView tempLineView;

    /**
     * 白天温度集合
     */
    private static int mTempDay[] = new int[6];

    /**
     * 夜间温度集合
     */
    private static int mTempNight[] = new int[6];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weather_fragment, container, false);
        context = view.getContext();
        /**
         * 初始化各控件
         */
        cityTv = view.findViewById(R.id.tv_location_city);
        temperatureTv = view.findViewById(R.id.tv_temperature);
        textTv = view.findViewById(R.id.tv_text);
        highLowTemperatureTv = view.findViewById(R.id.tv_high_low);
        codeIv = view.findViewById(R.id.iv_code);
        feelsTv=view.findViewById(R.id.tv_now_feels_like);
        humidityTv=view.findViewById(R.id.tv_now_humidity);
        windTv=view.findViewById(R.id.tv_now_wind);
        pressureTv=view.findViewById(R.id.tv_now_pressure);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        recyclerView = view.findViewById(R.id.recycler_life);
        addedBtn = view.findViewById(R.id.btn_added);
        setBtn = view.findViewById(R.id.btn_set);
        locationIv = view.findViewById(R.id.iv_location);
        shareBtn = view.findViewById(R.id.btn_share);
        scrollView = view.findViewById(R.id.scrollView_weather);
        pageNumTv = view.findViewById(R.id.tv_page_number);
        hourRecyclerView = view.findViewById(R.id.recycler_hour);
        listView = view.findViewById(R.id.lv_daily_weather);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
        tempLineView=view.findViewById(R.id.line_char);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /**
         * 显示城市个数和所在城市的页面index
         */
        pageNumTv.setText(String.valueOf(mNum)+"/"+String.valueOf(mAllNum));
        /**
         * 有缓存直接解析天气数据
         */
        String shareResponse = prefs.getString(mCity + "Share", null);
        if (shareResponse != null) {
            /**
             * 分享功能
             */
            shareText = JsonPrase.handShareResponse(shareResponse);

            /**
             * 天气实况
             */
            String nowResponse = prefs.getString(mCity + "Now", null);
            Now now = JsonPrase.handNowWeatherResponse(nowResponse);
            showNowWeather(now);

            /**
             * 逐时天气
             */
            String hourResponse = prefs.getString(mCity + "Hour", null);
            hourList = JsonPrase.handHourWeatherResponse(hourResponse);
            showHourWeather(hourList);

            /**
             * 多天预报
             */
            String dailyResponse = prefs.getString(mCity + "Daily", null);
            dailyList = JsonPrase.handDailyWeatherResponse(dailyResponse);
            showDailyWeather(dailyList);
            /**
             * 气温折线图
             */
           TempLine tempLine=JsonPrase.handTempResponse(dailyResponse);
           showTempLine(tempLine);
            /**
             * 生活指数
             */
            String lifeResponse = prefs.getString(mCity + "Life", null);
            briefList = JsonPrase.handLifeResponse(lifeResponse);
            showLife(briefList);
            /**
             * 没有缓存去联网查找天气
             * 如果是当地位置则重新定位
             */
        } else {
            if (mCity.equals("当前位置")) {
                findCity();
            } else {
                weatherRequest(mCity, "life");
                weatherRequest(mCity, "now");
                weatherRequest(mCity, "hour");
                weatherRequest(mCity, "daily");
                weatherRequest(mCity, "share");
            }
        }
        /**
         * 广播监听网络
         */
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        context.registerReceiver(networkChangeReceiver, intentFilter);
        /**
         * 分享功能
         */
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shareText == null) {
                    Toast.makeText(getActivity(), "分享功能出错了，小渊正在处理中", Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareText + "\n\n" + "^_^欢迎使用渊天气^_^");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    /**
                     * 程序选择器
                     */
                    startActivity(Intent.createChooser(intent, "分享"));
                }
            }
        });

        /**
         * 跳转城市管理界面
         */
        addedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageCityActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 设置
         */
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
            }
        });

        /**
         * 刷新实现
         */
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetwork) {
                    if (mCity.equals(locationCity)) {
                        findCity();
                    } else {
                        i=0;
                        weatherRequest(mCity, "now");
                        weatherRequest(mCity, "hour");
                        weatherRequest(mCity, "daily");
                        weatherRequest(mCity, "life");
                        weatherRequest(mCity, "share");
                    }
                }else{
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(context,"当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 查找当前位置的城市
     */
    private void findCity() {
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                double[] data = (double[]) msg.obj;
                getLocation(String.valueOf(data[0]), String.valueOf(data[1]));
            }

        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        new Thread() {
            @Override
            public void run() {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude(); // 纬度
                    longitude = location.getLongitude(); //  经度
                    double[] data = {latitude, longitude};
                    Message msg = handler.obtainMessage();
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }


    /**
     * 根据经纬度调用心知天气的api获取当地城市名
     *
     * @param latitude
     * @param longitude
     */
    private void getLocation(final String latitude, String longitude) {
        String address = LOCATION + latitude + ":" + longitude;
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String cityName = JsonPrase.handLocationCityResponse(response);

                        /**
                         * 保存到本地中
                         */
                        editor.putString("locationCity", cityName);
                        editor.apply();

                        locationCity = cityName;
                        mCity = cityName;


                        /**
                         * 查找天气
                         */
                        weatherRequest(cityName, "now");
                        weatherRequest(cityName, "hour");
                        weatherRequest(cityName, "daily");
                        weatherRequest(cityName, "life");
                        weatherRequest(cityName, "share");


                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 联网查询有关天气的数据
     */
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
            @Override
            public void onFinish(final String response) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type.equals("share")) {
                            /**
                             * 分享功能
                             */
                            editor.putString(cityName + "Share", response);
                            editor.apply();

                            shareText = JsonPrase.handShareResponse(response);
                        } else if (type.equals("now")) {
                            /**
                             * 天气实况
                             */
                            editor.putString(cityName + "Now", response);
                            editor.apply();

                            Now now = JsonPrase.handNowWeatherResponse(response);
                            showNowWeather(now);
                        } else if (type.equals("hour")) {
                            /**
                             * 逐时天气
                             */
                            editor.putString(cityName + "Hour", response);
                            editor.apply();

                            hourList = JsonPrase.handHourWeatherResponse(response);
                            showHourWeather(hourList);


                        } else if (type.equals("daily")) {
                            /**
                             * 多天预报
                             */

                            editor.putString(cityName + "Daily", response);
                            editor.apply();

                            dailyList = JsonPrase.handDailyWeatherResponse(response);
                            showDailyWeather(dailyList);

                            TempLine tempLine = JsonPrase.handTempResponse(response);
                            showTempLine(tempLine);
                            /**
                             * 每次联网查找天气时，多天预报返回数据的时间最长
                             * 故在多天天气预报中数据返回时提示更新完成
                             */
                            swipeRefresh.setRefreshing(false);
                            Toast.makeText(getActivity(), "已更新到最新天气", Toast.LENGTH_SHORT).show();
                        } else if (type.equals("life")) {
                            /**
                             * 生活指数
                             */
                            editor.putString(cityName + "Life", response);
                            editor.apply();

                            briefList = JsonPrase.handLifeResponse(response);
                            showLife(briefList);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Ui处理今天的天气
     */
    private void showNowWeather(Now now) {
        String temperature = now.getTemperature();
        String text = now.getText();
        String code = now.getCode();
        String feels=now.getFeels();
        String humidity=now.getHumidity();
        String wind=now.getWind();
        String pressure=now.getPressure();
        /**
         * 保存在数据库中
         * 判断数据库有没这个城市，如果有就更新相对应的列
         * 没有的话就直接创建表
         */
        boolean isHasCity = false;
        CityCrud cityCrud = new CityCrud(getActivity(), "weather.db", null, 2);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "weather.db", null, 2);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (mCity.equals(cursor.getString(cursor.getColumnIndexOrThrow("name")))) {
                isHasCity = true;
                break;
            }
        }
        cursor.close();
        db.close();
        /**
         * 删除当前位置的数据库，将当前位置的城市保存到数据库
         */
        cityCrud.isHasCity("当前位置");
        if (isHasCity) {
            cityCrud.updateCity(mCity, temperature, text, code);
        } else {
            cityCrud.create(mCity, temperature, text, code);
        }

        locationCity = prefs.getString("locationCity", null);
        if (mCity.equals("当前位置") || mCity.equals(locationCity)) {
            /**
             * 发送广播，令小部件更新
             */
            Intent broadIntent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
            context.sendBroadcast(broadIntent);
            cityTv.setText(locationCity);
            locationIv.setVisibility(View.VISIBLE);
        } else {
            locationIv.setVisibility(View.GONE);
            cityTv.setText(mCity);
        }
        setWeatherImage(code);
        temperatureTv.setText(temperature);
        textTv.setText(text);
        feelsTv.setText("体感"+"\n"+feels+"℃");
        humidityTv.setText("湿度"+'\n'+humidity+"％");
        windTv.setText(wind);
        pressureTv.setText("气压"+"\n"+pressure+"hPa");

    }

    /**
     * ui处理未来24小时的天气
     */
    private void showHourWeather(List<HoursWeather> hourList) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hourRecyclerView.setLayoutManager(layoutManager);
        HourAdapter adapter = new HourAdapter(hourList);
        hourRecyclerView.setAdapter(adapter);
    }


    /**
     * Ui处理多天天气，用ListView适配
     */
    private void showDailyWeather(List<DailyWeather> dailyList) {
        highLowTemperatureTv.setText(dailyList.get(0).getHighLowTemperature());
        DailyAdapter adapter = new DailyAdapter(context, R.layout.daily_listview_item, dailyList);
        listView.setAdapter(adapter);
    }


    /**
     *  气温折线图
     */

    private void showTempLine(TempLine tempLine){
        mTempDay=tempLine.getmTempDay();
        mTempNight=tempLine.getmTempNight();
        /**
         * 设置白天温度曲线
         */
        tempLineView .setTempDay(mTempDay);
        /**
         *  设置夜间温度曲线
         */
        tempLineView .setTempNight(mTempNight);
        /**
         * 屏幕刷新
         */
        tempLineView .invalidate();
    }


    /**
     * UI处理生活指数，用recyclerView实现瀑布流布局
     */

    private void showLife(List<String> brief) {
        initLife(brief);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        LifeAdapter adapter = new LifeAdapter(lifeList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化生活指数
     *
     * @param briefList
     */
    private void initLife(List<String> briefList) {
        lifeList.clear();
        Life umbrella = new Life(R.drawable.iv_umbrella_0, briefList.get(0), "雨伞");
        lifeList.add(umbrella);
        Life ac = new Life(R.drawable.iv_ac_1, briefList.get(1), "空调");
        lifeList.add(ac);
        Life boating = new Life(R.drawable.iv_boating_2, briefList.get(2), "划船");
        lifeList.add(boating);
        Life comfort = new Life(R.drawable.iv_comfort_3, briefList.get(3), "舒适度");
        lifeList.add(comfort);
        Life dressing = new Life(R.drawable.iv_dressing_4, briefList.get(4), "穿衣");
        lifeList.add(dressing);
        Life fishing = new Life(R.drawable.iv_fishing_5, briefList.get(5), "钓鱼");
        lifeList.add(fishing);
        Life flu = new Life(R.drawable.iv_flu_6, briefList.get(6), "感冒");
        lifeList.add(flu);
        Life shopping = new Life(R.drawable.iv_shopping_7, briefList.get(7), "购物");
        lifeList.add(shopping);
        Life sport = new Life(R.drawable.iv_sport_8, briefList.get(8), "运动");
        lifeList.add(sport);
        Life sunscreen = new Life(R.drawable.iv_sunscreen_9, briefList.get(9), "防晒");
        lifeList.add(sunscreen);
        Life travel = new Life(R.drawable.iv_trvel_10, briefList.get(10), "旅游");
        lifeList.add(travel);
        Life uv = new Life(R.drawable.iv_uv_11, briefList.get(11), "紫外线");
        lifeList.add(uv);
    }


    /**
     * 根据code来实现为天气配图
     * @param code 天气现象代码
     */
    private void setWeatherImage(String code) {
        if (code.equals("0")) {
            codeIv.setImageResource(R.drawable.iv_0);
        } else if (code.equals("1")) {
            codeIv.setImageResource(R.drawable.iv_1);
        } else if (code.equals("2")) {
            codeIv.setImageResource(R.drawable.iv_2);
        } else if (code.equals("3")) {
            codeIv.setImageResource(R.drawable.iv_3);
        } else if (code.equals("4")) {
            codeIv.setImageResource(R.drawable.iv_4);
        } else if (code.equals("5")) {
            codeIv.setImageResource(R.drawable.iv_5);
        } else if (code.equals("6")) {
            codeIv.setImageResource(R.drawable.iv_6);
        } else if (code.equals("7")) {
            codeIv.setImageResource(R.drawable.iv_7);
        } else if (code.equals("8")) {
            codeIv.setImageResource(R.drawable.iv_8);
        } else if (code.equals("9")) {
            codeIv.setImageResource(R.drawable.iv_9);
        } else if (code.equals("10")) {
            codeIv.setImageResource(R.drawable.iv_10);
        } else if (code.equals("11")) {
            codeIv.setImageResource(R.drawable.iv_11);
        } else if (code.equals("12")) {
            codeIv.setImageResource(R.drawable.iv_12);
        } else if (code.equals("13")) {
            codeIv.setImageResource(R.drawable.iv_13);
        } else if (code.equals("14")) {
            codeIv.setImageResource(R.drawable.iv_14);
        } else if (code.equals("15")) {
            codeIv.setImageResource(R.drawable.iv_15);
        } else if (code.equals("16")) {
            codeIv.setImageResource(R.drawable.iv_16);
        } else if (code.equals("17")) {
            codeIv.setImageResource(R.drawable.iv_17);
        } else if (code.equals("18")) {
            codeIv.setImageResource(R.drawable.iv_18);
        } else if (code.equals("19")) {
            codeIv.setImageResource(R.drawable.iv_19);
        } else if (code.equals("30")) {
            codeIv.setImageResource(R.drawable.iv_30);
        } else if (code.equals("31")) {
            codeIv.setImageResource(R.drawable.iv_31);
        }
    }

    /**
     * 网络操作
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                isNetwork = true;
            } else {
                isNetwork = false;
                Toast.makeText(getActivity(), "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /**
     *默认显示的首项是ListView，需要手动把ScrollView滚动至最顶端
     */
    @Override
    public void onStart(){
        super.onStart();
        scrollView.smoothScrollTo(0, 0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(networkChangeReceiver);
    }


    /**
     * 提供方法给活动传值
     * @param city
     */
    public void setCityName(String city) {
        mCity = city;
    }
    public void setNum(int num){mNum=num;}

    public void setAllNum(int allNum){
        mAllNum=allNum;
    }

}
