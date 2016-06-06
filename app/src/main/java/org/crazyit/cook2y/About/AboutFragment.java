package org.crazyit.cook2y.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.crazyit.cook2y.R;

/**
 * Created by chenti on 2016/4/13.
 */
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private Preference mShare;

    private Preference mBlog;

    private final String SHARE = "share";
    private final String BLOG = "blog";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);

        mShare = findPreference(SHARE);
        mBlog = findPreference(BLOG);

        mShare.setOnPreferenceClickListener(this);
        mBlog.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(mShare == preference){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_app));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_content));
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
        }
        if(mBlog == preference){
            Uri uri = Uri.parse(mBlog.getSummary()+"");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
        return false;
    }
}
