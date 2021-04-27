package com.example.graduatioproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduatioproject.ch340_library.driver.InitCH340;

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    Spinner spinnerBaudRate = null;
    Spinner spinnerDataBits = null;
    Spinner spinnerCorrectBits = null;
    Spinner spinnerStopBits = null;
    int[] baudRate = new int[]{2400,4800,9600,14400,19200,38400,57600,76800,115200,230400,460800,500000,921600,1000000,2000000,3000000};
    byte[] stopBytes = new byte[]{1,2};
    byte[] dataBytes = new byte[]{8,7,6,5};
    byte[] correctBytes = new byte[]{0,1,2};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        spinnerBaudRate = view.findViewById(R.id.sp_baud_rates);
        spinnerCorrectBits = view.findViewById(R.id.sp_correct_bits);
        spinnerDataBits = view.findViewById(R.id.sp_data_bits);
        spinnerStopBits = view.findViewById(R.id.sp_stop_bits);

        SharedPreferences pref = getContext().getSharedPreferences("serial_setting",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
//        editor.putInt(Constant.BAUD_RATE,8);
//        editor.putInt(Constant.DATA_BITS,0);
//        editor.putInt(Constant.STOP_BITS,0);
//        editor.putInt(Constant.CORRECT_BITS,0);
//        editor.apply();
        if(pref.getInt(Constant.BAUD_RATE,-1) == -1){
            editor.putInt(Constant.BAUD_RATE,8);
            editor.putInt(Constant.DATA_BITS,0);
            editor.putInt(Constant.STOP_BITS,0);
            editor.putInt(Constant.CORRECT_BITS,0);
            editor.apply();
        }

        spinnerBaudRate.setSelection(pref.getInt(Constant.BAUD_RATE,0));
        spinnerStopBits.setSelection(pref.getInt(Constant.STOP_BITS,0));
        spinnerCorrectBits.setSelection(pref.getInt(Constant.CORRECT_BITS,0));
        spinnerDataBits.setSelection(pref.getInt(Constant.DATA_BITS,0));
        InitCH340.baudRate = baudRate[pref.getInt(Constant.BAUD_RATE,0)];
        InitCH340.stopBit = stopBytes[pref.getInt(Constant.STOP_BITS,0)];
        InitCH340.parity = correctBytes[pref.getInt(Constant.CORRECT_BITS,0)];
        InitCH340.dataBit = dataBytes[pref.getInt(Constant.DATA_BITS,0)];
        initCH340(getContext());

        spinnerStopBits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(Constant.STOP_BITS,position);
                editor.apply();
                InitCH340.stopBit = stopBytes[position];
                initCH340(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBaudRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(Constant.BAUD_RATE,position);
                editor.apply();
                InitCH340.baudRate = baudRate[position];
                initCH340(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDataBits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(Constant.DATA_BITS,position);
                editor.apply();
                InitCH340.dataBit = dataBytes[position];
                initCH340(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCorrectBits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(Constant.CORRECT_BITS,position);
                editor.apply();
                InitCH340.parity = correctBytes[position];
                initCH340(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void initCH340(Context context){
        InitCH340.initCH340(context,MainActivity.getHandler());
    }
}
