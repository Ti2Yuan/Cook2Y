package org.crazyit.cook2y.FoodDetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Bean.ItemInfoBean;
import org.crazyit.cook2y.Bean.SearchItemBean;
import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.Collections.CollectionsIDSet;
import org.crazyit.cook2y.Constants.Constants;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.DataBase.FoodList;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Setting.Settings;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.api.FoodInfoApi;
import org.crazyit.cook2y.api.FoodListApi;

import java.io.IOException;
import java.util.List;

/**
 * Created by chenti on 2016/4/25.
 */
public class FoodDetailActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private WebView contentView;
    private SimpleDraweeView topImage;
    private NestedScrollView scrollView;
    private RelativeLayout mainContent;
    private ProgressBar progressBar;
    private ProgressBar progressBarTopPic;
    private ImageButton networkBtn;
    private boolean isCollected;
    private boolean isLoadDone;

    private FoodItemBean foodItemBean;
    private ItemInfoBean itemInfoBean;
    private CollectionList mCollectionList;
    private SearchItemBean mSearchItemBean;
    private FoodListCache foodListCache;
    private Settings mSettings;
    private int FoodID;
    private int ClassID;

    private String res;
    private StringBuilder stringBuilder;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_food_detail);

        foodListCache = new FoodListCache(handler);
        itemInfoBean = new ItemInfoBean();
        mSettings = Settings.newInstance();

        getFoodInfo();

        initView();

        Log.d("Tag",FoodID+"");
        Log.d("Tag","准备下载详情");
        loadFoodItemDetail();
        Log.d("Tag","下载成功");

    }

    private void loadFoodItemDetail(){
        if(mCollectionList != null){
            itemInfoBean.setCount(mCollectionList.getCount());
            itemInfoBean.setDescription(mCollectionList.getDescription());
            itemInfoBean.setUrl(mCollectionList.getUrl());
            itemInfoBean.setImages(mCollectionList.getImages());
            itemInfoBean.setFcount(mCollectionList.getFcount());
            itemInfoBean.setId(mCollectionList.getFood_id());
            itemInfoBean.setFood(mCollectionList.getFood());
            itemInfoBean.setKeywords(mCollectionList.getKeywords());
            itemInfoBean.setMessage(mCollectionList.getMessage());
            itemInfoBean.setName(mCollectionList.getName());
            itemInfoBean.setRcount(mCollectionList.getRcount());
            itemInfoBean.setStatus(mCollectionList.isStatus());
            itemInfoBean.setImg(mCollectionList.getImg());

            isCollected = true;

            handler.sendEmptyMessage(Constants.ID_COLLECTIONS_FROM_ALLCACHE_SUCC);
        }else if(mSearchItemBean != null) {
            itemInfoBean.setCount(mSearchItemBean.getCount());
            itemInfoBean.setDescription(mSearchItemBean.getDescription());
            itemInfoBean.setImages(mSearchItemBean.getImages());
            itemInfoBean.setFcount(mSearchItemBean.getFcount());
            itemInfoBean.setId(mSearchItemBean.getId());
            itemInfoBean.setFood(mSearchItemBean.getFood());
            itemInfoBean.setKeywords(mSearchItemBean.getKeywords());
            itemInfoBean.setMessage(mSearchItemBean.getMessage());
            itemInfoBean.setName(mSearchItemBean.getName());
            itemInfoBean.setRcount(mSearchItemBean.getRcount());
            itemInfoBean.setImg(mSearchItemBean.getImg());

            if (itemFood_isCollected()) {
                isCollected = true;
            } else {
                isCollected = false;
            }

            handler.sendEmptyMessage(Constants.ID_SEARCH_SUCC);
        } else {
            if (itemFood_isCollected()) {
                loadDetailInfoFromCache(FoodID);
                isCollected = true;
            } else {
                loadDetailInfoFromNet(FoodID);
            }
        }
    }

    /**
     * 从数据库中的CollectionList表中取出已收藏过的数据
     * @param foodID
     */
    private void loadDetailInfoFromCache(int foodID) {
        List<CollectionList> collectionList = foodListCache.selectCollectionsDataByFoodID(foodID);
        if(collectionList != null && collectionList.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.food_from_collectionList), collectionList.get(0));
            Message message = Message.obtain();
            message.what = Constants.ID_COLLECTIONS_FROM_CACHE_SUCC;
            message.setData(bundle);
            handler.sendMessage(message);
        }else {
            handler.sendEmptyMessage(Constants.ID_COLLECTIONS_FROM_CACHE_FAIL);
        };
    }

    private void initView(){
        mainContent = (RelativeLayout) findViewById(R.id.main_content);
//        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        progressBarTopPic = (ProgressBar) findViewById(R.id.progressBarTopPic);
        networkBtn = (ImageButton) findViewById(R.id.networkBtn);
        topImage = (SimpleDraweeView) findViewById(R.id.topImage);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        //getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.top_gradient));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("<head>");
        stringBuilder.append("<title>做法</title>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");
        contentView = (WebView) findViewById(R.id.webview);
        //开启javaScript调用
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();
                isLoadDone = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                displayNetworkError();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                displayNetworkError();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                contentView.loadUrl(url);
                return false;
            }
        });

        /*
         cache web page
         */

        contentView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        contentView.getSettings().setDomStorageEnabled(true);
        contentView.getSettings().setDatabaseEnabled(true);

        networkBtn.setVisibility(View.GONE);
        networkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                loadDetailInfoFromNet(FoodID);
            }
        });
    }

    private void displayNetworkError() {
        if(networkBtn != null){
            networkBtn.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
//            progressBarTopPic.setVisibility(View.VISIBLE);
        }
    }

    protected void loadDetailInfoFromNet(int foodID) {
        Request.Builder builder = new Request.Builder();
        String url = FoodInfoApi.food_info_url + "?id=" + foodID +"";
        Log.d("Tag",url);
        builder.url(url);
        Request request = builder.build();
        HttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(Constants.ID_FAILURE);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful() == false){
                    handler.sendEmptyMessage(Constants.ID_FAILURE);
                    return;
                }

                res = response.body().string();
                Log.d("Tag",res);

                Gson gson = new Gson();
                itemInfoBean = gson.fromJson(res,ItemInfoBean.class);
                Log.d("Tag",itemInfoBean.getUrl());
                Log.d("Tag",itemInfoBean.getMessage());
                handler.sendEmptyMessage(Constants.ID_SUCCESS);

            }
        });
    }

    private void getFoodInfo(){
        mCollectionList = (CollectionList) getIntent().getSerializableExtra(getString(R.string.collection_item));
        mSearchItemBean = (SearchItemBean) getIntent().getSerializableExtra(getString(R.string.search_food_info));
        if(mCollectionList != null){      //由收藏页面跳至详情页面
            FoodID = mCollectionList.getFood_id();
            ClassID = mCollectionList.getClass_id();
            title = mCollectionList.getName();
        }else if(mSearchItemBean != null){           //由搜索页面跳至详情页面
            FoodID = mSearchItemBean.getId();
            ClassID = getIntent().getIntExtra("ClassID",-2);
            title = mSearchItemBean.getName();
        } else{                             //由主页面跳至详情页面
            foodItemBean = (FoodItemBean) getIntent().getSerializableExtra(getString(R.string.food_info));
            FoodID = foodItemBean.getId();
            ClassID = getIntent().getIntExtra("ClassID", -1);
            title = foodItemBean.getName();
        }
    }

    private boolean itemFood_isCollected(){

        return  CollectionsIDSet.getCollectionsId().contains(FoodID);
//        List<FoodList> foodList = foodListCache.selectFoodListDataByFoodID(FoodID);
//        if(foodList != null && foodList.size() > 0){
//            return foodList.get(0).isCollected();
//        }else return false;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ID_FAILURE:
                    Log.d("Tag","下载失败");
                    break;
                case Constants.ID_SUCCESS:
                    Log.d("Tag",FoodListApi.food_img_url_2 + itemInfoBean.getImg());
                    showOnUIByItemInfoBean();
                    break;
                case Constants.ID_COLLECTIONS_FROM_CACHE_SUCC:
                    showOnUIByMsg(msg);
                    break;
                case Constants.ID_COLLECTIONS_FROM_CACHE_FAIL:
                    loadDetailInfoFromNet(FoodID);
                    break;
                case Constants.ID_COLLECTIONS_FROM_ALLCACHE_SUCC:
                    showOnUIByItemInfoBean();
                    break;
                case Constants.ID_SEARCH_SUCC:
                    showOnUIByItemInfoBean();
                    break;
            }
            return false;
        }
    });

    private void showOnUIByMsg(Message msg) {
        Bundle bundle = msg.getData();
        CollectionList collectionList = (CollectionList) bundle.getSerializable(getString(R.string.food_from_collectionList));
        itemInfoBean.setCount(collectionList.getCount());
        itemInfoBean.setDescription(collectionList.getDescription());
        itemInfoBean.setUrl(collectionList.getUrl());
        itemInfoBean.setImages(collectionList.getImages());
        itemInfoBean.setFcount(collectionList.getFcount());
        itemInfoBean.setId(collectionList.getFood_id());
        itemInfoBean.setFood(collectionList.getFood());
        itemInfoBean.setKeywords(collectionList.getKeywords());
        itemInfoBean.setMessage(collectionList.getMessage());
        itemInfoBean.setName(collectionList.getName());
        itemInfoBean.setRcount(collectionList.getRcount());
        itemInfoBean.setStatus(collectionList.isStatus());
        itemInfoBean.setImg(collectionList.getImg());

        showOnUIByItemInfoBean();
    }

    private void showOnUIByItemInfoBean(){
        if(HttpUtil.isWIFI == false || mSettings.getBoolean(Settings.NO_PICTURE,false)) {
            topImage.setImageURI(null);
        }else {
            topImage.setImageURI(Uri.parse(FoodListApi.food_img_url_2 + itemInfoBean.getImg()));
        }
        stringBuilder.append(itemInfoBean.getMessage());
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
        contentView.loadDataWithBaseURL(null,stringBuilder.toString(),"text/html","utf-8",null);
//        contentView.loadUrl(itemInfoBean.getUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share,menu);
        updateCollectionMenu(menu.findItem(R.id.collection));
        return super.onCreateOptionsMenu(menu);
    }

    private void updateCollectionMenu(MenuItem item) {
        if(isCollected){
            item.setIcon(R.mipmap.ic_star_black);
        }else {
            item.setIcon(R.mipmap.ic_star_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,getShareInfo());
            startActivity(Intent.createChooser(sharingIntent,"分享美食"));
        }else if(item.getItemId() == R.id.collection){
            if(isCollected){
                removeFromCollection();
                isCollected = false;
                updateCollectionMenu(item);
                Snackbar.make(mainContent,getString(R.string.remove_from_collections_succ),Snackbar.LENGTH_SHORT).show();
            }else {
                addToCollection(item);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToCollection(MenuItem item) {
        if(isLoadDone) {
            foodListCache.saveCollections(itemInfoBean, ClassID);
            isCollected = true;
            updateCollectionMenu(item);
            Snackbar.make(mainContent,getString(R.string.collected_succ),Snackbar.LENGTH_SHORT).show();
        }else {
            Snackbar.make(mainContent,getString(R.string.collected_fail),Snackbar.LENGTH_SHORT).show();
        }
    }

    private void removeFromCollection() {
        foodListCache.deleteCollections(itemInfoBean.getId());
    }

    private String getShareInfo() {
        return itemInfoBean.getName()+ getString(R.string.share_from_app) + itemInfoBean.getUrl();
    }
}
