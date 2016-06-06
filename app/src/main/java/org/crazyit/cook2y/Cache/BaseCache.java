package org.crazyit.cook2y.Cache;

import org.crazyit.cook2y.Bean.FoodItemBean;

import java.util.List;

/**
 * Created by chenti on 2016/5/27.
 */
public abstract class BaseCache {

    public List<FoodItemBean> mfoodLists ;

    public boolean hasData(){
        return !mfoodLists.isEmpty();
    }

    public List<FoodItemBean> getMfoodLists(){
        return mfoodLists;
    }
}
