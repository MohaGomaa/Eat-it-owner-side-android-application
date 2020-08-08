package com.example.mohamedahmedgomaa.restappservier;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restappservier.Model.Category;
import com.example.mohamedahmedgomaa.restappservier.Model.Food;
import com.example.mohamedahmedgomaa.restappservier.Model.Request;
import com.example.mohamedahmedgomaa.restappservier.Model.User;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.MenuViewHolder;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Profile extends AppCompatActivity {
    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapterReq;
    DatabaseReference requests;
    MaterialSpinner spinner,spinner1;
    CircleImageView imgProfile;
    TextView userName, userPhone;
    EditText edtName;
    Button btnSelect, btnUpload;
    Category newCategory;
    FirebaseDatabase database;
    DatabaseReference category;
    //Firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;
    //
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    DrawerLayout drawer;
  Button btnOrder;
    DatabaseReference request;
    //New Category
    Uri saveUri;

    Food newFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String img = Paper.book().read(Comman.Img_Profile);

        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.phone_Number);
        imgProfile = findViewById(R.id.imgProfile);
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.recycleorder);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        spinner = findViewById(R.id.statusSpinner);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        spinner.setItems("Placed", "On my way", "Shipped");

        if (img != null)
            Picasso.get().load(img).into(imgProfile);
        userName.setText(Comman.current_User.getName());
        userPhone.setText(Comman.current_User.getPhone());
        btnOrder=findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterReq= new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                        R.layout.order_layout,
                        OrderViewHolder.class,
                        requests) {
                    @Override
                    protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                        String s=Comman.convertCodeToStatus(String.valueOf(spinner.getSelectedIndex()));
                        if(Comman.convertCodeToStatus(request.getStatus())!=s) {
                            orderViewHolder.itemView.setVisibility(View.GONE);
                            orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                        }
                        else
                        {
                            orderViewHolder.itemView.setVisibility(View.VISIBLE);
                            orderViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            orderViewHolder.txtOrderId.setText( orderViewHolder.txtOrderId.getText()+ adapterReq.getRef(i).getKey());
                            orderViewHolder.txtOrderStatus.setText( orderViewHolder.txtOrderStatus.getText()+Comman.convertCodeToStatus(request.getStatus()));
                            orderViewHolder.txtOrderPhone.setText(orderViewHolder.txtOrderPhone.getText()+request.getPhone());
                            orderViewHolder.txtOrderAddress.setText( orderViewHolder.txtOrderAddress.getText()+ request.getAddress());
                            orderViewHolder.txtOrderDate.setText(orderViewHolder.txtOrderDate.getText()+request.getDate());
                            orderViewHolder.txtOrderTime.setText(orderViewHolder.txtOrderTime.getText()+request.getTime());
                            orderViewHolder.txtOrderTotal.setText(orderViewHolder.txtOrderTotal.getText()+request.getTotal());

                        }

                        orderViewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                            }
                        });
                    }
                };

                adapterReq.notifyDataSetChanged();
                recyclerView.setAdapter(adapterReq);
            }
        });

/*

        */

    }


    public void AddCategory(View view) {

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(Profile.this);
        aBuilder.setTitle("Add new Category");
        aBuilder.setMessage("Please Fill Full Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageCategory();
            }
        });
        aBuilder.setView(add_menu_layout);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newCategory != null) {
                    category.push().setValue(newCategory);
                    Toast.makeText(Profile.this, "New Category" + newCategory.getName() + "Was Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        aBuilder.show();


    }


    private void uploadImageCategory() {
        if (saveUri != null) {
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Uploaded !!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory = new Category(edtName.getText().toString(), uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressDialog.setMessage("Uploaded" + progress + "%");

                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Comman.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();

            btnSelect.setText("Image Selected !");

        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Comman.PICK_IMAGE_REQUEST);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()==Comman.UPDATE)
        {
            showUpdateDialog(adapterReq.getRef(item.getOrder()).getKey(),adapterReq.getItem(item.getOrder()));
        }
        else if(item.getTitle()==Comman.DELETE)
        {
            deleteOrder(adapterReq.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);

    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);
        spinner1 = view.findViewById(R.id.statusSpinner);
        spinner1.setItems("Placed","On my way","Shipped");
        alertDialog.setView(view);
        final String localKey = key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setStatus(String.valueOf(spinner1.getSelectedIndex()));
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
