package com.example.graduatioproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTimeFragment extends Fragment {
    static TextView tvContent = null;
    CheckBox checkBoxParse = null;
    CheckBox checkBoxNextLine = null;
    static TextView tvDeviceState = null;
    static TextView tvRxBytes = null;
    Button btnReceive;
    private static boolean appendNextLine = false;
    private static boolean isParse = false;
    private static boolean isStartTransfer = false;
    private static int recvLength = 0;
    private static boolean devState = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_receiver_setting,container,false);
        tvContent = view.findViewById(R.id.tv_content);
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        checkBoxNextLine = view.findViewById(R.id.cb_next_line);
        checkBoxParse = view.findViewById(R.id.cb_parse);
        tvDeviceState = view.findViewById(R.id.tv_device_state);
        tvRxBytes = view.findViewById(R.id.tv_rx);
        btnReceive = view.findViewById(R.id.btn_if_receive);
        btnReceive.setBackgroundColor(0xff0099ff);
        btnReceive.setTextColor(0xffffffff);

        checkBoxNextLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    appendNextLine = true;
                }else{
                    appendNextLine = false;
                }
            }
        });

        checkBoxParse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isParse = true;
                }else{
                    isParse = false;
                }
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnReceive.getText().equals("开始接收")){
                    if(devState){
                        btnReceive.setText("正在接收");
                        btnReceive.setBackgroundColor(0xffff2200);
                        isStartTransfer = true;
                    }else{
                        isStartTransfer = false;
                        Toast.makeText(getContext(),"请先连接设备",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    btnReceive.setText("开始接收");
                    btnReceive.setBackgroundColor(0xff00AAff);
                    isStartTransfer = false;
                }
            }
        });

        return view;
    }



    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    int offset = 0;
                    String recvDat = msg.getData().getString("recv_dat");
                    recvLength += recvDat.length();
                    tvRxBytes.setText("Rx:" + recvLength + "Bytes");
                    recvDat = parseContent(isParse,recvDat);
                    recvDat = addNextLine(appendNextLine,recvDat);
                    tvContent.append(recvDat);
                    offset = tvContent.getLineCount() * tvContent.getLineHeight();
                    if (offset > tvContent.getHeight()) tvContent.scrollTo(0, offset - tvContent.getHeight());
                    break;

                case 1:
                    int deviceState = msg.getData().getInt("device_state");
                    if(deviceState == 0){
                        devState = false;
                        tvDeviceState.setText("设备:已断开");
                    }else{
                        devState = true;
                        tvDeviceState.setText("设备:已连接");
                    }
                    break;
            }
        }
    };

    private static String addNextLine(boolean flag,String content){
        if(flag){
            content += '\n';
        }
        return content;
    }

    private static int x,y,gmi_ch1_integer,gmi_ch2_integer,gmi_ch3_integer,other_length = 0;
    private static int gmi_ch1_decimal,gmi_ch2_decimal,gmi_ch3_decimal;
    private static String other_data = null;
    private static boolean isValid = false;
    private static String parseContent(boolean flag,String data){
        if(flag){
            String ret = "";

            if(other_length > 0){
                data = other_data + data;
            }
            if(data.length() >= 5 && data.startsWith("START")){
                isValid = true;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter  =  new   SimpleDateFormat   ("yyyy.MM.dd-HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                String time = formatter.format(curDate);
                ret += '\n' + " 开始测量\n" + "测量时间：" + time + '\n';
                data = data.substring(5);
            }

            while(isValid && data.length() >= 38){
                x = Integer.parseInt(data.substring(0,4));
                y = Integer.parseInt(data.substring(4,8));
                x -= 3000;
                y -= 3000;
                gmi_ch1_integer = Integer.parseInt(data.substring(8,12));
                gmi_ch2_integer = Integer.parseInt(data.substring(18,22));
                gmi_ch3_integer = Integer.parseInt(data.substring(28,32));
                gmi_ch1_integer -= 5000;
                gmi_ch2_integer -= 5000;
                gmi_ch3_integer -= 5000;
                gmi_ch1_decimal = Integer.parseInt(data.substring(12,18));
                gmi_ch2_decimal = Integer.parseInt(data.substring(22,28));
                gmi_ch3_decimal = Integer.parseInt(data.substring(32,38));
                ret += " 坐标：(" + x + "," + y + ").\n";
                ret += " x轴磁场强度：" + gmi_ch1_integer + "." + gmi_ch1_decimal + "\n";
                ret += " y轴磁场强度：" + gmi_ch2_integer + "." + gmi_ch2_decimal + "\n";
                ret += " z轴磁场强度：" + gmi_ch3_integer + "." + gmi_ch3_decimal + "\n";
                data = data.substring(38);
            }
            other_length = data.length();
            other_data = data;
            if(data.endsWith("STOP")){
                isValid = false;
                other_length = 0;
                other_data = null;
                ret += "\n" + " 测量结束" + "\n";
            }
            return ret;
        }else{
            return data;
        }
    }

    public static Handler getHandler(){
        return handler;
    }

    public static boolean getStartTransfer(){
        return isStartTransfer;
    }
}
