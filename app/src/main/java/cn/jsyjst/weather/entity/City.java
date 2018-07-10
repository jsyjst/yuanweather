package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/17.
 */


public class City {
    private String mCityName;
    private String mTemperature;

    private String mText;

    public City(String cityName,String temperature,String text){
        mCityName=cityName;
        mTemperature=temperature;
        mText=text;
    }

    public String getmCityName() {
        return mCityName;
    }

    public String getmTemperature() {
        return mTemperature;
    }

    public String getmText() {
        return mText;
    }
}
