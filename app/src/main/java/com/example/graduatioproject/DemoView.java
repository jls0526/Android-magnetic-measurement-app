package com.example.graduatioproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.graduatioproject.contour.ContourCtrl;
import com.example.graduatioproject.contour.ContourPoint;
import com.example.graduatioproject.contour.MagneticRawData;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;

public class DemoView extends View {
    private Paint paint;
    private int viewLength;
    private int xLength,yLength;
    private int pixelStride;
    private static SQLiteDatabase writableDatabase;
    private DatabaseHelper databaseHelper;
    private Context context;
    Path bPath;
    private ContourPoint[][] contourPoints = null;
    private int axisOffset;
    private int otherOffset;

    public DemoView(Context context) {
        super(context);
        this.context = context;
    }

    public DemoView(Context context,@Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DemoView(Context context,@Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置正方形view
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.e("tag", "onMeasure: widthMode=" + widthMode + " heightMode=" + heightMode);
        //如果宽度指定特定值，并且高度未指定特定值（让高度等于宽度就保证了宽高相等）
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width;
            if (heightMode == MeasureSpec.AT_MOST) {//这里还考虑了高度受上限的情况（比如父容器固定了高度）
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint({"DrawAllocation", "DefaultLocale", "WrongThread"})
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvasInit();
        drawMagnetic(canvas);
        //DrawMagnetic.outputMagneticPicture(getContext(),3500);

    }

    private void canvasInit(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        //setLayerType(LAYER_TYPE_HARDWARE, null);

        axisOffset = 100;
        otherOffset = 15;//离上边和右边的距离
        viewLength = this.getWidth() - axisOffset - otherOffset;//1024
        //Log.d(TAG, "onDraw: Width:" + getWidth() + " Height:" + getHeight());
//        bPath = new Path();
//        bPath.moveTo(100,100);
//        bPath.quadTo(200,100,100,200);
//        bPath.rQuadTo(-50,50,0,100);//与前一个点的相对坐标
//        bPath.rQuadTo(50,50,50,50);
//        canvas.drawPath(bPath,paint);

        databaseHelper = new DatabaseHelper(context,"MageticDataDatabase.db",null,Constant.DATABASE_VERSION);
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
                Log.d(TAG, "onDraw: GMICH3:" + gmiTotal);
                MagneticRawData magneticRawData = new MagneticRawData(x,y,gmiCH1,gmiCH2,gmiCH3,gmiTotal);
                magneticRawDatas.add(magneticRawData);
            }while(cursor.moveToNext());
        }
        cursor.close();

        contourPoints = ContourCtrl.interPolationRawData(magneticRawDatas,viewLength,4);

        //pointLength = Math.max(contourPoints[0].length,contourPoints.length);

        xLength = contourPoints[0].length;
        yLength = contourPoints.length;
        pixelStride = viewLength / (Math.max(xLength,yLength) - 1);
        //Log.d(TAG, "onDraw: 每个点像素间隔" + pixelStride);

    }


    @SuppressLint("WrongThread")
    private void drawMagnetic(Canvas canvas){
        int distLeft = 60;
        DrawMagnetic.drawMagneticColor(canvas,contourPoints,distLeft,20,getWidth() - distLeft*2,4);
        DrawMagnetic.drawMagneticColorGuide(canvas,contourPoints,distLeft,getHeight() - 1000);
    }

}
