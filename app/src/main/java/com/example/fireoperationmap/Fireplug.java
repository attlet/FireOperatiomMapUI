package com.example.fireoperationmap;

public class Fireplug implements Comparable<Fireplug>{
    private float x;
    private float y;
    private int num;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getNum() { return num; }

    public Fireplug(){}

    @Override
    public int compareTo(Fireplug fireplug) {
        int tmp = num;
        if (tmp == fireplug.getNum())
            return 0;
        else if (tmp > fireplug.getNum())
            return 1;
        else
            return -1;
    }
}
