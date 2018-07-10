package cn.jsyjst.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 残渊 on 2018/5/19.
 */


/**
 * 创建表
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_CITY="create table City("
            +"id integer primary key autoincrement,"
            +"name text,"
            +"temperature text,"
            +"code text,"
            +"text text)";


    private Context mContext;

    public MyDatabaseHelper(Context context,String name,
                            SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int OldVersion,int newVersion){
        db.execSQL("drop table if exists City");
        onCreate(db);
    }

}
