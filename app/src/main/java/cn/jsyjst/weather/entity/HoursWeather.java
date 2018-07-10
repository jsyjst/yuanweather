package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/19.
 */

public class HoursWeather {
    private String mTime;

    private String mCode;

    private String mTemperature;

    public HoursWeather(String time,String code,String temperature){
        mTime=time;
        mCode=code;
        mTemperature=temperature;
    }

    public String getmTemperature() {
        return mTemperature;
    }

    public String getmCode() {
        return mCode;
    }

    public String getmTime() {
        return mTime;
    }
}
