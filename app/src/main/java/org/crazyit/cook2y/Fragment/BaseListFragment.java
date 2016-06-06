package org.crazyit.cook2y.Fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Constants.Constants;
import org.crazyit.cook2y.Cook2YApplication;
import org.crazyit.cook2y.FoodDetail.FoodDetailActivity;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.Utils.OnRecyclerItemClickListener;
import org.crazyit.cook2y.Utils.OnRecyclerViewScrollListener;

import java.util.List;

/**
 * Created by chenti on 2016/4/12.
 */
public abstract class BaseListFragment extends Fragment {

    protected View parentView;

    protected RecyclerView recyclerView;

    protected LinearLayoutManager mLayoutManager;

    protected RecyclerView.Adapter adapter;

    protected PullToRefreshView refreshView;

    protected FloatingActionButton fab;

    protected ProgressBar progressBar;

    protected boolean refresh = true;

    protected boolean Pull = true;

    protected boolean canLoadNextPage = true;

    protected abstract RecyclerView.Adapter bindAdapter();

    protected abstract void getArgs();

    protected abstract boolean hasData();

    protected abstract void createCache();

    protected abstract void loadFromDataBase();

    protected abstract void loadFromNet(boolean Pull);

    protected abstract void saveDataToDataBase();

    protected abstract List<FoodItemBean> getFoodItemBeanList();

    protected abstract int getFoodClassID();

    protected abstract void changeLoadStatus();

    protected abstract void changeReadStatus(int position);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getArgs();
        parentView = inflater.inflate(R.layout.layout_common_list,container,false);
        recyclerView = (RecyclerView)parentView.findViewById(R.id.recycleView);
        refreshView = (PullToRefreshView) parentView.findViewById(R.id.refresh);
        fab = (FloatingActionButton)parentView.findViewById(R.id.fab);
        progressBar = (ProgressBar) parentView.findViewById(R.id.progressbar);

        recyclerView.setVisibility(View.GONE);

        createCache();
        adapter = bindAdapter();



        mLayoutManager = new LinearLayoutManager(Cook2YApplication.AppContext);
        //使用RecycleView的几个步骤
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }
        });
        //单击双击事件响应
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            protected void onItemClick(RecyclerView.ViewHolder vh) {

// Toast.makeText(getContext(),vh.getLayoutPosition()+"",Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.food_info),getFoodItemBeanList().get(vh.getLayoutPosition()));
//                Toast.makeText(context,"你点击了第" + mfoodLists.get(position).getId() + "副图",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), FoodDetailActivity.class);
//                intent.putExtra(context.getString(R.string.food_id),mfoodLists.get(position).getId());
                intent.putExtras(bundle);
                intent.putExtra("ClassID",getFoodClassID());
                startActivity(intent);

                //改变读状态
                changeReadStatus(vh.getLayoutPosition());

            }

            @Override
            protected void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });

        //滚动事件响应
        recyclerView.addOnScrollListener(new OnRecyclerViewScrollListener(fab,mLayoutManager){

            @Override
            public void UpToLoadData() {
                Pull = false;
//                changeLoadStatus();
                if(canLoadNextPage) {
                    canLoadNextPage = false;
                    loadFromNet(Pull);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });



        if(refresh) {
            refreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Pull = true;
                    if(canLoadNextPage) {
                        canLoadNextPage = false;
                        loadFromNet(Pull);
                    }

                }
            });
        }else {
            refreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshView.setRefreshing(false);
                }
            });
        }

        HttpUtil.readWIFIState();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("start","loadFromDataBase");
                loadFromDataBase();
            }
        }).start();

        return parentView;
    }

    protected Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ID_FAILURE:
                    Log.d("TAG","人群食谱下载失败");
                    break;
                case Constants.ID_SUCCESS:
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("TAG","人群食谱下载成功");
                    canLoadNextPage = true;
                    saveDataToDataBase();
                    break;
                case Constants.ID_FROM_CACHE:
                    if(hasData() == false) {
                        loadFromNet(true);
                        return false;
//                    }else if(HttpUtil.isWIFI){
//                        loadFromNet();
//                        return false;
//                    }
                    }else {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    break;
            }
            if(refresh){
                refreshView.setRefreshing(false);
            }
            adapter.notifyDataSetChanged();
            return false;
        }
    });
}
