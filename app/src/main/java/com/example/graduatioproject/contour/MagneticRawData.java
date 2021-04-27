package com.example.graduatioproject.contour;

public class MagneticRawData {
    private int x;
    private int y;
    private int gmiCH1;
    private int gmiCH2;
    private int gmiCH3;
    private int gmiTotal;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getGmiCH1() {
        return gmiCH1;
    }

    public int getGmiCH2() {
        return gmiCH2;
    }

    public int getGmiCH3() {
        return gmiCH3;
    }

    public int getGmiTotal() {
        return gmiTotal;
    }

    public MagneticRawData(int x, int y, int gmiCH1, int gmiCH2, int gmiCH3,int gmiTotal) {
        this.x = x;
        this.y = y;
        this.gmiCH1 = gmiCH1;
        this.gmiCH2 = gmiCH2;
        this.gmiCH3 = gmiCH3;
        this.gmiTotal = gmiTotal;
    }
}
