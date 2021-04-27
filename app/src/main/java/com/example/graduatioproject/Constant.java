package com.example.graduatioproject;

import java.util.ArrayList;

public class Constant {
    public static String CHOSEN_ID = null;
    public static int MAGNETIC_DIR = 0;//0表示x轴，1表示y轴，2表示z轴,3表示总磁场强度
    public static int DATABASE_VERSION = 4;
    public static String LIST_NAME = null;
    public static String BAUD_RATE = "baud_rate";
    public static String STOP_BITS = "stop_bits";
    public static String DATA_BITS = "data_bits";
    public static String CORRECT_BITS = "correct_bits";
    public static String FIX_ID = null;
    public static ArrayList<MagneticListInfo> FIX_CHOSEN = new ArrayList<>();
}
