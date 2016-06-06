package org.crazyit.cook2y.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.crazyit.cook2y.Bean.FoodItemBean;
import org.crazyit.cook2y.Cache.FoodListCache;
import org.crazyit.cook2y.FoodDetail.FoodDetailActivity;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Setting.Settings;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.api.FoodListApi;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenti on 2016/4/12.
 */
public class FoodListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<FoodItemBean> mfoodLists = null;
    private FoodListCache foodListCache;
    private Settings mSettings;

    public Map<Integer,Boolean> readMap;
//    public int load_status = 0;
    public boolean read_or_not = false;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;


    public FoodListAdapter(Context context, FoodListCache foodListCache,Map<Integer,Boolean> readMap) {
        this.context = context;
        this.foodListCache = foodListCache;
        mfoodLists = getList();
        mSettings = Settings.newInstance();
        this.readMap = readMap;
    }

//    private void initReadMap(){
//        for(int i =0 ;i<mfoodLists.size();i++){
//            readMap.put(i,0);
//            Log.d("readMap",0+"");
//        }
//    }

//    public void changeLoadStatus(){
//        if(load_status == 0) {
//            load_status = 1;
//            notifyItemChanged(getItemCount());
//        }
//    }

    public void changeReadStatus(int position){
        readMap.put(mfoodLists.get(position).getId(),true);
        notifyItemChanged(position);
//        notifyDataSetChanged();
    }

    public List<FoodItemBean> getList(){
        return foodListCache.getMfoodLists();
    }

    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
            RecyclerView.ViewHolder vh = new ItemViewHolder(view);
            return vh;
        }else if(viewType == TYPE_FOOT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foot_view, parent, false);
            RecyclerView.ViewHolder vh = new FootViewHolder(view);
            return vh;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder) {
            ItemViewHolder holder1 = (ItemViewHolder) holder;
            holder1.title.setText(mfoodLists.get(position).getName());
            holder1.food.setText(mfoodLists.get(position).getFood());
            holder1.visit.setText(mfoodLists.get(position).getCount() + "");

            read_or_not = readMap.get(mfoodLists.get(position).getId());
            if(read_or_not){
                holder1.read.setImageResource(R.mipmap.ic_remove_red_eye_black_48dp);
//                holder1.read.setText(context.getString(R.string.readed));
//                holder1.read.setTextColor(Color.RED);
            }else if(!read_or_not){
                holder1.read.setImageResource(R.mipmap.ic_remove_red_eye_white_48dp);
//                holder1.read.setText(context.getString(R.string.not_read));
//                holder1.read.setTextColor(context.getResources().getColor(R.color.text_light));
            }
//            HttpUtil.isWIFI == false
            if ( HttpUtil.isWIFI == false || mSettings.getBoolean(Settings.NO_PICTURE, false)) {
                holder1.imageView.setImageURI(null);
            } else {
                Log.d("Tag", FoodListApi.food_img_url_1 + mfoodLists.get(position).getImg());
                holder1.imageView.setImageURI(Uri.parse(FoodListApi.food_img_url_2 + mfoodLists.get(position).getImg()));
            }
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            footViewHolder.loading.setText(context.getString(R.string.loading));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOT;
        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mfoodLists.size()+1;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public View parentView;
        public TextView title;
        public TextView food;
        public TextView visit;
        public ImageView read;
        public SimpleDraweeView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            title = (TextView)parentView.findViewById(R.id.title);
            food = (TextView) parentView.findViewById(R.id.food);
            visit = (TextView) parentView.findViewById(R.id.visit);
            read = (ImageView) parentView.findViewById(R.id.read);
            imageView = (SimpleDraweeView)parentView.findViewById(R.id.image);
        }
    }

    public static class FootViewHolder extends RecyclerView.ViewHolder{

        public View parentView;
        public TextView loading;
        public ProgressBar progressBar;

        public FootViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            loading = (TextView)parentView.findViewById(R.id.loading);
            progressBar = (ProgressBar) parentView.findViewById(R.id.foot_view_pro);
        }
    }
}
