package org.crazyit.cook2y.Cache;

import android.content.ContentValues;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.crazyit.cook2y.Bean.FoodBean;
import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Bean.ItemInfoBean;
import org.crazyit.cook2y.Collections.CollectionsIDSet;
import org.crazyit.cook2y.Constants.Constants;
import org.crazyit.cook2y.Cook2YApplication;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.DataBase.FoodList;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.api.FoodListApi;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenti on 2016/4/22.
 */
public class FoodListCache extends BaseCache{

    private Handler mHandler;

    public int food_id;

//    private boolean saveSucc ;

//    public List<FoodItemBean> mfoodLists ;
    public List<FoodList> foodLists ;
//    public Set<Integer> collectionsId; //存储收藏过的食谱id

    public Map<Integer,Boolean> readMap = new HashMap<>(); //存储是否读过的状态

    public FoodListCache(Handler handler,int food_id) {
        this.food_id = food_id;
        this.mHandler = handler;
//        saveSucc = false;
        mfoodLists = new ArrayList<>();
        foodLists = new ArrayList<>();
        CollectionsIDSet.setCollectionsId(selectFood_idFromCollectionList());
    }

    public FoodListCache(Handler handler){
        this.mHandler = handler;
    }

//    public boolean hasData(){
//        return !mfoodLists.isEmpty();
//    }
//
//    public List<FoodItemBean> getMfoodLists(){
//        return mfoodLists;
//    }


    //下载网络数据
    public void loadFormNet(int PageCount, final boolean Pull){
        Request.Builder builder = new Request.Builder();
        builder.url(FoodListApi.food_list_url + "?id=" + food_id +"&page=" + PageCount);
        Log.d("Tag",FoodListApi.food_list_url + "?id=" + food_id +"&page=" + PageCount);
        Request request = builder.build();
        HttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(Constants.ID_FAILURE);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful() == false){
                    mHandler.sendEmptyMessage(Constants.ID_FAILURE);
                    return;
                }

//                mfoodLists.clear();

                String res = response.body().string();
                Log.d("TAG",res);

//                deleteDatafromDataBase();     //清空数据库中此种类id的菜谱数据

