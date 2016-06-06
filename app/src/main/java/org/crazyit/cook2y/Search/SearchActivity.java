package org.crazyit.cook2y.Search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.crazyit.cook2y.Adapter.CollectionsAdapter;
import org.crazyit.cook2y.Adapter.SearchResultsAdapter;
import org.crazyit.cook2y.Bean.FoodBean;
import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Bean.SearchBean;
import org.crazyit.cook2y.Bean.SearchItemBean;
import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.Constants.Constants;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.FoodDetail.FoodDetailActivity;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.Utils.OnRecyclerItemClickListener;
import org.crazyit.cook2y.api.FoodInfoApi;
import org.crazyit.cook2y.api.FoodNameApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenti on 2016/5/16.
 */
public class SearchActivity extends AppCompatActivity {

    private Toolbar search_results_toolbar;
    private RecyclerView search_results_recycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar searchBar;
    private FloatingActionButton searchFab;

    private List<SearchItemBean> mSearchResults;

    private FoodListCache foodListCache;

    private String searchFoodName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        mSearchResults = new ArrayList<>();
        foodListCache = new FoodListCache(handler);

        searchFoodName = getSearchInput();

        initView();

        loadSearchResultsFromNet(searchFoodName);
    }

    private void loadSearchResultsFromNet(String searchFoodName) {

        Request.Builder builder = new Request.Builder();
        builder.url(FoodNameApi.food_name_url + "?name=" + searchFoodName+"");
//        builder.url(FoodInfoApi.food_info_url + "?id=" + 105597);
        Log.d("Tag",FoodInfoApi.food_info_url + "?id=" + 105597);
        Request request = builder.build();
        HttpUtil.enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(Constants.ID_SEARCH_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful() == false){
                    handler.sendEmptyMessage(Constants.ID_SEARCH_FAIL);
                    return;
                }
                String res = response.body().string();
                Log.d("Tag",res);
                    Gson gson = new Gson();
                SearchBean searchBean = gson.fromJson(res,SearchBean.class);
                SearchItemBean[] searchItemBean = searchBean.getTngou();
                for(SearchItemBean sib:searchItemBean){
                    mSearchResults.add(sib);
                }

                if(0 == mSearchResults.size()){
                    handler.sendEmptyMessage(Constants.ID_SEARCH_NULL);
                }else {
                    handler.sendEmptyMessage(Constants.ID_SEARCH_SUCC);
                }
            }

        });
    }

    private String getSearchInput() {
        String searchFoodName = getIntent().getStringExtra(getString(R.string.search_food_name));
        return searchFoodName;
    }

    private void initView() {
        search_results_toolbar = (Toolbar) findViewById(R.id.collection_toolbar);
        search_results_recycleView = (RecyclerView) findViewById(R.id.collection_recycleView);
        searchBar = (ProgressBar) findViewById(R.id.searchBar);
        searchBar.setVisibility(View.VISIBLE);
        searchFab = (FloatingActionButton) findViewById(R.id.collections_fab);

        setSupportActionBar(search_results_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_results));
        search_results_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_results_recycleView.scrollToPosition(0);
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ID_SEARCH_FAIL:
                    Toast.makeText(SearchActivity.this,getString(R.string.search_fail),Toast.LENGTH_SHORT).show();
                    break;
                case Constants.ID_SEARCH_SUCC:
                    initAdpater();
                    searchBar.setVisibility(View.GONE);
                    break;
                case Constants.ID_SEARCH_NULL:
                    Toast.makeText(SearchActivity.this,getString(R.string.search_null),Toast.LENGTH_SHORT).show();
                    searchBar.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    });

    private void initAdpater() {
        mLayoutManager = new LinearLayoutManager(this);
        search_results_recycleView.setAdapter(new SearchResultsAdapter(this,mSearchResults));
        search_results_recycleView.setItemAnimator(new DefaultItemAnimator());
        search_results_recycleView.setLayoutManager(mLayoutManager);

        search_results_recycleView.addOnItemTouchListener(new OnRecyclerItemClickListener(search_results_recycleView) {
            @Override
            protected void onItemClick(RecyclerView.ViewHolder vh) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.search_food_info),mSearchResults.get(vh.getLayoutPosition()));
                Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("ClassID",-1); //表示这是搜索的结果
                startActivity(intent);
            }

            @Override
            protected void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });

        search_results_recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    searchFab.setVisibility(View.VISIBLE);
                }else {
                    searchFab.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
