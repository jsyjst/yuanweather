package cn.jsyjst.weather.config;

/**
 * Created by 残渊 on 2018/5/31.
 */

public class ApiInterface {
    public final static String LOCATION="https://api.seniverse.com/v3/location/search.json?key=y76boyq776m6qisk&q=";

    public final static String NOW= "https://api.seniverse.com/v3/weather/now.json?key=y76boyq776m6qisk&language=zh-Hans&unit=c&location=";

    public final static String SHARE="https://api.seniverse.com/v3/robot/talk.json?key=y76boyq776m6qisk&q=";

    public final static String HOUR="https://api.seniverse.com/v3/weather/hourly.json?key=y76boyq776m6qisk&language=zh-Hans&unit=c&start=0&hours=24 " +
            "&location=";

    public final static String DAILY= "https://api.seniverse.com/v3/weather/daily.json?key=y76boyq776m6qisk"+
            "&language=zh-Hans&unit=c@start=0&days=6&location=";

    public final static String LIFE="https://api.seniverse.com/v3/life/suggestion.json?key=y76boyq776m6qisk"+
            "&language=zh-Hans &location=";
}
