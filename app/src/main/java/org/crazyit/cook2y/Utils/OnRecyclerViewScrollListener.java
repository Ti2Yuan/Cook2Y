package org.crazyit.cook2y.Utils;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.okhttp.Request;

/**
 * Created by chenti on 2016/5/24.
 */
public abstract class OnRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private FloatingActionButton fab;

    int lastVisibleItem;

    private LinearLayoutManager mLayoutManager;

    public OnRecyclerViewScrollListener(FloatingActionButton fab,LinearLayoutManager mLayoutManager) {
        this.fab = fab;
        this.mLayoutManager = mLayoutManager;
    }



    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(newState == recyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()){
            UpToLoadData();
        }
    }

    public abstract void UpToLoadData();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(dy > 0){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }
        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
    }
}
