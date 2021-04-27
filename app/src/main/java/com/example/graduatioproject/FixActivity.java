package com.example.graduatioproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.graduatioproject.contour.MagneticRawData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FixActivity extends AppCompatActivity {
    private List<MagneticListInfo> magneticListInfos = new ArrayList<>();
    private Button btnFixOK;
    private String TAG = "TestFix";
    SQLiteDatabase writableDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);

        btnFixOK = findViewById(R.id.btn_fix_ok);

        DatabaseHelper databaseHelper = new DatabaseHelper(this,"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);

        writableDatabase = databaseHelper.getWritableDatabase();

        @SuppressLint("UseSparseArrays") final HashMap<Integer,MagneticRawData> hsMultiply = new HashMap<>();
        final ArrayList<MagneticRawData> magneticRawDatas = new ArrayList<>();

        btnFixOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator it = Constant.FIX_CHOSEN.iterator();
                Log.d(TAG, "onClick: size:" + Constant.FIX_CHOSEN.size());

                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter  =  new   SimpleDateFormat   ("yyyyMMdd-HHmmss");
                Date curDate =  new Date(System.currentTimeMillis());
                String time = formatter.format(curDate);
                Log.d(TAG, "onClick: " + time);
                MainActivity.insertNameList(time);

                while (it.hasNext()){
                    MagneticListInfo magneticListInfo = (MagneticListInfo) it.next();
                    Log.d(TAG, "onClick: " + magneticListInfo.getId());

                    Cursor cursor = writableDatabase.query("MagneticData",new String[]{"x","y","x_surface","y_surface","z_surface","time"},"time=?",new String[]{magneticListInfo.getId()},null,null,null);
                    if(cursor.moveToFirst()){
                        do{
                            int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("x")));
                            int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("y")));
                            int gmiCH1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("x_surface")));
                            int gmiCH2 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("y_surface")));
                            int gmiCH3 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("z_surface")));

                            if(hsMultiply.containsKey(x)){
                                if(hsMultiply.get(x).getY() == y){
                                    break;
                                }else {
                                    MagneticRawData magneticRawData = new MagneticRawData(x,y,gmiCH1,gmiCH2,gmiCH3,0);
                                    hsMultiply.put(x,magneticRawData);
                                    magneticRawDatas.add(magneticRawData);
                                }

                            }else{
                                MagneticRawData magneticRawData = new MagneticRawData(x,y,gmiCH1,gmiCH2,gmiCH3,0);
                                hsMultiply.put(x,magneticRawData);
                                magneticRawDatas.add(magneticRawData);
                            }

                        }while(cursor.moveToNext());
                    }
                    cursor.close();
                }

                Iterator iterator = magneticRawDatas.iterator();
                while (iterator.hasNext()){
                    MagneticRawData magneticRawData = (MagneticRawData) iterator.next();
                    Log.d(TAG, "onClick: x" + magneticRawData.getX() + " y:" + magneticRawData.getY());
                    MainActivity.insertMageticData(writableDatabase,
                            magneticRawData.getX(),
                            magneticRawData.getY(),
                            magneticRawData.getGmiCH1(),
                            magneticRawData.getGmiCH2(),
                            magneticRawData.getGmiCH3(),
                            time);


                }
                Constant.FIX_CHOSEN.clear();
                Intent intent = new Intent(FixActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        Cursor cursor = writableDatabase.query("NameList",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String content = cursor.getString(cursor.getColumnIndex("name"));
                String label = cursor.getString(cursor.getColumnIndex("label"));
                if(Constant.FIX_ID != null){
                    if(!Constant.FIX_ID.equals(content)){
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
                    }
                }
                //Log.d(TAG, "onCreate: nameList:" + content);
            }while (cursor.moveToNext());
        }
        cursor.close();

        MagneticFixAdapter magneticFixAdapter = new MagneticFixAdapter(getApplicationContext(),R.layout.magnetic_fix_layout,magneticListInfos);
        ListView listView = findViewById(R.id.lv_choose);
        listView.setAdapter(magneticFixAdapter);

    }
}
