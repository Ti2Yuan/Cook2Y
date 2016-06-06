package org.crazyit.cook2y;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.crazyit.cook2y.About.AboutActivity;
import org.crazyit.cook2y.Adapter.ViewPageAdapter;
import org.crazyit.cook2y.Collections.CollectionsActivity;
import org.crazyit.cook2y.Constants.Constants;
import org.crazyit.cook2y.Search.SearchActivity;
import org.crazyit.cook2y.Setting.SettingActivity;
import org.crazyit.cook2y.Setting.Settings;
import org.crazyit.cook2y.api.FoodClassify;
import org.litepal.tablemanager.Connector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Drawer drawer;

    private AccountHeader header;

    private Menu menu;

    private Toolbar mToolbar;

    private SmartTabLayout smartTabLayout;

    private ViewPageAdapter viewPagerAdapter;

    private FragmentManager fragmentManager;

    protected ViewPager viewPager;

//    private Map<String,Fragment> fragmentList;
    private Map<String,Integer> classIDList;

    private long lastPressTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initData();

        initViewPager();

        SQLiteDatabase db = Connector.getDatabase();
        db.setLocale(Locale.CHINA);


    }

    private void initViewPager() {
        classIDList = new HashMap<>();

        for(int i = 0; i < FoodClassify.food_classify.length; i++){
            classIDList.put(FoodClassify.food_classify[i],FoodClassify.food_classify_id[i]);
        }

        fragmentManager = getSupportFragmentManager();
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        smartTabLayout = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerAdapter = new ViewPageAdapter(fragmentManager,classIDList ,FoodClassify.food_classify);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(FoodClassify.food_classify.length);
        smartTabLayout.setViewPager(viewPager);
    }

    public void initData(){

        mToolbar = (Toolbar)findViewById(R.id.toolBar);
        mToolbar.setTitleTextColor(Color.WHITE);//设置ToolBar的titl颜色
        setSupportActionBar(mToolbar);

        //账户信息
        header = new AccountHeaderBuilder().withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(R.mipmap.header)
                .addProfiles(new ProfileDrawerItem().withIcon(R.drawable.logo)
                .withEmail(getString(R.string.email_name))
                .withName(getString(R.string.name)))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        Intent i = new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(i);
                        return false;
                    }
                })
                .build();

        //侧滑菜单
        drawer = new DrawerBuilder().withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(header)
                .withSliderBackgroundColor(ContextCompat.getColor(this,R.color.white))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.hot).withIcon(R.mipmap.ic_home).withIdentifier(R.string.hot),
                        new PrimaryDrawerItem().withName(R.string.custom).withIcon(R.mipmap.ic_science).withIdentifier(R.string.custom),
                        new PrimaryDrawerItem().withName(R.string.healthy).withIcon(R.mipmap.ic_news).withIdentifier(R.string.healthy),
                        new PrimaryDrawerItem().withName(R.string.collections).withIcon(R.mipmap.ic_collect_grey).withIdentifier(R.string.collections),
                        new SectionDrawerItem().withName(R.string.app_name),
                        new SecondaryDrawerItem().withName(R.string.night_model).withIcon(R.mipmap.ic_night).withIdentifier(R.mipmap.ic_night)
                        .withTextColor(ContextCompat.getColor(this,R.color.text_light)),
                        new SecondaryDrawerItem().withName(R.string.setting).withIcon(R.mipmap.ic_setting).withIdentifier(R.mipmap.ic_setting)
                                .withTextColor(ContextCompat.getColor(this,R.color.text_light)),
                        new SecondaryDrawerItem().withName(R.string.about).withIcon(R.mipmap.ic_about).withIdentifier(R.mipmap.ic_about)
                                .withTextColor(ContextCompat.getColor(this,R.color.text_light))
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()){
                            case R.string.collections:
                                Intent intent = new Intent(MainActivity.this, CollectionsActivity.class);
                                startActivity(intent);
                                return false;
                            case R.mipmap.ic_setting:
                                Intent setting = new Intent(MainActivity.this,SettingActivity.class);
                                setting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(setting);
                                return false;
                            case R.mipmap.ic_about:
                                Intent about = new Intent(MainActivity.this,AboutActivity.class);
                                startActivity(about);
                                return false;
                        }
                        return false;
                    }
                })
                .build();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                SearchDialog();
                break;
            case R.id.share_app:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.share_app));
                intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_content));
                startActivity(Intent.createChooser(intent,getString(R.string.share_app)));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SearchDialog() {
        final View rootView = getLayoutInflater().inflate(R.layout.search_view,null);

        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_search)
                .setTitle(R.string.search_food)
                .setView(rootView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EditText editText = (EditText) rootView.findViewById(R.id.food_name);
                        editText.clearFocus();
                        String foodName = editText.getText().toString();
                        Log.d("tag","search name:"+foodName + "length " + foodName.length());
                        if(foodName != null && foodName.length() > 0){
                            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                            intent.putExtra(getString(R.string.search_food_name),foodName);
                            startActivity(intent);
                        }else if(0 == foodName.length()){
                            Toast.makeText(MainActivity.this,getString(R.string.search_text_null),Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen()){
            drawer.closeDrawer();
        }else if(canExit()){
            super.onBackPressed();
        }
    }

    private boolean canExit() {
        if(Settings.isExitConfirm){
            if(System.currentTimeMillis() - lastPressTime > Constants.exitConfirmTime){
                lastPressTime = System.currentTimeMillis();
                Snackbar.make(getCurrentFocus(),R.string.notify_exit_confirm,Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Settings.needRecreate){
            Settings.needRecreate = false;
            this.recreate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
    }
}