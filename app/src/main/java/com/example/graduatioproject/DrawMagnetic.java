package com.example.graduatioproject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import com.example.graduatioproject.contour.ContourCtrl;
import com.example.graduatioproject.contour.ContourPoint;
import com.example.graduatioproject.contour.MagneticRawData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.graduatioproject.MainActivity.writableDatabase;

public class DrawMagnetic {

    /**
     * 绘制磁场强度分布图，带颜色，带等值线
     * @param canvas
     * @param contourPoints
     * @param distLeft 离canvas左边多少像素
     * @param distTop 离canvas上边多少像素
     * @param width 磁场强度分布图的宽度，单位是像素，高度自动计算，不需要输入
     */
    public static void drawMagneticColor(Canvas canvas, ContourPoint[][] contourPoints,int distLeft,int distTop,int width,int quality){
        Bitmap bitmap;

        if(contourPoints == null){
            return;
        }
        int xLength = contourPoints[0].length;
        int yLength = contourPoints.length;
        if(xLength*yLength < 1){
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        int pixelStride = width / (xLength - 1);
        int xPixel = (xLength - 1)*pixelStride;
        int yPixel = (yLength - 1)*pixelStride;

        int[] colorFill = ContourCtrl.getPixelValues(contourPoints,pixelStride,quality);
        bitmap = Bitmap.createBitmap(xPixel,yPixel, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(colorFill,0,xPixel,0,0,xPixel,yPixel);
        canvas.drawBitmap(bitmap,distLeft,distTop,paint);

        //画矩形，等值线图外面的框
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(distLeft,distTop,xPixel + distLeft,yPixel + distTop,paint);


        //画x,y坐标系以及刻度
        //画x轴
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25);
        paint.setStyle(Paint.Style.FILL);
        int startAxisX = distLeft;
        int startAxisY = (yLength - 1)*pixelStride + distTop;
        int stopAxisX = startAxisX;
        int stopAxisY = startAxisY + 15;
        for(int i = 0;i <= xLength/quality;i++){
            canvas.drawLine(startAxisX,startAxisY,stopAxisX,stopAxisY,paint);
            canvas.drawText(String.valueOf(i),stopAxisX,stopAxisY + 20,paint);
            startAxisX += pixelStride*quality;
            stopAxisX += pixelStride*quality;
        }
        //画y轴
        startAxisX = distLeft;
        startAxisY = (yLength - 1)*pixelStride + distTop;
        stopAxisX = startAxisX - 15;
        stopAxisY = startAxisY;
        for(int i = 0;i <= yLength/quality;i++){
            canvas.drawLine(startAxisX,startAxisY,stopAxisX,stopAxisY,paint);
            canvas.drawText(String.valueOf(i),stopAxisX - 15,stopAxisY + 10,paint);
            startAxisY -= pixelStride*quality;
            stopAxisY -= pixelStride*quality;
        }
        //画单位
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(36);
        canvas.drawText("单位:米",width,(yLength - 1)*pixelStride + distTop + 80,paint);
        drawMagneticLine(canvas,contourPoints,distLeft,distTop,pixelStride);
    }

    private static void drawMagneticLine(Canvas canvas,ContourPoint[][] contourPoints,int distLeft,int distTop,int pixelStride){
        //绘制等值线
        int xLength = contourPoints[0].length;
        int yLength = contourPoints.length;
        if(xLength*yLength < 1){
            return;
        }
        int[][] contourValues = new int[yLength][xLength];
        int valMin = Integer.MAX_VALUE;
        int valMax = Integer.MIN_VALUE;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        for(int i = 0;i < yLength;i++){
            for(int j = 0;j < xLength;j++){
                contourValues[i][j] = contourPoints[i][j].getValue();
                if(contourValues[i][j] < valMin){
                    valMin = contourValues[i][j];
                }else if(contourValues[i][j] > valMax){
                    valMax = contourValues[i][j];
                }
            }
        }
        int valRange = valMax - valMin;
        for(int i = valMin + valRange/10;i <= valMax;i+=valRange/6){
            ArrayList<ContourPoint> arrayList = ContourCtrl.CoutourPointSearch(contourValues,pixelStride,i);
            ArrayList<ContourPoint> res = ContourCtrl.drawContourLine(arrayList,xLength,yLength,pixelStride);
            for(ContourPoint con : res){
                int startX = con.getX() + distLeft;
                int startY = con.getY() + distTop;
                int stopX = distLeft;
                int stopY = distTop;
                boolean firstFlag = true;
                ContourPoint conStart = null;
                while((con != null)){
                    if(con == conStart){
                        stopX = con.getX() + distLeft;
                        stopY = con.getY() + distTop;
                        canvas.drawLine(startX,startY,stopX,stopY,paint);
                        break;
                    }
                    if(firstFlag){
                        firstFlag = false;
                        conStart = con;
                    }
                    stopX = con.getX() + distLeft;
                    stopY = con.getY() + distTop;
                    canvas.drawLine(startX,startY,stopX,stopY,paint);
                    startX = stopX;
                    startY = stopY;
                    con = con.getNext();
                }

            }
        }
    }

    public static void drawMagneticColorGuide(Canvas canvas,ContourPoint[][] contourPoints,int distLeft,int distTop){

        if(contourPoints == null){
            return;
        }
        int xLength = contourPoints[0].length;
        int yLength = contourPoints.length;
        if(xLength*yLength < 1){
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);



        int[][] contourValues = new int[yLength][xLength];
        int valMin = Integer.MAX_VALUE;
        int valMax = Integer.MIN_VALUE;
        for(int i = 0;i < yLength;i++){
            for(int j = 0;j < xLength;j++){
                contourValues[i][j] = contourPoints[i][j].getValue();
                if(contourValues[i][j] < valMin){
                    valMin = contourValues[i][j];
                }else if(contourValues[i][j] > valMax){
                    valMax = contourValues[i][j];
                }
            }
        }
        int valRange = valMax - valMin;

        //画颜色条
        int cWidth = 36;
        int cHeight = 600;
        Bitmap bitmapDrawColorGuide;
        bitmapDrawColorGuide = Bitmap.createBitmap(cWidth,cHeight,Bitmap.Config.ARGB_8888);
        bitmapDrawColorGuide.setPixels(getColorGuide(cWidth,cHeight),0,cWidth,0,0,cWidth,cHeight);

        int startLineX = distLeft + cWidth;
        int stopLineX = startLineX + 10;
        int startAndStopLineY = distTop;
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawBitmap(bitmapDrawColorGuide,distLeft,distTop,paint);
        canvas.drawRect(distLeft + 1,distTop,cWidth + distLeft,distTop + cHeight,paint);

        double showData = valMax/1000.0;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(30);
        paint.setStyle(Paint.Style.FILL);
        for(int i = 0;i < 9;i++){
            canvas.drawLine(startLineX,startAndStopLineY,stopLineX,startAndStopLineY,paint);
            //Log.d(TAG, "onDraw: valmindouble:" + String.format("%.2f",valMin/1000.0));
            canvas.drawText(String.format("%.2f",showData),stopLineX + 10,startAndStopLineY + 10,paint);
            showData -= valRange/8000.0;
            startAndStopLineY += cHeight/8;
        }
        paint.setTextSize(40);
        canvas.drawText("H(uT)",distLeft,distTop - 20,paint);
    }

    private static int[] getColorGuide(int width,int height){
        int[] ret = new int[width*height];
        int div = height / 4;
        double colorDiv = (double) 0xff / div;
        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                if(i < height/4){
                    ret[i*width + j] = 0xffff0000;//R=0xff
                    ret[i*width + j] += (int)(i*colorDiv) << 8;
                }else if(i < height/2){
                    int temp = i - height/4;
                    ret[i*width + j] = 0xffffff00;//R=0xff,G=0xff
                    ret[i*width + j] -= (int)(temp*colorDiv) << 16;
                }else if(i < (height*3/4)){
                    int temp = i - height/2;
                    ret[i*width + j] = 0xff00ff00;//G=0xff
                    ret[i*width + j] += (int)(temp*colorDiv);
                }else{
                    int temp = i - (height*3/4);
                    ret[i*width + j] = 0xff00ffff;//G=0xff,B=0xff
                    ret[i*width + j] -= (int)(temp*colorDiv) << 8;
                }
            }
        }
        return ret;
    }

    public static void outputMagneticPicture(Context context, int width,String name,int quality){
        DatabaseHelper databaseHelper = new DatabaseHelper(context, "MageticDataDatabase.db", null, Constant.DATABASE_VERSION);
        writableDatabase = databaseHelper.getWritableDatabase();
        if(writableDatabase == null){
            Log.d(TAG, "onClick: 不能写入数据库");
        }else {
            Log.d(TAG, "onClick: 可以写入数据库");
        }

        Cursor cursor = writableDatabase.query("MagneticData",new String[]{"x","y","x_surface","y_surface","z_surface","time"},"time=?",new String[]{Constant.CHOSEN_ID},null,null,null);
        ArrayList<MagneticRawData> magneticRawDatas = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int x = Integer.parseInt(cursor.getString(cursor.getColumnIndex("x")));
                int y = Integer.parseInt(cursor.getString(cursor.getColumnIndex("y")));
                int gmiCH1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("x_surface")));
                int gmiCH2 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("y_surface")));
                int gmiCH3 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("z_surface")));
                //(722809-gmichi)/0.05878
                gmiCH1 /= 100;
                gmiCH2 /= 100;
                gmiCH3 /= 100;
                gmiCH1 = (int)((7228 - gmiCH1)/0.05878);
                //Log.d(TAG, "onDraw: GMICH1:" + gmiCH1);
                gmiCH2 = (int)((18511 - gmiCH2)/0.05667);
                //Log.d(TAG, "onDraw: GMICH2:" + gmiCH2);
                gmiCH3 = (int)((9055 - gmiCH3)/0.02324);

                int gmiTotal = (int)Math.sqrt(Math.pow(gmiCH1,2) + Math.pow(gmiCH2,2) + Math.pow(gmiCH3,2));
                //Log.d(TAG, "onDraw: GMITOTAL:" + gmiTotal);
                MagneticRawData magneticRawData = new MagneticRawData(x,y,gmiCH1,gmiCH2,gmiCH3,gmiTotal);
                magneticRawDatas.add(magneticRawData);
            }while(cursor.moveToNext());
        }
        cursor.close();

        ContourPoint[][] contourPoints = ContourCtrl.interPolationRawData(magneticRawDatas, width,quality);

        //pointLength = Math.max(contourPoints[0].length,contourPoints.length);

        int stride = width/contourPoints[0].length;
        int height = stride*contourPoints.length;
        Bitmap bitmapPic = Bitmap.createBitmap(width + 400,height + 300,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapPic);
        drawCanvas(canvas,contourPoints,width,name,quality);

        //保存磁场强度分布图到图片
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "drawMagnetic: 无法读写图片");
        }else{
            Log.d(TAG, "drawMagnetic: 可以输出图片");
            //通过UUID生成字符串文件名
            //String fileName1 = UUID.randomUUID().toString();
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/毕业设计/磁场强度图片/";
            try {
                File file = new File(dir);
                if(!file.exists()){
                    file.mkdirs();
                }
                File file2 = new File(dir + name + ".jpg");
                if(!file2.exists()){
                    try {
                        file2.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FileOutputStream fop = new FileOutputStream(file2);
                bitmapPic.compress(Bitmap.CompressFormat.JPEG,100,fop);
                try {
                    fop.flush();
                    fop.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void drawCanvas(Canvas canvas,ContourPoint[][] contourPoints,int width,String name,int quality) {
        canvas.drawColor(0xffffffff);//背景为白色
        DrawMagnetic.drawMagneticColor(canvas,contourPoints,100,150,width,quality);
        DrawMagnetic.drawMagneticColorGuide(canvas,contourPoints,width + 150,100);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.FILL);

        int left = width/2 + 100;
        canvas.drawText(name,left,100,paint);
    }
}
