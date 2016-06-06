package org.crazyit.cook2y.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.crazyit.cook2y.Bean.ItemInfoBean;
import org.crazyit.cook2y.DataBase.CollectionList;
import org.crazyit.cook2y.FoodDetail.FoodDetailActivity;
import org.crazyit.cook2y.R;
import org.crazyit.cook2y.Utils.HttpUtil;
import org.crazyit.cook2y.api.FoodListApi;

import java.util.List;

/**
 * Created by chenti on 2016/5/9.
 */
public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder> {

    private Context context;
    private List<CollectionList> mCollectionList;
    public CollectionsAdapter(Context context,List<CollectionList> mCollectionList){
        this.context = context;
        this.mCollectionList = mCollectionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food,parent,false);
        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title.setText(mCollectionList.get(position).getName());
        holder.food.setText(mCollectionList.get(position).getFood());
        holder.visit.setText(mCollectionList.get(position).getCount()+"");

        if(HttpUtil.isWIFI == false){
            holder.imageView.setImageURI(null);
        }else {
            holder.imageView.setImageURI(Uri.parse(FoodListApi.food_img_url_2 + mCollectionList.get(position).getImg()));
        }

//        holder.parentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(context.getString(R.string.collection_item),
//                        mCollectionList.get(position));
//                Intent intent = new Intent(context, FoodDetailActivity.class);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View parentView;
        public TextView title;
        public TextView food;
        public TextView visit;
        public SimpleDraweeView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            title = (TextView)parentView.findViewById(R.id.title);
            food = (TextView) parentView.findViewById(R.id.food);
            visit = (TextView) parentView.findViewById(R.id.visit);
            imageView = (SimpleDraweeView)parentView.findViewById(R.id.image);
        }
    }
}
