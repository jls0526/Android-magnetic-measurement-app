package com.example.graduatioproject.ch340_library.runnable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.graduatioproject.FileLog;
import com.example.graduatioproject.MainActivity;
import com.example.graduatioproject.ch340_library.driver.InitCH340;
import com.example.graduatioproject.ch340_library.logger.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by xpf on 2017/12/20.
 * Function:ReadDataRunnable
 */
public class ReadDataRunnable implements Runnable {

    private static final String TAG = "ReadDataRunnable";
    private boolean mStop = false; // 是否停止线程
    private String time = null;
    private boolean isValid = false;
    Handler mHandler;

    @Override
    public void run() {
        startReadThread();
    }

    public ReadDataRunnable(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /**
     * 开启读取数据线程
     */
    private void startReadThread() {
//        File file = new File("log.txt");
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        while (!mStop) {
            byte[] receiveBuffer = new byte[1024];// 接收数据数组
            // 读取缓存区的数据长度
            int length = InitCH340.getDriver().ReadData(receiveBuffer, 1024);
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream("log.txt");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

            switch (length) {
                case 0: // 无数据
                    LogUtils.i(TAG, "No data~");
                    break;
                default: // 有数据时的处理
                    String data = new String(receiveBuffer);
//                    byte[] bytes = data.getBytes();
//                    try {
//                        fos.write(bytes);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    //MainActivity.parseDataAndSaveToDataBase(data);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    data = data.substring(0,length);
                    bundle.putString("data",data);
                    bundle.putInt("data_length",length);
                    msg.setData(bundle);
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止读取任务
     */
    public void stopTask() {
        mStop = true;
    }

}
