package com.example.fireoperationmap;

public class User {
    private String id;
    private String st_name;
    private String address;
    private String structure;
    private String floor;
    private String st_type;
    private String faclility;
    private String person;
    private int num;


    private String phone1;
    private String phone2;

    public User(){}

    public void setSt_name(String st_name) {this.st_name = st_name;}
    public void setAddress(String address) {this.address = address;}
    public void setStructure(String structure) {this.structure = structure;}
    public void setFloor(String floor) {this.floor = floor;}
    public void setSt_type(String st_type) {this.st_type = st_type;}
    public void setPerson(String person) {this.person = person;}
    public void setPhone1(String phone1) {this.phone1 = phone1;}
    public void setPhone2(String phone2) {this.phone2 = phone2;}
    public void setId(String id) {this.id = id;}
    public void setFaclility(String faclility) {this.faclility = faclility;}
    public void setNum(int num){this.num = num;}

    public String getId() {return this.id;}
    public String getSt_name() {return this.st_name;}
    public String getAddress() {return this.address;}
    public String getStructure() {return this.structure;}
    public String getFloor() {return this.floor;}
    public String getSt_type() {return this.st_type;}
    public String getPerson() {return this.person;}
    public String getPhone1() {return this.phone1;}
    public String getPhone2() {return this.phone2;}
    public String getFaclility() {return faclility;}
    public int getNum(){return num;}

    public String getTagId() {return "건물 번호: " + this.id;}
    public String getTagSt_name() {return "상호: " + this.st_name;}
    public String getTagAddress() {return "주소: " + this.address;}
    public String getTagStructure() {return "건물 구조: " + this.structure;}
    public String getTagFloor() {return "층수: " + this.floor;}
    public String getTagSt_type() {return "업종: " + this.st_type;}
    public String getTagPerson() {return "대표자: " + this.person;}
    public String getTagPhone1() {return "연락처(휴대폰): " + this.phone1;}
    public String getTagPhone2() {return "연락처(일반전화): " + this.phone2;}
    public String getTagFaclility() {return "건물(점포) 소방시설: " + faclility;}
}
