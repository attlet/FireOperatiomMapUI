package com.example.fireoperationmap;

public class Auto_Arcade implements Comparable<Auto_Arcade>{
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

    public Auto_Arcade(){}

    @Override
    public int compareTo(Auto_Arcade auto_arcade) {
        int tmp = num;
        if (tmp == auto_arcade.getNum())
            return 0;
        else if (tmp > auto_arcade.getNum())
            return 1;
        else
            return -1;
    }
}
