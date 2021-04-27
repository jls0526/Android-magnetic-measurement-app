package com.example.graduatioproject.contour;

import android.util.Log;

import com.example.graduatioproject.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class ContourCtrl {

    public enum DIR{
        FROM_TOP,
        FROM_BOTTOM,
        FROM_LEFT,
        FROM_RIGHT,
        FROM_NULL
    };
    /**
     * 计算出某一个值所有的等值点
     * @param nums 数据数组，保存的磁场数据
     * @param stride 每个数据之间的间隔，单位是像素。
     * @param value 等值点的值
     * @return 每一个等值点的实际坐标
     *
     * 问题：需将stride转换成float类型
     */
    public static ArrayList<ContourPoint> CoutourPointSearch(int [][]nums, int stride, int value){

        ArrayList<ContourPoint> arrayList = new ArrayList<>();
        int lengthR = nums.length;
        int lengthC = nums[0].length;
        int[][] nums_temp = new int[lengthR + 1][lengthC + 1];
        for(int i = 0;i < lengthR;i++){
            for(int j = 0;j < lengthC;j++){
                nums_temp[i][j] = nums[i][j];
            }
        }

        for(int i = 0;i < lengthR;i++){
            nums_temp[i][lengthC] = nums[i][lengthC - 1];
            //nums_temp[lengthC][i] = nums[lengthC - 1][i];
        }
        for(int i = 0;i < lengthC;i++){
            nums_temp[lengthR][i] = nums[lengthR - 1][i];
        }


        for(int i = 0;i < lengthR;i++){
            for(int j = 0;j < lengthC;j++){
                //第一步，修正值。即当nums数组中有与value相同的值，将nums+1
                if(nums_temp[i][j] == value){
                    nums_temp[i][j]++;
                }
                //第二步，
                int x,y;
                int offset = 0;
                //Log.d(TAG, "CoutourPointSearch: i,j:" + nums_temp[i][j] + " i+1,j:" + nums_temp[i + 1][j] + " res:" + (((long)(nums_temp[i][j] - value))*((long)(nums_temp[i + 1][j] - value)) < 0));
                if(((long)(nums_temp[i][j] - value))*((long)(nums_temp[i + 1][j] - value)) < 0){
                    //Log.d(TAG, "CoutourPointSearch: 第一个值：" + nums_temp[i][j] + " 第二个值：" + nums_temp[i + 1][j]);
                    if(nums_temp[i][j] < value){
                        offset = (int)((float)(value - nums_temp[i][j])/(float)(nums_temp[i + 1][j] - nums_temp[i][j])*stride);
                        if(offset == 0){
                            offset = 1;
                        }
                        x = j*stride;
                        y = i*stride + offset;
                        ContourPoint contourPoint = new ContourPoint(value,x,y);
                        //Log.d(TAG, "CoutourSearch: x:" + x + "y:" + y);
                        arrayList.add(contourPoint);
                    }else if(nums_temp[i][j] > value){
                        offset = (int)((float)(nums_temp[i][j] - value)/(float)(nums_temp[i][j] - nums_temp[i + 1][j])*stride);
                        if(offset == 0){
                            offset = 1;
                        }
                        x = j*stride;
                        y = i*stride + offset;
                        ContourPoint contourPoint = new ContourPoint(value,x,y);
                        //Log.d(TAG, "CoutourSearch: x:" + x + "y:" + y);
                        arrayList.add(contourPoint);
                    }
                    //Log.d(TAG, "CoutourPointSearch: offset:" + offset);
                }
                //Log.d(TAG, "CoutourPointSearch: i,j:" + nums_temp[i][j] + " i,j+1:" + nums_temp[i][j + 1] + " res:" + (((long)(nums_temp[i][j] - value))*((long)(nums_temp[i][j + 1] - value)) < 0));
                if(((long)(nums_temp[i][j] - value))*((long)(nums_temp[i][j + 1] - value)) < 0){
                    //Log.d(TAG, "CoutourPointSearch: 第一个值：" + nums_temp[i][j] + " 第二个值：" + nums_temp[i + 1][j]);
                    if(nums_temp[i][j] < value){
                        offset = (int)((float)(value - nums_temp[i][j])/(float)(nums_temp[i][j + 1] - nums_temp[i][j])*stride);
                        if(offset == 0){
                            offset = 1;
                        }
                        x = j*stride + offset;
                        y = i*stride;
                        ContourPoint contourPoint = new ContourPoint(value,x,y);
//                        Log.d(TAG, "CoutourSearch: x:" + x + " y:" + y + " num:" + nums[i][j] + " num2:" + nums_temp[i][j + 1] + " value:" + value
//                         + " 乘积：" + (nums_temp[i][j] - value)*(nums_temp[i][j + 1] - value));
                        arrayList.add(contourPoint);
                    }else if(nums_temp[i][j] > value){
                        offset = (int)((float)(nums_temp[i][j] - value)/(float)(nums_temp[i][j] - nums_temp[i][j + 1])*stride);
                        if(offset == 0){
                            offset = 1;
                        }
                        x = j*stride + offset;
                        y = i*stride;
                        ContourPoint contourPoint = new ContourPoint(value,x,y);
                        //Log.d(TAG, "CoutourSearch: x:" + x + "y:" + y);
                        arrayList.add(contourPoint);
                    }
                    //Log.d(TAG, "CoutourPointSearch: offset:" + offset);
                }

            }
        }
        //Log.d(TAG, "等值线搜索: OK");
        return arrayList;
    }

    public static ArrayList<ContourPoint> drawContourLine(ArrayList<ContourPoint> contourPoints,int x_length,int y_length,int stride){
        //1.开曲线
        ContourPoint contourPointStart = null;
        ContourPoint curContourPoint = null;
        ArrayList<ContourPoint> retContourLines = new ArrayList<>();
        int val = 0;
        ArrayList<ContourPoint> borderPoints = new ArrayList<>();
        for (ContourPoint temp : contourPoints) {
            if (temp.getX() == 0 || temp.getY() == 0 ||
                    temp.getX() == (x_length - 1) * stride ||
                    temp.getY() == (y_length - 1) * stride) {
//                contourPointStart = temp;
//                retContourLines.add(contourPointStart);
//                curContourPoint = contourPointStart;
//                contourPointStart.setPre(contourPointStart);
                borderPoints.add(temp);
                val = temp.getValue();
            }
        }
        //Log.d(TAG, "drawContourLine: value:" + val + " borderPoints nums；" + borderPoints.size());
//        contourPoints.remove(contourPointStart);

        while (borderPoints.size() > 0){
            //获取等值点头,保存在contourPointStart变量中
            Iterator iterator = borderPoints.iterator();
            if(iterator.hasNext()){
                contourPointStart = (ContourPoint) iterator.next();
                retContourLines.add(contourPointStart);
                curContourPoint = contourPointStart;
                contourPointStart.setPre(contourPointStart);
                contourPoints.remove(contourPointStart);
                borderPoints.remove(contourPointStart);
            }

            DIR curDIR;
            //true表示那一边有等值点
            ContourPoint nextLeft = null,nextRight = null,nextTop = null,nextBottom = null;
            if (curContourPoint != null && curContourPoint.getX() == 0) {
                curDIR = DIR.FROM_LEFT;
            }else if (curContourPoint != null && curContourPoint.getY() == 0) {
                curDIR = DIR.FROM_TOP;
            }else if (curContourPoint != null && curContourPoint.getX() == (x_length - 1) * stride) {
                curDIR = DIR.FROM_RIGHT;
            }else{
                curDIR = DIR.FROM_BOTTOM;
            }
//            Log.d(TAG, "drawContourLine: curDIR:" + curDIR.toString());
            //获取一条等值线
            while (contourPoints.size() > 0 && curContourPoint != null){
//                Log.d(TAG, "drawContourLine: contourPoints.size():" + contourPoints.size());
                Iterator it2 = contourPoints.iterator();
                ContourPoint nextContourPoint;
                boolean findNext = false;
                while (it2.hasNext()){
                    nextContourPoint = (ContourPoint) it2.next();
                    int xIndex,yIndex;
                    xIndex = curContourPoint.getX()/stride;
                    yIndex = curContourPoint.getY()/stride;

                    if ((curDIR == DIR.FROM_TOP) || (curDIR == DIR.FROM_LEFT)) {
                        //从左边出
                        if((nextContourPoint.getX() == xIndex*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从右边出
                        else if((nextContourPoint.getX() == (xIndex+1)*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                           // Log.d(TAG, "drawContourLine: enter nextRight x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextRight = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;

                        }//从下边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex + 1)*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextBottom x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextBottom = nextContourPoint;
                        }
                    }else if(curDIR == DIR.FROM_RIGHT){
                        //从左边出
                        if((nextContourPoint.getX() == (xIndex - 1)*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() > (xIndex-1)*stride)&&
                                (nextContourPoint.getX() < xIndex*stride)&&
                                (nextContourPoint.getY() == yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;

                        }//从下边出
                        else if((nextContourPoint.getX() > (xIndex-1)*stride)&&
                                (nextContourPoint.getX() < xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex + 1)*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextBottom x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextBottom = nextContourPoint;
                        }
                    }else {
                        //从左边出
                        if((nextContourPoint.getX() == xIndex*stride)&&
                                (nextContourPoint.getY() > (yIndex - 1)*stride)&&
                                (nextContourPoint.getY() < yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从右边出
                        else if((nextContourPoint.getX() == (xIndex+1)*stride)&&
                                (nextContourPoint.getY() > (yIndex - 1)*stride)&&
                                (nextContourPoint.getY() < yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextRight x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextRight = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex-1)*stride)){
                            //Log.d(TAG, "drawContourLine: enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;
                        }
                    }


                    if(curDIR == DIR.FROM_TOP){
                        if(nextLeft != null){
                            curContourPoint.setNext(nextLeft);
                            nextLeft.setPre(curContourPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curContourPoint = nextLeft;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextBottom != null){
                            curContourPoint.setNext(nextBottom);
                            nextBottom.setPre(curContourPoint);
                            curDIR = DIR.FROM_TOP;
                            curContourPoint = nextBottom;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextRight != null){
                            curContourPoint.setNext(nextRight);
                            nextRight.setPre(curContourPoint);
                            curDIR = DIR.FROM_LEFT;
                            curContourPoint = nextRight;
                            it2.remove();
                            findNext = true;
                            break;
                        }
                    }else if(curDIR == DIR.FROM_LEFT){
                        if(nextTop != null){
                            curContourPoint.setNext(nextTop);
                            nextTop.setPre(curContourPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curContourPoint = nextTop;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextRight != null){
                            //Log.d(TAG, "drawContourLine: nextRight 进入llllllllllllllll");
                            curContourPoint.setNext(nextRight);
                            nextRight.setPre(curContourPoint);
                            curDIR = DIR.FROM_LEFT;
                            curContourPoint = nextRight;
                            it2.remove();
                            findNext = true;
                            break;
                            //Log.d(TAG, "drawContourLine: TEST ENTER");
                        }else if(nextBottom != null){
                            //Log.d(TAG, "drawContourLine: nextBottom 进入llllllllllllllll");
                            curContourPoint.setNext(nextBottom);
                            nextBottom.setPre(curContourPoint);
                            curDIR = DIR.FROM_TOP;
                            curContourPoint = nextBottom;
                            it2.remove();
                            findNext = true;
                            break;
                        }
                    }else if(curDIR == DIR.FROM_BOTTOM){
                        if(nextRight != null){
                            curContourPoint.setNext(nextRight);
                            nextRight.setPre(curContourPoint);
                            curDIR = DIR.FROM_LEFT;
                            curContourPoint = nextRight;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextTop != null){
                            curContourPoint.setNext(nextTop);
                            nextTop.setPre(curContourPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curContourPoint = nextTop;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextLeft != null){
                            curContourPoint.setNext(nextLeft);
                            nextLeft.setPre(curContourPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curContourPoint = nextLeft;
                            it2.remove();
                            findNext = true;
                            break;
//                            Log.d(TAG, "drawContourLine: x:" + curContourPoint.getX() + " y:" + nextLeft.getY());
                        }
                    }else {
                        if(nextBottom != null){
                            curContourPoint.setNext(nextBottom);
                            nextBottom.setPre(curContourPoint);
                            curDIR = DIR.FROM_TOP;
                            curContourPoint = nextBottom;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextLeft != null){
                            curContourPoint.setNext(nextLeft);
                            nextLeft.setPre(curContourPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curContourPoint = nextLeft;
                            it2.remove();
                            findNext = true;
                            break;
                        }else if(nextTop != null){
                            curContourPoint.setNext(nextTop);
                            nextTop.setPre(curContourPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curContourPoint = nextTop;
                            it2.remove();
                            findNext = true;
                            break;
                        }
                    }
                }
                nextBottom = null;
                nextTop = null;
                nextLeft = null;
                nextRight = null;
                if(!findNext){
                    break;
                }
                if (curContourPoint.getX() == 0 || curContourPoint.getY() == 0 ||
                        curContourPoint.getX() == (x_length - 1) * stride ||
                        curContourPoint.getY() == (y_length - 1) * stride) {
                    //Log.d(TAG, "drawContourLine: 等值线连接结束");
                    borderPoints.remove(curContourPoint);
                    break;
                }
            }
            ContourPoint temp = contourPointStart;
            //Log.d(TAG, "drawContourLine: DEBUG");
            while (temp != null){
                //Log.d(TAG, "等值线: x:" + temp.getX() + " y:" + temp.getY() + " val:" + temp.getValue() +
                        //" xIndex:" +temp.getX()/stride+ " yIndex:" + temp.getY()/stride);
                temp = temp.getNext();
            }
        }


        //闭曲线

        while(contourPoints.size() > 0){
            Iterator iterator = contourPoints.iterator();
            ContourPoint conStart = null;
            ContourPoint curPoint = null;
            ContourPoint nextPoint = null;
            DIR curDIR = null;
            ContourPoint nextLeft = null,nextRight = null,nextTop = null,nextBottom = null;
            boolean endFlag = true;
            //随便取一个，作为起始节点
            if(iterator.hasNext()){
                conStart = (ContourPoint) iterator.next();
                curPoint = conStart;
                //Log.d(TAG, "drawContourLine: 测试conStart x:" + conStart.getX() + " y:" + conStart.getY());
                if(conStart.getY() % stride == 0){
                    curDIR = DIR.FROM_TOP;
                }else{
                    curDIR = DIR.FROM_LEFT;
                }
                iterator.remove();
            }
            /**
             * 2021-04-05 09:57:29.262 23506-23506/com.example.graduatioproject D/ContentValues: drawContourLine: 测试conStart x:315 y:545 ---1
             * 2021-04-05 09:57:29.262 23506-23506/com.example.graduatioproject D/ContentValues: drawContourLine: 测试conStart x:314 y:546 ---
             * 2021-04-05 09:57:29.262 23506-23506/com.example.graduatioproject D/ContentValues: drawContourLine: 测试conStart x:315 y:552 ---3
             * 2021-04-05 09:57:29.262 23506-23506/com.example.graduatioproject D/ContentValues: drawContourLine: 测试conStart x:316 y:546 ---2
             */
            while (true){
                if(nextPoint != null){
                    //Log.d(TAG, "drawContourLine: conStart x:" + conStart.getX() + " y:" + conStart.getY());
                    //Log.d(TAG, "drawContourLine: nextPoint x:" + nextPoint.getX() + " y:" + nextPoint.getY());
                }
                Iterator iteratorInner = contourPoints.iterator();
                while(iteratorInner.hasNext()){
                    ContourPoint nextContourPoint = (ContourPoint) iteratorInner.next();
                    if(nextContourPoint == conStart){
                        if(contourPoints.size() > 1){
                            if(iteratorInner.hasNext()){
                                nextContourPoint = (ContourPoint) iteratorInner.next();
                            }
                        }
                    }
                    //Log.d(TAG, "drawContourLine: 遍历 x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY() + " size:" + contourPoints.size());
                    int xIndex = curPoint.getX()/stride;
                    int yIndex = curPoint.getY()/stride;
                    if (curDIR == DIR.FROM_TOP) {
                        //从左边出
                        if((nextContourPoint.getX() == xIndex*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从右边出
                        else if((nextContourPoint.getX() == (xIndex+1)*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextRight x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextRight = nextContourPoint;
                        }//从下边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex + 1)*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextBottom x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextBottom = nextContourPoint;
                        }
                    }else if (curDIR == DIR.FROM_LEFT) {
                        //从右边出
                        if((nextContourPoint.getX() == (xIndex+1)*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextRight x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextRight = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;

                        }//从下边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex + 1)*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextBottom x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextBottom = nextContourPoint;
                        }
                    }
                    else if(curDIR == DIR.FROM_RIGHT){
                        //从左边出
                        if((nextContourPoint.getX() == (xIndex - 1)*stride)&&
                                (nextContourPoint.getY() < (yIndex + 1)*stride)&&
                                (nextContourPoint.getY() > yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() > (xIndex-1)*stride)&&
                                (nextContourPoint.getX() < xIndex*stride)&&
                                (nextContourPoint.getY() == yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;

                        }//从下边出
                        else if((nextContourPoint.getX() > (xIndex-1)*stride)&&
                                (nextContourPoint.getX() < xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex + 1)*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextBottom x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextBottom = nextContourPoint;
                        }
                    }else {
                        //从左边出
                        if((nextContourPoint.getX() == xIndex*stride)&&
                                (nextContourPoint.getY() > (yIndex - 1)*stride)&&
                                (nextContourPoint.getY() < yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextLeft x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextLeft = nextContourPoint;
                        }//从右边出
                        else if((nextContourPoint.getX() == (xIndex+1)*stride)&&
                                (nextContourPoint.getY() > (yIndex - 1)*stride)&&
                                (nextContourPoint.getY() < yIndex*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextRight x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextRight = nextContourPoint;
                        }//从上边出
                        else if((nextContourPoint.getX() < (xIndex+1)*stride)&&
                                (nextContourPoint.getX() > xIndex*stride)&&
                                (nextContourPoint.getY() == (yIndex-1)*stride)){
                            //Log.d(TAG, "drawContourLine: close enter nextTop x:" + nextContourPoint.getX() + " y:" + nextContourPoint.getY());
                            nextTop = nextContourPoint;
                        }
                    }

                    if(nextBottom != null || nextLeft != null || nextRight != null || nextTop != null){
                        endFlag = false;
                    }

                    if(curDIR == DIR.FROM_TOP){
                        if(nextLeft != null){
                            curPoint.setNext(nextLeft);
                            nextLeft.setPre(curPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curPoint = nextLeft;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextBottom != null){
                            curPoint.setNext(nextBottom);
                            nextBottom.setPre(curPoint);
                            curDIR = DIR.FROM_TOP;
                            curPoint = nextBottom;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextRight != null){
                            curPoint.setNext(nextRight);
                            nextRight.setPre(curPoint);
                            curDIR = DIR.FROM_LEFT;
                            curPoint = nextRight;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }
                    }else if(curDIR == DIR.FROM_LEFT){
                        if(nextTop != null){
                            curPoint.setNext(nextTop);
                            nextTop.setPre(curPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curPoint = nextTop;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextRight != null){
                            curPoint.setNext(nextRight);
                            nextRight.setPre(curPoint);
                            curDIR = DIR.FROM_LEFT;
                            curPoint = nextRight;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextBottom != null){
                            curPoint.setNext(nextBottom);
                            nextBottom.setPre(curPoint);
                            curDIR = DIR.FROM_TOP;
                            curPoint = nextBottom;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }
                    }else if(curDIR == DIR.FROM_BOTTOM){
                        if(nextRight != null){
                            curPoint.setNext(nextRight);
                            nextRight.setPre(curPoint);
                            curDIR = DIR.FROM_LEFT;
                            curPoint = nextRight;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextTop != null){
                            curPoint.setNext(nextTop);
                            nextTop.setPre(curPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curPoint = nextTop;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextLeft != null){
                            curPoint.setNext(nextLeft);
                            nextLeft.setPre(curPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curPoint = nextLeft;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }
                    }else {
                        if(nextBottom != null){
                            curPoint.setNext(nextBottom);
                            nextBottom.setPre(curPoint);
                            curDIR = DIR.FROM_TOP;
                            curPoint = nextBottom;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextLeft != null){
                            curPoint.setNext(nextLeft);
                            nextLeft.setPre(curPoint);
                            curDIR = DIR.FROM_RIGHT;
                            curPoint = nextLeft;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }else if(nextTop != null){
                            curPoint.setNext(nextTop);
                            nextTop.setPre(curPoint);
                            curDIR = DIR.FROM_BOTTOM;
                            curPoint = nextTop;
                            nextPoint = curPoint;
                            iteratorInner.remove();
                            break;
                        }
                    }
                }
                if(endFlag){
                    if (curPoint != null) {
                        curPoint.setNext(conStart);
                        if (conStart != null) {
                            conStart.setPre(curPoint);
                        }
                    }
                    retContourLines.add(conStart);
                    //Log.d(TAG, "drawContourLine: 闭曲线找到一条");
                    break;
                }else {
                    endFlag = true;
                }
            }
        }
        return retContourLines;
    }

    public static ContourPoint[][] interPolationRawData(ArrayList<MagneticRawData> magneticRawDatas,int pixelLength,int quality){
        //找出横纵坐标极值
        int xMin = Integer.MAX_VALUE,xMax = Integer.MIN_VALUE,yMin = Integer.MAX_VALUE,yMax = Integer.MIN_VALUE;
        for(MagneticRawData magneticRawData : magneticRawDatas){
            if(magneticRawData.getX() < xMin){
                xMin = magneticRawData.getX();
            }else if(magneticRawData.getX() > xMax){
                xMax = magneticRawData.getX();
            }
            if(magneticRawData.getY() < yMin){
                yMin = magneticRawData.getY();
            }else if(magneticRawData.getY() > yMax){
                yMax = magneticRawData.getY();
            }
        }

        //获取测量矩形的范围，单位为100cm，x，y坐标单位是cm
        int xRange = xMax - xMin,yRange = yMax - yMin;
        int xOffset = 100 - (xRange % 100);
        int yOffset = 100 - (yRange % 100);
        xRange = xRange + xOffset;
        yRange = yRange + yOffset;
        xMin = xMin - xOffset / 2;
        yMin = yMin - yOffset / 2;
        //Log.d(TAG, "interPolationRawData: xRange:" + xRange + "  yRange:" + yRange);

        //修正坐标，让所有的x,y坐标在xRange，yRange范围内
        //每隔25cm进行一次插值。
        if(quality < 1){
            quality = 1;
        }else if(quality > 10){
            quality = 10;
        }
        int div = 100/quality;
        ContourPoint[][] contourPoints = new ContourPoint[yRange/div + 1][xRange/div + 1];

        //MagneticRawData[][] result = new MagneticRawData[yRange/25 + 1][xRange/25 + 1];
        for(int i = 0;i < yRange/div + 1;i++){
            for(int j = 0;j < xRange/div + 1;j++){
                int x = xMin + j*div;
                int y = yMin + i*div;
                int stride = pixelLength / Math.max(yRange/div + 1,xRange/div + 1);
                int pixelX = j*stride;
                int pixelY = i*stride;
                MagneticRawData[] surroundPoints = findMinPoints(magneticRawDatas,x,y,5);
                if (surroundPoints != null) {
                    double[] distReverse = new double[surroundPoints.length];
                    double[] weight = new double[surroundPoints.length];
                    double sumDistReverse = 0.0;
                    double targetGMICH1 = 0.0,targetGMICH2 = 0.0,targetGMICH3 = 0.0,targetGMITotal = 0.0;
                    for(int k = 0;k < surroundPoints.length;k++){
                        distReverse[k] = Math.sqrt(Math.pow(x - surroundPoints[k].getX(),2) + Math.pow(y - surroundPoints[k].getY(),2));
                        distReverse[k] = Math.pow(distReverse[k],-2);
                        sumDistReverse += distReverse[k];
                    }
                    for(int k = 0;k < surroundPoints.length;k++){
                        weight[k] = distReverse[k]/sumDistReverse;
                        targetGMICH1 += surroundPoints[k].getGmiCH1()*weight[k];
                        targetGMICH2 += surroundPoints[k].getGmiCH2()*weight[k];
                        targetGMICH3 += surroundPoints[k].getGmiCH3()*weight[k];
                        targetGMITotal += surroundPoints[k].getGmiTotal()*weight[k];
                    }
                    ContourPoint cpTemp = null;
                    switch (Constant.MAGNETIC_DIR){
                        case 0:
                            cpTemp = new ContourPoint((int)targetGMICH1,pixelX,pixelY);
                            break;
                        case 1:
                            cpTemp = new ContourPoint((int)targetGMICH2,pixelX,pixelY);
                            break;
                        case 2:
                            cpTemp = new ContourPoint((int)targetGMICH3,pixelX,pixelY);
                            break;
                        case 3:
                            cpTemp = new ContourPoint((int)targetGMITotal,pixelX,pixelY);
                            break;
                    }

                    /**
                     * 修改：contourPoints[i][j] 改为contourPoints[yRange/25 - i][j]
                     * 目的：转换坐标系，android坐标系原点在左上角，人们习惯坐标系在左下角
                     */
                    contourPoints[yRange/div - i][j] = cpTemp;
                }
            }
        }

        return contourPoints;

    }

    /**
     * 返回最短距离的n个数据点
     * @param magneticRawDatas
     * @param x
     * @param y
     * @return
     */
    private static MagneticRawData[] findMinPoints(ArrayList<MagneticRawData> magneticRawDatas,int x,int y,int n){
        if(n < 3)return null;
        MagneticRawData[] target = new MagneticRawData[n];
        int index = 0;
        //Log.d(TAG, "findMinPoints: xmin:" + x + "  ymin:" + y);
        for(MagneticRawData rawData : magneticRawDatas){
            //调试
            //double realDist = Math.pow(x - rawData.getX(),2) + Math.pow(y - rawData.getY(),2);
            //Log.d(TAG, "数据点信息: distance:" + realDist + " x:" +rawData.getX() + " y:" + rawData.getY());
            if(index < 4){
                target[index] = rawData;
            }else if(index == 4){
                target[index] = rawData;
                //对target进行排序
                for(int i = 0;i < target.length - 1;i++){
                    double minDist = Math.pow(x - target[i].getX(),2) + Math.pow(y - target[i].getY(),2);
                    int minIndex = i;
                    for(int j = i + 1;j < target.length;j++){
                        double currentDist = Math.pow(x - target[j].getX(),2) + Math.pow(y - target[j].getY(),2);
                        if(currentDist < minDist){
                            minDist = currentDist;
                            minIndex = j;
                        }
                    }
                    MagneticRawData temp = target[i];
                    target[i] = target[minIndex];
                    target[minIndex] = temp;
                }

                for(int i = 0;i < target.length;i++){
                    //Log.d(TAG, "前五个点排序结果: x" + i + ":" + target[i].getX() + "  y" + i + ":" + target[i].getY());
                }

            }else{
                double curDist = Math.pow(x - rawData.getX(),2) + Math.pow(y - rawData.getY(),2);
                int insertIndex = Integer.MAX_VALUE;

                //获取要插入的数组的索引
                double orderedPre = Math.pow(x - target[0].getX(),2) + Math.pow(y - target[0].getY(),2);
                double orderedNext;
                for(int i = 1;i < target.length;i++){
                    orderedNext = Math.pow(x - target[i].getX(),2) + Math.pow(y - target[i].getY(),2);
                    if(curDist < orderedPre){
                        insertIndex = 0;
                        break;
                    }else if(curDist < orderedNext){
                        insertIndex = i;
                        break;
                    }else{
                        orderedPre = orderedNext;
                    }
                }

                if(insertIndex < target.length){
                    target[target.length - 1] = rawData;
                    int curIndex = target.length - 1;
                    while(curIndex > insertIndex){
                        MagneticRawData magTemp = target[curIndex];
                        target[curIndex] = target[curIndex - 1];
                        target[curIndex - 1] = magTemp;
                        curIndex--;
                    }
                }
            }
            index++;
        }
        return target;
    }

    public static int[] getPixelValues(ContourPoint[][] contourPoints,int stride,int quality){
        int xPixel = (contourPoints[0].length - 1)*stride;
        int yPixel = (contourPoints.length - 1)*stride;
        int xLength = contourPoints[0].length;
        int yLength = contourPoints.length;
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        int[][] ret = new int[yPixel][xPixel];
        int[] target = new int[xPixel*yPixel];

//        for(int i = 0;i < yLength;i++){
//            for(int j = 0;j < xLength;j++){
//                Log.d(TAG, "getPixelValues: " + contourPoints[i][j].getValue());
//            }
//        }

        //获取方格边界的值,但不包含下边界和右边界
        for(int i = 0;i < yLength - 1;i++){
            for(int j = 0;j < xLength - 1;j++){
                int x0y0 = contourPoints[i][j].getValue();
                int x1y0 = contourPoints[i][j + 1].getValue();
                int x0y1 = contourPoints[i + 1][j].getValue();
                double xSlope = (double) (x1y0 - x0y0)/stride;
                double ySlope = (double) (x0y1 - x0y0)/stride;
                for(int k = 0;k < stride;k++){
                    ret[i*stride][j*stride + k] = (int)xSlope*k + x0y0;
                    ret[i*stride + k][j*stride] = (int)ySlope*k + x0y0;
                }
            }
        }
        //处理右边界
        for(int i = 0;i < yLength - 1;i++){
            int x0y0 = contourPoints[i][xLength - 1].getValue();
            int x0y1 = contourPoints[i + 1][xLength - 1].getValue();
            double ySlope = (double) (x0y1 - x0y0)/stride;
            for(int j = 0;j < stride;j++){
                ret[i*stride + j][xPixel - 1] = (int)ySlope*j + x0y0;
            }
        }
        //处理下边界
        for(int i = 0;i < xLength - 1;i++){
            int x0y0 = contourPoints[yLength - 1][i].getValue();
            int x1y0 = contourPoints[yLength - 1][i + 1].getValue();
            double xSlope = (double) (x1y0 - x0y0)/stride;
            for(int j = 0;j < stride;j++){
                ret[yPixel - 1][i*stride + j] = (int)xSlope*j + x0y0;
            }
        }
        //处理每个小方格内部
        for(int i = 0;i < yLength - 1;i++){
            for(int j = 0;j < xLength - 1;j++){
                for(int k = 1;k < stride;k++){
                    int x0y0 = ret[i*stride + k][j*stride];
                    int x1y0;
                    if(j == xLength - 2){
                        x1y0 = ret[i*stride + k][(j + 1)*stride - 1];
                    }else{
                        x1y0 = ret[i*stride + k][(j + 1)*stride];
                    }

                    double xSlope = (double) (x1y0 - x0y0)/stride;
                    for(int l = 1;l < stride;l++){
                        ret[i*stride + k][j*stride + l] = (int)xSlope*l + x0y0;
                    }
                }
            }
        }

        for(int i = 0;i < yPixel;i++){
            for(int j = 0;j < xPixel;j++){
                //Log.d(TAG, "getPixelValues: xPixel:" + i + " yPixel:" + j + " value:" + ret[i][j]);
                if(ret[i][j] < minValue){
                    minValue = ret[i][j];
                }else if(ret[i][j] > maxValue){
                    maxValue = ret[i][j];
                }
            }
        }

        Log.d(TAG, "getPixelValues: maxValue:" + maxValue + " minValue:" + minValue);

        int valueRange = maxValue - minValue;
        double colorStride = (double) valueRange / (4*256);//1024 = 4*256 4表示颜色分为4段，256表示2的8次方
        for(int i = 0;i < yPixel;i++){
            for(int j = 0;j < xPixel;j++){
                int temp = ret[i][j];
                temp -= minValue;
                if(temp < valueRange / 4){
                    //Log.d(TAG, "getPixelValues: green color:" + temp/colorStride);
                    //ret[i][j] = 0xff << 24 + (int)(temp/colorStride) << 8 + 0xff;
                    ret[i][j] = 0xff << 24;
                    ret[i][j] += (int)(temp/colorStride) << 8;
                    ret[i][j] += 0xff;
                    //Log.d(TAG, "getPixelValues: R:" +(ret[i][j] & 0x00ff0000) + " G:" +(ret[i][j] & 0x0000ff00) + " B:" +(ret[i][j] & 0x000000ff) + " val:" + ret[i][j]);
                }else if(temp < valueRange / 2){
                    temp -= valueRange / 4;
                    ret[i][j] = 0xff << 24;
                    ret[i][j] += 0xff << 8;
                    ret[i][j] += 0xff - (int)(temp/colorStride);
                    //Log.d(TAG, "getPixelValues: R:" +(ret[i][j] & 0x00ff0000) + " G:" +(ret[i][j] & 0x0000ff00) + " B:" +(ret[i][j] & 0x000000ff) + " val:" + ret[i][j]);
                }else if(temp < valueRange * 3 / 4){
                    temp -= valueRange / 2;
                    ret[i][j] = 0xff << 24;
                    ret[i][j] += (int)(temp/colorStride) << 16;
                    ret[i][j] += 0xff << 8;
                    //Log.d(TAG, "getPixelValues: R:" +(ret[i][j] & 0x00ff0000) + " G:" +(ret[i][j] & 0x0000ff00) + " B:" +(ret[i][j] & 0x000000ff) + " val:" + ret[i][j]);
                }else {
                    temp -= (valueRange * 3 / 4);
                    ret[i][j] = 0xff << 24;
                    ret[i][j] += 0xff << 16;
                    ret[i][j] += ((0xff - (int)(temp/colorStride)) << 8);
                    //Log.d(TAG, "getPixelValues: R:" +(ret[i][j] & 0x00ff0000) + " G:" +(ret[i][j] & 0x0000ff00) + " B:" +(ret[i][j] & 0x000000ff) + " val:" + ret[i][j]);
                }
            }
        }

        for(int i = 0;i < yPixel;i++) {
            for (int j = 0; j < xPixel; j++) {
                target[i*xPixel + j] = ret[i][j];
//                Log.d(TAG, "getPixelValues: R:" +(ret[i][i] & 0x00ff0000) + " G:" +(ret[i][i] & 0x0000ff00) + " B:" +(ret[i][i] & 0x000000ff) + " val:" + ret[i][j]);
            }
        }

        return target;
    }

}
