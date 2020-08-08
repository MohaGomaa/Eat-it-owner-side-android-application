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


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        , View.OnCreateContextMenuListener
{
    public   TextView txtMenuName;
    public ImageView imgView;
    public  ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMenuName = itemView.findViewById(R.id.menu_name);
        imgView = itemView.findViewById(R.id.menu_image);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);



    }


    public  void setItemClickListener(ItemClickListener itmClickListener){
        this.itemClickListener = itmClickListener;


    }

    @Override
    public void onClick(View v ) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(0,0,getAdapterPosition(), Comman.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Comman.DELETE);



    }
}