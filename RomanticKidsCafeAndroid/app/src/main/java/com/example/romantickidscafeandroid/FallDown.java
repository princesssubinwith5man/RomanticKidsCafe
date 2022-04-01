package com.example.romantickidscafeandroid;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FallDown {
    public String date;
    public boolean fall_down;
    public boolean girlfriend;
    public String name;

    public FallDown(){}

    public FallDown(String date, boolean fall_down, boolean girlfriend, String name){
        this.date = date;
        this.fall_down = fall_down;
        this.girlfriend = girlfriend;
        this.name = name;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date",date);
        result.put("fall_down",fall_down);
        result.put("girlfriend",girlfriend);
        result.put("name",name);

        return result;
    }
}
