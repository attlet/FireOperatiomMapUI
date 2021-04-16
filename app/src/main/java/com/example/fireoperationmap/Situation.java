package com.example.fireoperationmap;

public class Situation {
    private String Building_total; //건축물 합계
    private String Building_sale; //건축물 판매
    private String Building_food; //건축물 식품
    private String Building_multiple; //건축물 다중
    private String Building_house; //건축물 주택
    private String Building_the_other; //건축물 기타

    private String Arcade_total; //아케이드 구역내 점포 합게
    private String Arcade_sale; //아케이드 구역내 판매
    private String Arcade_food; //아케이드 구역내 식품
    private String Arcade_multiple; //아케이드 구역내 다중
    private String Arcade_building; //아케이드 구역내 주택
    private String Arcade_the_other; //아케이드 구역내 기타

    public String getBuilding_total() { return Building_total; }
    public String getBuilding_sale() { return Building_sale; }
    public String getBuilding_food() { return Building_food; }
    public String getBuilding_multiple() { return Building_multiple; }
    public String getBuilding_house() { return Building_house; }
    public String getBuilding_the_other() { return Building_the_other; }

    public String getArcade_total() { return Arcade_total; }
    public String getArcade_sale() { return Arcade_sale; }
    public String getArcade_food() { return Arcade_food; }
    public String getArcade_multiple() { return Arcade_multiple; }
    public String getArcade_building() { return Arcade_building; }
    public String getArcade_the_other() { return Arcade_the_other; }

    public Situation(){}
}
