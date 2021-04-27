package com.example.graduatioproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLog {
    static String fileName = null;
    public static void fileLog(Context context,String content, String fileName){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/毕业设计/LOG/";
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        File file2 = new File(dir + fileName + ".txt");
        if(!file2.exists()){
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(file2,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