                Gson gson = new Gson();
                FoodBean foodBean = gson.fromJson(res,FoodBean.class);
                Log.d("TAG",foodBean.getTngou().length+"");
                FoodItemBean[] foodItemBean = foodBean.getTngou();
                if(Pull) {
                    for (FoodItemBean fib : foodItemBean) {
                        mfoodLists.add(0, fib);
                        readMap.put(fib.getId(),false);
                    }
                }else {
                    for (FoodItemBean fib : foodItemBean) {
                        mfoodLists.add(fib);
                        readMap.put(fib.getId(),false);
                    }
                }
                mHandler.sendEmptyMessage(Constants.ID_SUCCESS);
//                saveSucc = true;   //存储成功


            }
        });
    }

    /**
     * 将数据存储到数据库中
     *
     */
    public void saveDataToDataBase(){

        for(int i = 0;i<mfoodLists.size();i++) {
            FoodList foodList = new FoodList();
            FoodItemBean foodItemBean = mfoodLists.get(i);
            foodList.setClass_id(food_id);
            foodList.setCount(foodItemBean.getCount());
            foodList.setDescription(foodItemBean.getDescription());
            foodList.setFcount(foodItemBean.getFcount());
            foodList.setFood(foodItemBean.getFood());
            foodList.setFood_id(foodItemBean.getId());
            foodList.setImages(foodItemBean.getImages());
            foodList.setImg(foodItemBean.getImg());
            foodList.setKeywords(foodItemBean.getKeywords());
            foodList.setRcount(foodItemBean.getRcount());
            foodList.setName(foodItemBean.getName());
            foodList.setCollected(false);

            foodLists.add(foodList);
        }
        DataSupport.saveAll(foodLists);
    }

    /**
     * 收藏，添加数据到Collections表
     * @param itemInfoBean
     * @param ClassID
     */
    public void saveCollections(ItemInfoBean itemInfoBean,int ClassID){
        CollectionList collectionList = new CollectionList();
        collectionList.setCount(itemInfoBean.getCount());
        collectionList.setDescription(itemInfoBean.getDescription());
        collectionList.setFcount(itemInfoBean.getFcount());
        collectionList.setFood(itemInfoBean.getFood());
        collectionList.setFood_id(itemInfoBean.getId());
        collectionList.setImages(itemInfoBean.getImages());
        collectionList.setImg(itemInfoBean.getImg());
        collectionList.setKeywords(itemInfoBean.getKeywords());
        collectionList.setMessage(itemInfoBean.getMessage());
        collectionList.setName(itemInfoBean.getName());
        collectionList.setRcount(itemInfoBean.getRcount());
        collectionList.setStatus(itemInfoBean.isStatus());
        collectionList.setUrl(itemInfoBean.getUrl());
        collectionList.setClass_id(ClassID);

        collectionList.save();

        CollectionsIDSet.getCollectionsId().add(itemInfoBean.getId());

        ContentValues values = new ContentValues();
        values.put("isCollected",true);
        DataSupport.updateAll(FoodList.class,values,"food_id=?",itemInfoBean.getId()+"");
//        mHandler.sendEmptyMessage(Constants.ID_SAVE_COLLECTIONS_SUCC);
    }

    /**
     * 删除指定收藏
     * @param foodId
     */
    public void deleteCollections(int foodId){
        DataSupport.deleteAll(CollectionList.class,"food_id = ?",foodId+"");

        CollectionsIDSet.getCollectionsId().remove(foodId);

        ContentValues values = new ContentValues();
        values.put("isCollected",false);
        DataSupport.updateAll(FoodList.class,values,"food_id=?",foodId+"");
//        mHandler.sendEmptyMessage(Constants.ID_DELETE_COLLECTIONS_SUCC);
    }

    /**
     * 删除数据库中所有数据
     */
    public static void clear_cache(){
        WebView webView = new WebView(Cook2YApplication.AppContext);
        webView.clearCache(true);
        DataSupport.deleteAll(FoodList.class);
        DataSupport.deleteAll(CollectionList.class);
    }

    /**
     * 查询数据库中此种类id的菜谱数据
     * @return
     */
    public synchronized void loadDataFromDataBase(){
//        mfoodLists.clear(); //清空列表

        List<FoodList> foodLists = DataSupport.where("class_id = ?",food_id+"")
                .find(FoodList.class);
        if(foodLists != null && foodLists.size() > 0){
            for(FoodList foodList: foodLists){
                FoodItemBean foodItemBean = new FoodItemBean();
                foodItemBean.setName(foodList.getName());
                foodItemBean.setImg(foodList.getImg());
                foodItemBean.setKeywords(foodList.getKeywords());
                foodItemBean.setCount(foodList.getCount());
                foodItemBean.setFood(foodList.getFood());
                foodItemBean.setId(foodList.getFood_id());
                this.mfoodLists.add(foodItemBean);
                readMap.put(foodList.getFood_id(),false);
            }
        }else {
            Log.d("Tag","There is no data in database");
        }
        mHandler.sendEmptyMessage(Constants.ID_FROM_CACHE);
    }

    /**
     * 根据特定id查询FoodList表中记录
     * @param FoodID
     * @return
     */
    public List<FoodList> selectFoodListDataByFoodID(int FoodID){
        List<FoodList> foodList = DataSupport.where("food_id=?",FoodID+"").find(FoodList.class);
        return foodList;
    }

    /**
     * 根据特定id查询CollectionList表中记录
     * @param FoodID
     * @return
     */
    public List<CollectionList> selectCollectionsDataByFoodID(int FoodID){
        List<CollectionList> collectionLists = DataSupport.where("food_id=?",FoodID+"").find(CollectionList.class);
        return collectionLists;
    }

    /**
     * 查询CollectionList表中记录,填充collectionsId集合
     * @return
     */
    public Set<Integer> selectFood_idFromCollectionList(){
        List<CollectionList> collectionLists = DataSupport.findAll(CollectionList.class);
        Set<Integer> collectionsID = new HashSet<>();
        for(CollectionList collectionItem:collectionLists){
            collectionsID.add(collectionItem.getFood_id());
        }
        return collectionsID;
    }

    /**
     * 查询CollectionList表中所有记录
     * @return
     */
    public List<CollectionList> selectCollectionsAll() {
        List<CollectionList> collectionLists = DataSupport.findAll(CollectionList.class);
        return collectionLists;
    }

}
