package com.example.graduatioproject.contour;

public class ContourPoint {
    private int value;
    private int x;//像素坐标
    private int y;//像素坐标





    public ContourPoint getPre() {
        return pre;
    }

    public ContourPoint getNext() {
        return next;
    }

    private ContourPoint pre;
    private ContourPoint next;

    public ContourPoint(int value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public void setPre(ContourPoint pre) {
        this.pre = pre;
    }

    public void setNext(ContourPoint next) {
        this.next = next;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
