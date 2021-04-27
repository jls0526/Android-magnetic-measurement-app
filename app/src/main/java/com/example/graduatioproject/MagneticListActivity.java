package com.example.graduatioproject;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MagneticListActivity extends AppCompatActivity {

    private static final String TAG = "MagneticListActivity";
    private List<MagneticListInfo> magneticListInfos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic_list);
        //MagneticListInfo magneticListInfo = new MagneticListInfo("第一次测量","2021年4月10日","111");
        DatabaseHelper databaseHelper = new DatabaseHelper(this,"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);
        SQLiteDatabase writableDatabase;
        writableDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.query("NameList",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String content = cursor.getString(cursor.getColumnIndex("name"));
                String label = cursor.getString(cursor.getColumnIndex("label"));
                if(content.length() >= 15){
                    String time = "测量时间：" + content.substring(0,4) + "." +
                            Integer.parseInt(content.substring(4,6)) + "." +
                            Integer.parseInt(content.substring(6,8)) + "-" +
                            Integer.parseInt(content.substring(9,11)) + ":" +
                            Integer.parseInt(content.substring(11,13)) + ":" +
                            Integer.parseInt(content.substring(13,15));
                    MagneticListInfo magneticListInfo = new MagneticListInfo(label,time,content);
                    magneticListInfos.add(magneticListInfo);
                }

                //Log.d(TAG, "onCreate: nameList:" + content);
            }while (cursor.moveToNext());
        }
        cursor.close();

        MagneticDataAdapter magneticDataAdapter = new MagneticDataAdapter(MagneticListActivity.this,R.layout.magnetic_list_layout,magneticListInfos);
        ListView listView = findViewById(R.id.magnetic_list);
        listView.setAdapter(magneticDataAdapter);


    }
}
