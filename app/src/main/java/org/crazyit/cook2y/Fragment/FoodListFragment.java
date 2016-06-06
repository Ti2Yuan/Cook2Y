package org.crazyit.cook2y.Fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.crazyit.cook2y.Adapter.FoodListAdapter;
import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.api.FoodClassify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenti on 2016/5/15.
 */
public class FoodListFragment extends BaseListFragment {

//    private List<FoodItemBean> mfoodLists = null;

    private FoodListCache foodListCache;

    private FoodListAdapter foodListAdapter;

    private int ClassID;

    private int PageCount = 1;

    public Map<Integer,Boolean> readMap; //存储是否读过的状态

    public FoodListFragment() {
    }

    public static FoodListFragment newInstance()
    {
        return new FoodListFragment();
    }

    protected void createCache(){
        foodListCache = new FoodListCache(mHandler, ClassID);
    }

    private void getReadMap(){
        this.readMap = foodListCache.readMap;
    }

    @Override
    protected void saveDataToDataBase() {
        if(PageCount < 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    foodListCache.saveDataToDataBase();
                }
            }).start();
        }else {
            Log.d("Tag", PageCount + "---没有存储到数据库");
        }

    }

    @Override
    protected List<FoodItemBean> getFoodItemBeanList() {
        return foodListCache.getMfoodLists();
    }

    @Override
    protected int getFoodClassID() {
        return FoodClassify.food_classify_id[0];
    }

    @Override
    protected void changeLoadStatus() {
//        foodListAdapter.changeLoadStatus();
    }

    @Override
    protected void changeReadStatus(int position) {
        foodListAdapter.changeReadStatus(position);
    }


    @Override
    protected void loadFromDataBase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                foodListCache.loadDataFromDataBase();
            }
        }).start();
    }

    @Override
    protected void loadFromNet(boolean Pull) {
        if(hasData()) {
            PageCount += 1;
            foodListCache.loadFormNet(PageCount,Pull);

            Log.d("Tag", PageCount + "");
        }else {
            foodListCache.loadFormNet(PageCount,Pull);
            Log.d("Tag", PageCount + "--PageCount  ---hasData"+ hasData()+"");
        }
    }

    @Override
    protected boolean hasData() {
        return foodListCache.hasData();
    }

    @Override
    protected RecyclerView.Adapter bindAdapter() {
        getReadMap();
        foodListAdapter = new FoodListAdapter(getContext(),foodListCache,readMap);
        return foodListAdapter;
    }

    @Override
    protected void getArgs() {
        ClassID = getArguments().getInt(getString(R.string.ClassID));
        Log.d("Tag",ClassID+"");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(getChildFragmentManager().getFragments()!=null){
            getChildFragmentManager().getFragments().clear();
        }
        Log.d("Tag","onDetach()");
    }
}
