package org.crazyit.cook2y.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


import org.crazyit.cook2y.Cook2YApplication;
import org.crazyit.cook2y.Fragment.FoodListFragment;
import org.crazyit.cook2y.R;

import java.util.Map;


/**
 * Created by chenti on 2016/4/7.
 */
public class ViewPageAdapter extends FragmentStatePagerAdapter {


    private String[] Titles;

//    private Map<String,Fragment> fragmentList;

    private Map<String,Integer> classIDList;

//    public ViewPageAdapter(FragmentManager fm,String[] Titles,Map<String,Fragment> fragmentList) {
//        super(fm);
//        this.Titles = Titles;
//        this.fragmentList = fragmentList;
//    }

    public ViewPageAdapter(FragmentManager fm,Map<String,Integer> classIDList,String[] Titles) {
        super(fm);
        this.classIDList = classIDList;
        this.Titles = Titles;
    }
    @Override
    public Fragment getItem(int position) {
        FoodListFragment foodListFragment = FoodListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Cook2YApplication.AppContext.getString(R.string.ClassID),
                classIDList.get(Titles[position]));
        foodListFragment.setArguments(bundle);
        return foodListFragment;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        super.instantiateItem(container, position);
//        return getItem(position);
//    }

    //

    @Override
    public int getCount() {
        //返回Fragment的数量
        return Titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //得到对应position的Fragment的title
        return Titles[position];
    }
}
