package com.example.graduatioproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduatioproject.ch340_library.driver.InitCH340;
import com.example.graduatioproject.contour.ContourCtrl;
import com.example.graduatioproject.contour.ContourPoint;
import com.example.graduatioproject.contour.MagneticRawData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String databaseName = "MageticDataDatabase.db";
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                case 2:
                    if(RealTimeFragment.getStartTransfer()){
                        String recv_dat = msg.getData().getString("data");
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("recv_dat",recv_dat);
                        message.setData(bundle);
                        message.what = 0;
                        Handler handlerSend = RealTimeFragment.getHandler();
                        if(handlerSend != null){
                            handlerSend.handleMessage(message);
                        }
                        //FileLog.fileLog(getApplicationContext(),recv_dat,"串口测试");
                        parseDataAndSaveToDataBase(recv_dat);
                        //Toast.makeText(MainActivity.this,"" + msg.getData().getString("data"),Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    public static Handler getHandler(){
        return handler;
    }


    private DatabaseHelper databaseHelper;
    static SQLiteDatabase writableDatabase;
    private static boolean isValid = false;
    private static String time = null;
    private static int x,y,gmi_ch1_integer,gmi_ch2_integer,gmi_ch3_integer,other_length = 0;

    private static String other_data = null;
    private static ArrayList<MagneticRawData> magneticRawDataArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);



        databaseHelper = new DatabaseHelper(this,databaseName,null,Constant.DATABASE_VERSION);

        //接收USB广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbStateChangeReceiver, filter);

        //请求写入外部文本文件权限
        if(Build.VERSION.SDK_INT >= 23){
            int REQUEST_CODE_CONTACT = 101;
            String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for(String str : permissions){
                if(MainActivity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED){
                    MainActivity.this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    break;
                }
            }
        }

        writableDatabase = databaseHelper.getWritableDatabase();


        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final RealTimeFragment realTimeFragment = new RealTimeFragment();
        final ReceiverFragment receiverFragment = new ReceiverFragment();
        final SettingFragment settingFragment = new SettingFragment();

        fragmentTransaction.add(R.id.main_activity_layout,realTimeFragment);
        fragmentTransaction.add(R.id.main_activity_layout,receiverFragment);
        fragmentTransaction.add(R.id.main_activity_layout,settingFragment);
        fragmentTransaction.show(realTimeFragment);
        fragmentTransaction.hide(receiverFragment);
        fragmentTransaction.hide(settingFragment);
        fragmentTransaction.commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.navigation_receiver:
                        getSupportFragmentManager().beginTransaction()
                                .hide(receiverFragment).hide(settingFragment).show(realTimeFragment)
                                .commit();

                        break;
                    case R.id.navigation_history:
                        getSupportFragmentManager().beginTransaction()
                                .hide(realTimeFragment).hide(settingFragment).show(receiverFragment)
                                .commit();
                        break;
                    case R.id.navigation_settings:
                        getSupportFragmentManager().beginTransaction()
                                .hide(realTimeFragment).hide(receiverFragment).show(settingFragment)
                                .commit();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    //在Activity被回收时调用
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * ch340插入、拔出的监听函数
     */
    private final BroadcastReceiver mUsbStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("device_state",1);//1代表连接上，0代表断开
            message.what = 1;
            message.setData(bundle);
            if(RealTimeFragment.getHandler() != null){
                RealTimeFragment.getHandler().sendMessage(message);
            }

            UsbDevice usbDevice = (UsbDevice)intent.getExtras().get("device");
            if(usbDevice != null && usbDevice.getProductId() == 29987 && usbDevice.getVendorId() == 6790){
                if(action == UsbManager.ACTION_USB_DEVICE_ATTACHED){
                    if(InitCH340.getDriver() == null){
                        InitCH340.initCH340(getApplicationContext(),handler);
                    }
                    if(InitCH340.isIsOpenDeviceCH340()){
                        Toast.makeText(getApplicationContext(),"ch340已打开",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"ch340打开失败",Toast.LENGTH_SHORT).show();
                    }
                    UsbDevice mUsbDevice = InitCH340.getUsbDevice();
                    if(mUsbDevice != null){
                    }
                }else if(action == UsbManager.ACTION_USB_DEVICE_DETACHED){

                }
            }


        }
    };

    public static void insertMageticData(SQLiteDatabase writableDatabase,int x,int y,int x_surface,int y_surface,int z_surface,String time){
        if(writableDatabase == null)return;
        ContentValues values = new ContentValues();
        values.put("x",x);
        values.put("y",y);
        values.put("x_surface",x_surface);
        values.put("y_surface",y_surface);
        values.put("z_surface",z_surface);
        values.put("time",time);
        writableDatabase.insert("MagneticData",null,values);
    }

    public static void insertNameList(String name){
        if(writableDatabase == null){
            return;
        }
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("label",name);

        writableDatabase.insert("NameList",null,values);
    }

    public static void parseDataAndSaveToDataBase(String data){
        if(other_length > 0){
            data = other_data + data;
        }
        if(data.length() >= 5 && data.startsWith("START")){
            isValid = true;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter  =  new   SimpleDateFormat   ("yyyyMMdd-HHmmss");
            Date curDate =  new Date(System.currentTimeMillis());
            time = formatter.format(curDate);
            insertNameList(time);
            data = data.substring(5);
        }

        while(isValid && data.length() >= 38){
            x = Integer.parseInt(data.substring(0,4));
            y = Integer.parseInt(data.substring(4,8));
            gmi_ch1_integer = Integer.parseInt(data.substring(8,14));
            gmi_ch2_integer = Integer.parseInt(data.substring(18,24));
            gmi_ch3_integer = Integer.parseInt(data.substring(28,34));
            insertMageticData(writableDatabase,x,y,gmi_ch1_integer,gmi_ch2_integer,gmi_ch3_integer,time);
            data = data.substring(38);
        }
        other_length = data.length();
        other_data = data;
        if(data.endsWith("STOP")){
            isValid = false;
            other_length = 0;
            other_data = null;
        }
    }
}
