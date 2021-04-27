package com.example.graduatioproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MagneticFixAdapter extends ArrayAdapter<MagneticListInfo> {
    private int resourceID;
    List<MagneticListInfo> nameList;
    private DatabaseHelper databaseHelper = null;
    private SQLiteDatabase writableDatabase;
    CheckBox checkBox;
    TextView tvTitle;
    TextView tvTime;
    private String TAG = "TestFix";

    public MagneticFixAdapter(Context context, int magnetic_list_layout, List<MagneticListInfo> nameList){
        super(context, magnetic_list_layout, nameList);
        resourceID = magnetic_list_layout;
        this.nameList = nameList;
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(getContext(),"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);
            writableDatabase = databaseHelper.getWritableDatabase();
        }
    }

    private MagneticListInfo magneticListInfo = null;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer,Boolean> checkBoxState = new HashMap<>();
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") final View view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        magneticListInfo = getItem(position);
        checkBox = view.findViewById(R.id.cb_chosen);
        tvTime = view.findViewById(R.id.tv_time);
        tvTitle = view.findViewById(R.id.tv_title);
        if (magneticListInfo != null) {
            tvTitle.setText(magneticListInfo.getName());
            tvTime.setText(magneticListInfo.getTime());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magneticListInfo = getItem(position);
                CheckBox cb = v.findViewById(R.id.cb_chosen);
                if(cb.isChecked()){
                    cb.setChecked(false);
                    checkBoxState.put(position,false);
                    Constant.FIX_CHOSEN.remove(magneticListInfo);
                    Log.d(TAG, "onClick: " + "移除");
                }else {
                    cb.setChecked(true);
                    checkBoxState.put(position,true);
                    Constant.FIX_CHOSEN.add(magneticListInfo);
                    Log.d(TAG, "onClick: " + "添加");
                }
            }
        });

        if(checkBoxState != null && checkBoxState.containsKey(position)){
            if(checkBox != null){
                checkBox.setChecked(checkBoxState.get(position));
            }

        }
        return view;
    }
}
