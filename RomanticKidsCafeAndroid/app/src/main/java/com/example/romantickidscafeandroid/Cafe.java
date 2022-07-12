package com.example.romantickidscafeandroid;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Cafe {
    public String address;
    public String url;
    public String topic_name;

    public Cafe(){}

    public Cafe(String address, String url, String topic_name){
        this.address = address;
        this.url = url;
        this.topic_name = topic_name;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address",address);
        result.put("url",url);
        result.put("topic_name",topic_name);

        return result;
    }
}
