package com.example.fireoperationmap;

public class Approach implements Comparable<Approach>{
    private String name;
    private String address;
    private int num;
    private float x;
    private float y;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getNum() { return num; }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Approach(){}

    @Override
    public int compareTo(Approach approach) {
        int tmp = num;
        if (tmp == approach.getNum())
            return 0;
        else if (tmp > approach.getNum())
            return 1;
        else
            return -1;
    }
}
