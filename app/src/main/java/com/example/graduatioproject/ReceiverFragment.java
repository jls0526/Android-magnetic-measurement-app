package com.example.graduatioproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.ContentValues.TAG;

public class ReceiverFragment extends Fragment {
    private List<MagneticListInfo> magneticListInfos = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_magnetic_list,container,false);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext(),"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);
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

                Log.d(TAG, "onCreate: nameList:" + content);
            }while (cursor.moveToNext());
        }
        cursor.close();

        MagneticDataAdapter magneticDataAdapter = new MagneticDataAdapter(getContext(),R.layout.magnetic_list_layout,magneticListInfos);
        ListView listView = view.findViewById(R.id.magnetic_list);
        listView.setAdapter(magneticDataAdapter);

        return view;
    }
}
