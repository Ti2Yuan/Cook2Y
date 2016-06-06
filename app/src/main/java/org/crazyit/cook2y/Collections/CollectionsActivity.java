package org.crazyit.cook2y.Collections;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.crazyit.cook2y.Adapter.CollectionsAdapter;
import org.crazyit.cook2y.Bean.ItemInfoBean;
import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.FoodDetail.FoodDetailActivity;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Utils.OnRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenti on 2016/5/9.
 */
public class CollectionsActivity extends AppCompatActivity {

    private Toolbar collection_toolbar;
    private RecyclerView collection_recycleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton collections_fab;

    private List<CollectionList> mCollectionList;

    private FoodListCache foodListCache;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        mCollectionList = new ArrayList<>();
        foodListCache = new FoodListCache(handler);

        initView();
    }

    private void initView() {
        collection_toolbar = (Toolbar) findViewById(R.id.collection_toolbar);
        collection_recycleView = (RecyclerView) findViewById(R.id.collection_recycleView);
        collections_fab = (FloatingActionButton) findViewById(R.id.collections_fab);

        setSupportActionBar(collection_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.collections));
        collection_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collections_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection_recycleView.scrollToPosition(0);
            }
        });

        initData();

        mLayoutManager = new LinearLayoutManager(this);
        collection_recycleView.setAdapter(new CollectionsAdapter(this,mCollectionList));
        collection_recycleView.setItemAnimator(new DefaultItemAnimator());
        collection_recycleView.setLayoutManager(mLayoutManager);

        //实现点击
        collection_recycleView.addOnItemTouchListener(new OnRecyclerItemClickListener(collection_recycleView) {
            @Override
            protected void onItemClick(RecyclerView.ViewHolder vh) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.collection_item),
                        mCollectionList.get(vh.getLayoutPosition()));
                Intent intent = new Intent(CollectionsActivity.this, FoodDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            protected void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });

        //RecyclerView上下滚动显示FloatingActionButton
        collection_recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    collections_fab.setVisibility(View.VISIBLE);
                }else {
                    collections_fab.setVisibility(View.GONE);
                }
            }
        });

        //实现拖拽
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelpCallback(mCollectionList)
        {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                collection_recycleView.getAdapter().notifyItemRemoved(position);
                foodListCache.deleteCollections(mCollectionList.get(position).getFood_id());
                mCollectionList.remove(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(collection_recycleView);
    }

    private void initData() {
        mCollectionList = foodListCache.selectCollectionsAll();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
