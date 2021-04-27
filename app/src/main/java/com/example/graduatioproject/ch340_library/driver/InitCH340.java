package com.example.graduatioproject.ch340_library.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.widget.Toast;

import com.example.graduatioproject.ch340_library.logger.LogUtils;
import com.example.graduatioproject.ch340_library.runnable.ReadDataRunnable;


import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import cn.wch.ch34xuartdriver.CH34xUARTDriver;


/**
 * Created by xpf on 2017/11/22.
 * Function:初始化 ch340 驱动
 */
public class InitCH340 {

    private static final String TAG = InitCH340.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.linc.USB_PERMISSION";
    private static final ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    public static byte parity = 0;
    public static byte stopBit = 1;
    public static byte dataBit = 8;
    public static int baudRate = 115200;
    private static final byte flowControl = 0;
    @SuppressLint("StaticFieldLeak")
    private static CH34xUARTDriver driver;
    private static boolean isOpenDeviceCH340 = false;
    private static boolean isUsbHostSupported = false;
    private static boolean isHasPermission = false;
    private static ReadDataRunnable readDataRunnable;
    private static UsbManager mUsbManager;
    private static IUsbPermissionListener listener;
    private static UsbDevice mUsbDevice;

    public static UsbDevice getUsbDevice() {
        return mUsbDevice;
    }

    public static void setListener(IUsbPermissionListener listener) {
        InitCH340.listener = listener;
    }

    /**
     * initialize ch340 parameters.
     *
     * @param context Application context.
     */
    public static void initCH340(Context context, Handler handler) {
        if (context == null) return;
        //Toast.makeText(context,"ch340 found",Toast.LENGTH_SHORT).show();
        Context appContext = context.getApplicationContext();
        mUsbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
            LogUtils.e(TAG, "deviceHashMap.size()=" + deviceHashMap.size());
            for (UsbDevice device : deviceHashMap.values()) {
                LogUtils.i(TAG, "ProductId:" + device.getProductId() + ",VendorId:" + device.getVendorId());
                if (device.getProductId() == 29987 && device.getVendorId() == 6790) {
                    //Toast.makeText(context,"ch3401 found",Toast.LENGTH_SHORT).show();
                    mUsbDevice = device;
                    if (mUsbManager.hasPermission(device)) {
                        isHasPermission = true;
                        //Toast.makeText(context,"ch3402 found",Toast.LENGTH_SHORT).show();
                        loadDriver(appContext, mUsbManager,handler);
                    } else {
                        if (listener != null) {
                            listener.result(false);
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * load ch340 driver.
     *
     * @param appContext
     * @param usbManager
     */
    public static void loadDriver(Context appContext, UsbManager usbManager,Handler handler) {
        driver = new CH34xUARTDriver(usbManager, appContext, ACTION_USB_PERMISSION);
        // 判断系统是否支持USB HOST
        if (!driver.UsbFeatureSupported()) {

            LogUtils.e(TAG, "Your mobile phone does not support USB HOST, please change other phones to try again!");
        } else {
            isUsbHostSupported = true;
            openCH340(handler);
        }
    }

    /**
     * config and open ch340.
     */
    private static void openCH340(Handler handler) {
        int ret_val = driver.ResumeUsbList();
        LogUtils.d(TAG, ret_val + "");
        // ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
        if (ret_val == -1) {
            LogUtils.d(TAG, ret_val + "Failed to open device!");
            driver.CloseDevice();
        } else if (ret_val == 0) {
            if (!driver.UartInit()) {  //对串口设备进行初始化操作
                LogUtils.d(TAG, ret_val + "Failed device initialization!");
                LogUtils.d(TAG, ret_val + "Failed to open device!");
                return;
            }
            LogUtils.d(TAG, ret_val + "Open device successfully!");
            if (!isOpenDeviceCH340) {
                isOpenDeviceCH340 = true;
                configParameters(handler);//配置ch340的参数、需要先配置参数
            }
        } else {
            LogUtils.d(TAG, "The phone couldn't find the device！");
        }
    }

    /**
     * config ch340 parameters.
     * 配置串口波特率，函数说明可参照编程手册
     */
    private static void configParameters(Handler handler) {
        boolean isSetConfig = driver.SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
        if (isSetConfig) {
            LogUtils.i(TAG, "Serial port Settings success~");
            if (readDataRunnable == null) {
                readDataRunnable = new ReadDataRunnable(handler);
            }
            mThreadPool.execute(readDataRunnable);
        } else {
            LogUtils.e(TAG, "Serial port Settings failed！");
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdownThreadPool() {
        if (!mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
    }

    /**
     * ch340 is or not open.
     *
     * @return
     */
    public static boolean isIsOpenDeviceCH340() {
        return isOpenDeviceCH340;
    }

    public static boolean isIsUsbHostSupported(){
        return isUsbHostSupported;
    }

    public static boolean isIsHasPermission(){
        return isHasPermission;
    }

    /**
     * get ch340 driver.
     *
     * @return
     */
    public static CH34xUARTDriver getDriver() {
        return driver;
    }

    public static UsbManager getmUsbManager() {
        return mUsbManager;
    }

    public interface IUsbPermissionListener {
        void result(boolean isGranted);
    }
}
