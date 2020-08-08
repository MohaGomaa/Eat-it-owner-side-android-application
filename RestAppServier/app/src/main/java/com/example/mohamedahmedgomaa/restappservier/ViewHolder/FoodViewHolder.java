package com.example.mohamedahmedgomaa.restappservier.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restappservier.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        ,View.OnCreateContextMenuListener{
    public TextView food_name;
    public ImageView food_image;
    public ItemClickListener itemClickListener;
    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        food_image = itemView.findViewById(R.id.food_image);
        itemView.setOnCreateContextMenuListener(this);
        //   itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
    public  void setItemClickListener(ItemClickListener itmClickListener){
        this.itemClickListener = itmClickListener;


    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(0,0,getAdapterPosition(), Comman.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Comman.DELETE);
    }
}