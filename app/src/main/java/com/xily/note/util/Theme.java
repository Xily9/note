package com.xily.note.util;

import android.app.Activity;

import com.xily.note.R;
import com.xily.note.SettingsData;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xily on 2017/10/24.
 */

public class Theme {
    private static final int[] themeList={
            R.id.theme_red,
            R.id.theme_orange,
            R.id.theme_pink,
            R.id.theme_green,
            R.id.theme_blue,
            R.id.theme_purple,
            R.id.theme_teal,
            R.id.theme_brown,
            R.id.theme_dark_blue,
            R.id.theme_dark_purple
    };
    public static void setTheme(Activity act){
        SettingsData settingsData=new SettingsData(act);
        int theme = settingsData.getValue("theme",4);
        int[] styleList={
                R.style.AppThemeRed,
                R.style.AppThemeOrange,
                R.style.AppThemePink,
                R.style.AppThemeGreen,
                R.style.AppThemeBlue,
                R.style.AppThemePurple,
                R.style.AppThemeTeal,
                R.style.AppThemeBrown,
                R.style.AppThemeDarkBlue,
                R.style.AppThemeDarkPurple
        };
        act.setTheme(styleList[theme]);
    }
    public static int[] getThemeList(){
        return themeList;
    }
}
