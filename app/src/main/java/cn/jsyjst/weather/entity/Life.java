package cn.jsyjst.weather.entity;

/**
 * Created by 残渊 on 2018/5/18.
 */

public class Life {
    private int ImageId;

    private String brief;
    private String lifeName;

    public Life(int imageId,String brief,String lifeName){
        this.ImageId=imageId;
        this.brief=brief;
        this.lifeName=lifeName;
    }

    public int getImageId() {
        return ImageId;
    }

    public String getBrief() {
        return brief;
    }

    public String getLifeName() {
        return lifeName;
    }
}
