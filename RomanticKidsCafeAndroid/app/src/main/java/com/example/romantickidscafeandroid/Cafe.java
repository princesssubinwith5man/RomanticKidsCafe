package com.example.romantickidscafeandroid;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Cafe {
    public String address;
    public String url;

    public Cafe(){}

    public Cafe(String address, String url){
        this.address = address;
        this.url = url;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address",address);
        result.put("url",url);

        return result;
    }
}
