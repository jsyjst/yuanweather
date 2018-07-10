package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/18.
 */

public class Now {
    private String mTemperature;

    private String mText;

    private String mCode;

    private String mFeels;

    private String mPressure;

    private String mHumidity;

    private String mWind;

    public Now(String temperature,String text,String code,String feels,String pressure,
               String humidity,String wind){
        mText=text;
        mTemperature=temperature;
        mCode=code;
        mFeels=feels;
        mPressure=pressure;
        mHumidity=humidity;
        mWind=wind;
    }



    public String getTemperature() {
        return mTemperature;
    }




    public String getText() {
        return mText;
    }


    public String getCode() {
        return mCode;
    }

    public String getFeels() {
        return mFeels;
    }

    public String getPressure() {
        return mPressure;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public String getWind() {
        return mWind;
    }
}
