package com.example.fireoperationmap;

public class Arcade implements Comparable<Arcade>{
    private String detail;
    private String id;
    private String num;
    private float x;
    private float y;

    public String getDetail() {
        return detail;
    }

    public String getId() {
        return id;
    }

    public int getNum() {
        return Integer.parseInt(this.num);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Arcade(){}

    @Override
    public int compareTo(Arcade arcade) {
        int tmp = Integer.parseInt(this.num);
        if (tmp == arcade.getNum())
            return 0;
        else if (tmp > arcade.getNum())
            return 1;
        else
            return -1;
    }
}
