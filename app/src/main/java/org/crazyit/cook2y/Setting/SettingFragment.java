package org.crazyit.cook2y.Setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.R;

/**
 * Created by chenti on 2016/4/15.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener{

    private Preference mLanguage;
    private CheckBoxPreference mExitConfirm;
    private CheckBoxPreference mNightMode;
    private CheckBoxPreference mNoPicture;
    private Preference mClearCache;

    private Settings mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        mSettings = Settings.newInstance();

        mLanguage = findPreference(Settings.LANGUAGE);
        mExitConfirm = (CheckBoxPreference) findPreference(Settings.EXIT_CONFIRM);
        mNightMode = (CheckBoxPreference) findPreference(Settings.NIGHT_MODE);
        mNoPicture = (CheckBoxPreference) findPreference(Settings.NO_PICTURE);
        mClearCache = findPreference(Settings.CLEAR_CACHE);

        mExitConfirm.setChecked(Settings.isExitConfirm);
        mNightMode.setChecked(Settings.isNightMode);
        mNoPicture.setChecked(Settings.isNoPicture);

        mExitConfirm.setOnPreferenceChangeListener(this);
        mNightMode.setOnPreferenceChangeListener(this);
        mNoPicture.setOnPreferenceChangeListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        mLanguage.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == mExitConfirm){
            Settings.isExitConfirm = Boolean.parseBoolean(newValue.toString());
            mSettings.putBoolean(Settings.EXIT_CONFIRM,Settings.isExitConfirm);
            return true;
        }else if(preference == mNightMode){
            Settings.isNightMode = Boolean.parseBoolean(newValue.toString());
            mSettings.putBoolean(Settings.NIGHT_MODE,Settings.isNightMode);
            Settings.needRecreate = true;
            Toast.makeText(getActivity(),"已切换情景模式！",Toast.LENGTH_SHORT).show();
            return true;

            /**
             * 重设应用主题
             * getActivity().recreate();
             */

        }else if(preference == mNoPicture){
            Settings.isNoPicture = Boolean.parseBoolean(newValue.toString());
            mSettings.putBoolean(Settings.NO_PICTURE,Settings.isNoPicture);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == mClearCache){
            Snackbar.make(getView(),"确认清除所有缓存与收藏数据?",Snackbar.LENGTH_LONG)
                    .setAction("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FoodListCache.clear_cache();
                            Settings.needRecreate = true;
                            Snackbar.make(getView(),"清除成功!",Snackbar.LENGTH_SHORT).show();
                        }
                    }).show();
        }else if(preference == mLanguage){
            Snackbar.make(getView(),"当前语言为简体中文！",Snackbar.LENGTH_SHORT).show();
        }
        return false;
    }
}
