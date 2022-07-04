package com.example.romantickidscafeandroid;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AlarmCheck {
    public String check;

    public AlarmCheck(){}

    public AlarmCheck(String check){
        this.check = check;
    }
    @Exclude
    public String toCheck() {
        String result = this.check;
        return result;
    }
}
