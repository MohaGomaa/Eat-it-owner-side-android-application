package com.example.mohamedahmedgomaa.restappservier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restappservier.Model.Category;
import com.example.mohamedahmedgomaa.restappservier.Model.Food;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseDatabase database;
    DatabaseReference foodList;
    TextView txtFullname;
    String CategoryId="" ;

    Uri saveUri;
    MaterialEditText edtName,edtDisc,edtPrice,edtDesc;
    Button btnSelect,btnUpload;
    FloatingActionButton fab;
    RelativeLayout rootLayout;
    Food newFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        rootLayout=findViewById(R.id.rootLayout);
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        recyclerView=findViewById(R.id.recycle_food);
        recyclerView.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        fab=findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddFoodDialog();
            }
        });
        if(getIntent()!=null)
        {
            CategoryId =getIntent().getStringExtra("CategoryId");
        }
        if(!CategoryId.isEmpty()&&CategoryId !=null)
        {

            loadListFood(CategoryId);
        }
    }

    private void showAddFoodDialog() {

        AlertDialog.Builder aBuilder=new AlertDialog.Builder(FoodList.this);
        aBuilder.setTitle("Add new Food");
        aBuilder.setMessage("Please Fill Full Information");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_food_layout=inflater.inflate(R.layout.add_new_food_layout,null);

        edtName=add_food_layout.findViewById(R.id.edtName);
        edtPrice=add_food_layout.findViewById(R.id.edtPrice);
        edtDesc=add_food_layout.findViewById(R.id.edtDescription);
        edtDisc=add_food_layout.findViewById(R.id.edtDiscount);

        btnSelect=add_food_layout.findViewById(R.id.btnSelect);
        btnUpload=add_food_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        aBuilder.setView(add_food_layout);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtName.getText().toString().equals(null )||edtName.getText().toString().equals(""))
                {        edtName.setError("Enter Food Name");
                    return;
                }
                if(edtDesc.getText().toString().equals(null )||edtDesc.getText().toString().equals(""))
                {        edtDesc.setError("Enter Description ");
                    return;
                }
                if(edtPrice.getText().toString().equals(null )||edtPrice.getText().toString().equals(""))
                {        edtPrice.setError("Enter Price");
                    return;
                }
                if(edtDisc.getText().toString().equals(null )||edtDisc.getText().toString().equals(""))
                {        edtDisc.setError("Enter Discount");
                    return;
                }
                dialog.dismiss();
                if(newFood !=null)
                {
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout,"New Category"+newFood.getName()+"Was Added",Snackbar.LENGTH_SHORT).show();
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

    private void uploadImage() {
        {
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
                                newFood = new Food(edtName.getText().toString(), uri.toString(), edtDesc.getText().toString(), edtPrice.getText().toString(), edtDisc.getText().toString(), CategoryId);
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
    }
    private void chooseImage() {


        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Comman.PICK_IMAGE_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Comman.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null)
        {
            saveUri=data.getData();
            btnSelect.setText("Image Selected !");
        }
    }





    private void loadListFood(String categoryId) {

        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(CategoryId)
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder foodViewHolder, final Food food, final int i) {
                foodViewHolder.food_name.setText(food.getName());

                Picasso.get().load(food.getImage()).into(foodViewHolder.food_image);





                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


    }


    private void showUpdateDialog(final String key, final Food item) {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(FoodList.this);
        aBuilder.setTitle("Update Food");
        aBuilder.setMessage("Please Fill all Information");
        aBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        LayoutInflater inflater=this.getLayoutInflater();
        View add_food_layout=inflater.inflate(R.layout.add_new_food_layout,null);

        edtName=add_food_layout.findViewById(R.id.edtName);
        edtDesc=add_food_layout.findViewById(R.id.edtDescription);
        edtPrice=add_food_layout.findViewById(R.id.edtPrice);
        edtDisc=add_food_layout.findViewById(R.id.edtDiscount);

        btnSelect=add_food_layout.findViewById(R.id.btnSelect);
        btnUpload=add_food_layout.findViewById(R.id.btnUpload);

        edtName.setText(item.getName());
        edtDesc.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDisc.setText(item.getDiscount());

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });
        aBuilder.setView(add_food_layout);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(edtName.getText().toString().equals(null )||edtName.getText().toString().equals(""))
                {        edtName.setError("Enter Food Name");
                    return;
                }
                if(edtDesc.getText().toString().equals(null )||edtDesc.getText().toString().equals(""))
                {        edtDesc.setError("Enter Description ");
                    return;
                }
                if(edtPrice.getText().toString().equals(null )||edtPrice.getText().toString().equals(""))
                {        edtPrice.setError("Enter Price");
                    return;
                }
                if(edtDisc.getText().toString().equals(null )||edtDisc.getText().toString().equals(""))
                {        edtDisc.setError("Enter Discount");
                    return;
                }

                dialog.dismiss();

                item.setName(edtName.getText().toString());
                item.setDescription(edtDesc.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDiscount(edtDisc.getText().toString());
                foodList.child(key).setValue(item);
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

    private void changeImage(final Food item) {

        if(saveUri !=null)
        {
            final ProgressDialog mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();
            String imageName= UUID.randomUUID().toString();
            final  StorageReference imageFolder= storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Uploaded !!",Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mProgressDialog.setMessage("Uploaded"+progress+"%");

                }
            });

        }

    }

    private void deleteFood(String key, Food item) {
        foodList.child(key).removeValue();
        Toast.makeText(this,"Food "+item.getName()+" is Deleted",Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()==Comman.UPDATE)
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle()==Comman.DELETE)
        {
            deleteFood(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);

    }

}
