package com.xily.note;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Xily on 2017/10/27.
 */

public class SettingsData{
    static public SharedPreferences data=null;
    public SettingsData(Context context){
        if(data==null)data=context.getSharedPreferences("data",Context.MODE_PRIVATE);
    }
    public boolean getValue(String name,boolean defValue){
        return data.getBoolean(name,defValue);
    }
    public int getValue(String name,int defValue){
        return data.getInt(name,defValue);
    }
    public void setValue(String name,boolean value){
        SharedPreferences.Editor editor=data.edit();
        editor.putBoolean(name,value).apply();
    }
    public void setValue(String name,int value){
        SharedPreferences.Editor editor=data.edit();
        editor.putInt(name,value).apply();
    }
}
