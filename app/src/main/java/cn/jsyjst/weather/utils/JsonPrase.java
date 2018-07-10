package cn.jsyjst.weather.utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jsyjst.weather.entity.DailyWeather;
import cn.jsyjst.weather.entity.HoursWeather;
import cn.jsyjst.weather.entity.Now;
import cn.jsyjst.weather.entity.TempLine;

/**
 * Created by 残渊 on 2018/5/18.
 */

/**
 * 解析和处理请求网络返回的数据
 */
public class JsonPrase {
    /**
     * 解析和处理服务器返回的定位数据
     *
     * @param response Json数据
     * @return
     */
    public static String handLocationCityResponse(String response) {
        String cityName;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject cityJsonObject = jsonObject.getJSONArray("results").getJSONObject(0);
            cityName = cityJsonObject.getString("name");
            return cityName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析处理心知服务器返回的今天的数据
     *
     * @param response
     * @return
     */
    public static Now handNowWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject nowJsonObject = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("now");
            String temperature = nowJsonObject.getString("temperature");
            String text = nowJsonObject.getString("text");
            String code = nowJsonObject.getString("code");
            String feels=nowJsonObject.getString("feels_like");
            String pressure=nowJsonObject.getString("pressure");
            String humidity=nowJsonObject.getString("humidity");
            String wind_direction=nowJsonObject.getString("wind_direction");
            String wind_scale=nowJsonObject.getString("wind_scale");
            String wind=wind_direction+"风"+"\n"+wind_scale+"级";
            Now now = new Now(temperature, text, code,feels,pressure,humidity,wind);
            return now;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析处理心知服务器返回的多日天气
     *
     * @param response
     * @return
     */
    public static List<DailyWeather> handDailyWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dailyJsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("daily");
            final List<DailyWeather> dailyList = new ArrayList<>();
            for (int i = 0; i < dailyJsonArray.length(); i++) {
                /**
                 * 解析日期，并将YYYY-MM-dd转换成MM/dd的形式
                 */
                String dailyDate = dailyJsonArray.getJSONObject(i).getString("date");
                String month = dailyDate.substring(5, 7);
                String day = dailyDate.substring(8, 10);

                /**
                 * 根据日期获得星期几
                 */
                SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
                Date dt = dateFm.parse(dailyDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                String date;
                if (i == 0) {
                    date=month+"/"+day+" 今天";
                } else if (i == 1) {
                    date = month + "/" + day + " 明天";
                } else {
                    date = month + "/" + day + " " + dateFormat.format(dt);
                }

                String dailyText = dailyJsonArray.getJSONObject(i).getString("text_day");
                String dailyHigh = dailyJsonArray.getJSONObject(i).getString("high");
                String dailyLow = dailyJsonArray.getJSONObject(i).getString("low");
                String dailyHighLow = dailyHigh + "℃ " + "/ " + dailyLow + "℃";
                String dailyCode = dailyJsonArray.getJSONObject(i).getString("code_day");
                DailyWeather dailyWeather;

                dailyWeather = new DailyWeather(date, dailyText, dailyHighLow, dailyCode);
                dailyList.add(dailyWeather);
            }
            return dailyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析和处理最高温和最低温
     */
    public static TempLine handTempResponse(String response) {

        int tempDay[] = new int[6];
        int tempNight[] = new int[6];

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dailyJsonArray = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("daily");
            for (int i = 0; i < dailyJsonArray.length(); i++) {
                String dailyHigh = dailyJsonArray.getJSONObject(i).getString("high");
                tempDay[i] = Integer.valueOf(dailyHigh);
                String dailyLow = dailyJsonArray.getJSONObject(i).getString("low");
                tempNight[i] = Integer.valueOf(dailyLow);
            }
            TempLine tempLine = new TempLine(tempDay, tempNight);
            return tempLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解析和处理服务器返回的生活指数的数据
     *
     * @param response 地址
     * @return
     */
    public static List<String> handLifeResponse(String response) {
        List<String> briefList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject suggestJb = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("suggestion");

            String umbrella = suggestJb.getJSONObject("umbrella").getString("brief");
            briefList.add(umbrella);
            String ac = suggestJb.getJSONObject("ac").getString("brief");
            briefList.add(ac);
            String boating = suggestJb.getJSONObject("boating").getString("brief");
            briefList.add(boating);
            String comfort = suggestJb.getJSONObject("comfort").getString("brief");
            briefList.add(comfort);
            String dressing = suggestJb.getJSONObject("dressing").getString("brief");
            briefList.add(dressing);
            String fishing = suggestJb.getJSONObject("fishing").getString("brief");
            briefList.add(fishing);
            String flu = suggestJb.getJSONObject("flu").getString("brief");
            briefList.add(flu);
            String shopping = suggestJb.getJSONObject("shopping").getString("brief");
            briefList.add(shopping);
            String sport = suggestJb.getJSONObject("sport").getString("brief");
            briefList.add(sport);
            String sunscreen = suggestJb.getJSONObject("sunscreen").getString("brief");
            briefList.add(sunscreen);
            String travel = suggestJb.getJSONObject("travel").getString("brief");
            briefList.add(travel);
            String uv = suggestJb.getJSONObject("uv").getString("brief");
            briefList.add(uv);

            return briefList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析处理服务器返回的每小时天气的数据
     */
    public static List<HoursWeather> handHourWeatherResponse(String response) {
        List<HoursWeather> hourList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jay = jsonObject.getJSONArray("results").getJSONObject(0).
                    getJSONArray("hourly");
            for (int i = 0; i < jay.length(); i++) {
                String time = jay.getJSONObject(i).getString("time");
                String hour;
                if (i == 0) {
                    hour = "现在";
                } else {
                    hour = time.substring(11, 16);
                }

                String code = jay.getJSONObject(i).getString("code");
                String temperature = jay.getJSONObject(i).getString("temperature") + "℃";
                HoursWeather hoursWeather = new HoursWeather(hour, code, temperature);
                hourList.add(hoursWeather);
            }
            return hourList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理和解析服务器返回的城市列表
     *
     * @param response
     * @return
     */
    public static List<String> handFindCityResponse(String response) {
        List<String> pathList = new ArrayList<>();
        pathList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jay = jsonObject.getJSONArray("results");
            for (int i = 0; i < jay.length(); i++) {
                String path = jay.getJSONObject(i).getString("path");
                pathList.add(path);
            }
            return pathList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析和处理自然语言
     */
    public static String handShareResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject replyJb = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("reply");
            String plain = replyJb.getString("plain");
            return plain;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
