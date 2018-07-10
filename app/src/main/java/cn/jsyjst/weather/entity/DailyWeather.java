package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/18.
 */

public class DailyWeather {
    private String date;

    private String text;

    private String highLowTemperature;

    private String code;


    public DailyWeather(String date,String text,String highLowTemperature,String code){
        this.date=date;
        this.text=text;
        this.highLowTemperature=highLowTemperature;
        this.code=code;

    }
    public String getDate(){
        return date;
    }


    public String getText(){
        return text;
    }


    public String getHighLowTemperature(){
        return highLowTemperature;
    }


    public String getCode() {
        return code;
    }


}
