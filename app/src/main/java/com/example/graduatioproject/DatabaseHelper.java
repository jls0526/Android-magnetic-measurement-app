package com.example.graduatioproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_DATABASE = "create table MagneticData("
            + "x integer,"
            + "y integer,"
            + "x_surface integer,"
            + "y_surface integer,"
            + "z_surface integer,"
            + "time text)";

    private static final String CREATE_NAME_DATABASE = "create table NameList("
            + "name text,"
            + "label text)";
    Context mContext;
    /**
     *
     * @param context   上下文
     * @param name      数据库名称
     * @param factory   游标工厂
     * @param version   版本号
     */
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);//执行SQL语句
        db.execSQL(CREATE_NAME_DATABASE);
        Toast.makeText(mContext,"Create database successful!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists MagneticData");
        db.execSQL("drop table if exists NameList");
        onCreate(db);
    }
}
