package org.crazyit.cook2y.Collections;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.crazyit.cook2y.Cook2YApplication;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by chenti on 2016/5/24.
 */
public abstract class ItemTouchHelpCallback extends ItemTouchHelper.Callback {

    private List<CollectionList> mCollectionList;

    public ItemTouchHelpCallback(List<CollectionList> mCollectionList) {
        this.mCollectionList = mCollectionList;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags,swipeFlags;
        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
             dragFlags = ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT |
                    ItemTouchHelper.RIGHT;
             swipeFlags = 0;
        }else {
             dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
             swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到拖动viewHolder的position
        int fromPosition = viewHolder.getAdapterPosition();
        //得到目标viewHolder的position
        int toPosition = target.getAdapterPosition();
        if(fromPosition < toPosition){
            for(int i = fromPosition;i<toPosition;++i){
                Collections.swap(mCollectionList,i,i+1); //改变实际的数据集
            }
        }else {
            for (int i = fromPosition;i>toPosition;i--){
                Collections.swap(mCollectionList,i,i-1); //改变实际的数据集
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
        return true;
    }


    //当长按选中item的时候（拖拽开始的时候）调用
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            viewHolder.itemView.setBackgroundColor(Cook2YApplication.getContext().getResources().
                    getColor(R.color.colorPrimary));
        }
        Vibrator vib = (Vibrator) Cook2YApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(70); //震动70毫秒
        super.onSelectedChanged(viewHolder, actionState);
    }

    //当手指松开的时候（拖拽完成的时候）调用
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.WHITE);
    }
}
