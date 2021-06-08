package com.example.fireoperationmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OperationMap {
    public String name;
    public java.util.Map<Integer, java.util.Map<Integer, Place>> sectionData = new HashMap<>();
    public List<User> userList = new ArrayList<>();
    public List<Arcade> arcadeList = new ArrayList<>();
    public List<Approach> approachList = new ArrayList<>();
    public List<Auto_Arcade> auto_arcadeList = new ArrayList<>();
    public List<Fireplug> fireplugList = new ArrayList<>();
}
