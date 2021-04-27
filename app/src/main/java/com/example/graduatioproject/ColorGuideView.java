package com.example.graduatioproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorGuideView extends View {
    private Paint paint;
    private int viewHeight;
    private int viewWidth;
    private Bitmap bitmap;

    public ColorGuideView(Context context) {
        super(context);
    }

    public ColorGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColorGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
        bitmap = Bitmap.createBitmap(viewWidth,viewHeight,Bitmap.Config.ARGB_8888);
        bitmap.setPixels(getColorGuide(viewWidth,viewHeight),0,viewWidth,0,0,viewWidth,viewHeight);
        canvas.drawBitmap(bitmap,0,0,paint);
        super.onDraw(canvas);
    }

    private void init(){
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    //画颜色条
    private int[] getColorGuide(int width,int height){
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
}
