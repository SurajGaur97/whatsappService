package com.example.whatsappservice;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private String ISON = "ISON";

    public PrefManager(Context context){
        pref = context.getSharedPreferences("MySP", context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setIsON(boolean isOn){
        editor.putBoolean(ISON, isOn);
        editor.commit();
    }

    public Boolean getIsON(){
        return  pref.getBoolean(ISON, false);
    }
}
