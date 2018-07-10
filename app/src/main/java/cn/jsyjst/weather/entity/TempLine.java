package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/23.
 */


public class TempLine {
    /**
     * 白天温度集合
     */
    private  int mTempDay[] = new int[6];

    /**
     * 夜间温度集合
     */
    private  int mTempNight[] = new int[6];

    public TempLine(int tempDay[],int[] tempNight){
        mTempDay=tempDay;
        mTempNight=tempNight;
    }

    public  int[] getmTempDay() {
        return mTempDay;
    }

    public  int[] getmTempNight() {
        return mTempNight;
    }
}
