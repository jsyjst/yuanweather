package cn.jsyjst.weather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.jsyjst.weather.entity.City;
import cn.jsyjst.weather.entity.Now;

/**
 * Created by 残渊 on 2018/5/19.
 */


/**
 * 数据库的CRUD操作
 */
public class CityCrud  {
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase db;

    private List<City> cityList=new ArrayList<>();

    public CityCrud(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        dbHelper = new MyDatabaseHelper(context, name, factory, version);
    }

    /**
     * 创建表操作
     * @param city 城市名
     * @param temperature 温度
     * @param text 天气现象
     * @param code 天气现象代码
     */
    public void create(String city,String temperature,String text,String code){
        isHasCity(city);
        db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",city);
        values.put("temperature",temperature);
        values.put("code",code);
        values.put("text",text);
        db.insert("City",null,values);
        db.close();
    }

    /**
     * 删除列功能
     * @param city 城市名
     */
    public void isHasCity(String city){
        db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("City",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            if(city.equals(cursor.getString(cursor.getColumnIndex("name")))){
                String id = cursor.getString(cursor.getColumnIndex("id"));//获取该记录的id
                db.execSQL("delete from City Where id=" + id);
            }
        }
        db.close();
        cursor.close();
    }

    public List<City> getCityList() {
        cityList.clear();
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String temperature = cursor.getString(cursor.getColumnIndex("temperature"));
            String text=cursor.getString(cursor.getColumnIndexOrThrow("text"));
            City city = new City(name,temperature,text );
            cityList.add(city);
        }
        db.close();
        cursor.close();
        return cityList;
    }

    /**
     * 更新表
     * @param city 城市名
     * @param temperature 温度
     * @param text 天气现象
     * @param code 天气现象代码
     */
    public void updateCity(String city,String temperature,String text,String code ){
        db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query("City",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            if(city.equals(cursor.getString(cursor.getColumnIndex("name")))){
                ContentValues values=new ContentValues();
                values.put("temperature",temperature);
                values.put("code",code);
                values.put("text",text);
                db.update("City",values,"name= ?",new String[]{city} );
            }
        }
        db.close();
        cursor.close();

    }
}
