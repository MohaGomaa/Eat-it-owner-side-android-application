package com.example.mohamedahmedgomaa.restappservier;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restappservier.Model.Request;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.FoodViewHolder;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;
    MaterialSpinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        setContentView(R.layout.activity_order_status);
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        recyclerView=findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();

    }

    private void loadOrders() {
        adapter= new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.txtOrderId.setText(orderViewHolder.txtOrderId.getText()+adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(orderViewHolder.txtOrderStatus.getText()+Comman.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderPhone.setText(orderViewHolder.txtOrderPhone.getText()+request.getPhone());
                orderViewHolder.txtOrderAddress.setText(orderViewHolder.txtOrderAddress.getText()+request.getAddress());
                orderViewHolder.txtOrderDate.setText(orderViewHolder.txtOrderDate.getText()+request.getDate());
                orderViewHolder.txtOrderTime.setText(orderViewHolder.txtOrderTime.getText()+request.getTime());
                orderViewHolder.txtOrderTotal.setText(orderViewHolder.txtOrderTotal.getText()+request.getTotal());



                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
      adapter.notifyDataSetChanged();
      recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()==Comman.UPDATE)
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle()==Comman.DELETE)
        {
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);

    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);
        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my way","Shipped");
        alertDialog.setView(view);


        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
               /// requests.child(localKey).removeValue();
                requests.child(localKey).setValue(item);



            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();



    }

}
