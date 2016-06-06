package org.crazyit.cook2y.Setting;

import android.content.Context;
import android.content.SharedPreferences;

import org.crazyit.cook2y.Cook2YApplication;

/**
 * Created by chenti on 2016/4/15.
 * 全局只有一个，所以是单例模式
 */
public class Settings {

    public static final String XML_NAME = "setting";      //xml名
    public static final String LANGUAGE = "language";    //语言
    public static final String EXIT_CONFIRM = "exit_confirm";   //退出确认
    public static final String NIGHT_MODE = "night_mode";    //夜间模式
    public static final String NO_PICTURE = "no_picture";    //无图模式
    public static final String CLEAR_CACHE = "clear_cache";   //清除缓存

    public static boolean needRecreate = false;
    public static boolean isExitConfirm = true;
    public static boolean isNightMode = false;
    public static boolean isNoPicture = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Context context;

    private static Settings sInstance;

    public Settings(Context context1) {
        context = context1;
        sharedPreferences = context.getSharedPreferences(XML_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static Settings newInstance(){
        if (sInstance == null){
            return new Settings(Cook2YApplication.AppContext);
        }
        return sInstance;
    }

    public void putBoolean(String key,boolean value){
        editor.putBoolean(key,value).commit();
    }

    public boolean getBoolean(String key,boolean defValue){
        return sharedPreferences.getBoolean(key,defValue);
    }

    public void putString(String key,String value){
        editor.putString(key,value).commit();
    }

    public String getString(String key,String defValue){
        return sharedPreferences.getString(key,defValue);
    }
}
