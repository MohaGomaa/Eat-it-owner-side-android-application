package com.example.mohamedahmedgomaa.restappservier;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Database.UserData;
import com.example.mohamedahmedgomaa.restappservier.Interface.ItemClickListener;
import com.example.mohamedahmedgomaa.restappservier.Model.Category;
import com.example.mohamedahmedgomaa.restappservier.Service.ListenOrder;
import com.example.mohamedahmedgomaa.restappservier.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
   TextView txtFullName;
    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    DrawerLayout drawer;
    //New Category
    Category newCategory;

    UserData userData;
    // Select Image from Galery
    Uri saveUri;
    //Firebase Storage
  FirebaseStorage storage;
  StorageReference storageReference;
  //
    MaterialEditText edtName;
    Button btnSelect,btnUpload;

    CircleImageView profileImg;
    ImageView ProfileImgLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
         drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerview=navigationView.getHeaderView(0);


        profileImg=headerview.findViewById(R.id.imageview);

        Paper.init(this);

        String img= Paper.book().read(Comman.Img_Profile);

        if(img !=null)
        {
            Picasso.get().load(img).into(profileImg);
        }
        //  another way to save photo internal
        userData=new UserData(this);

        Cursor c = userData.getImage();
        if (!c.moveToNext()) {

        } else {


            String iamge = c.getString(0);
            Picasso.get().load(iamge).into(profileImg);
        }

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogProfile();

            }
        });
        txtFullName=headerview.findViewById(R.id.txtFullName);
        txtFullName.setText(Comman.current_User.getName());

        recyclerView=findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(Comman.isConnectedToInternet(Home.this)) {
            loadMenu();
            Intent serviceIntent = new Intent(Home.this, ListenOrder.class);
            startService(serviceIntent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please check your connection!!", Toast.LENGTH_SHORT).show();

            return;
        }
    }

    protected  void showDialog() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(Home.this);
        aBuilder.setTitle("Add new Category");
        aBuilder.setMessage("Please Fill Full Information");

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);
        
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
        aBuilder.setView(add_menu_layout);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtName.getText().toString().equals(null )||edtName.getText().toString().equals(""))
                {        edtName.setError("Enter Food Name");
                    return;
                }

                    dialog.dismiss();
                    if(newCategory !=null)
                    {
                        category.push().setValue(newCategory);
                        Snackbar.make(drawer,"New Category"+newCategory.getName()+"Was Added",Snackbar.LENGTH_SHORT).show();
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
                            newCategory=new Category(edtName.getText().toString(),uri.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Comman.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null)
        {
            saveUri=data.getData();

            btnSelect.setText("Image Selected !");

        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Comman.PICK_IMAGE_REQUEST );
    }

    private void loadMenu() {
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {

            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());

                Picasso.get().load(category.getImage()).into(menuViewHolder.imgView);
                final Category clickItem=category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //   Toast.makeText(Home.this,""+clickItem.getName(),Toast.LENGTH_SHORT).show();
                      Intent foodIntent=new Intent(Home.this,FoodList.class);
                        foodIntent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodIntent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);
        }

        if (id == R.id.nav_profile) {
            Intent ProfileIntent=new Intent(Home.this,Profile.class);
            startActivity(ProfileIntent);

        }
        else if (id == R.id.nav_menu) {

        }
      else if (id == R.id.nav_signout) {
            //destroy remember me
            SharedPreferences sharedPreferences=getSharedPreferences("LoginData",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent SignIn=new Intent(this, SignIn.class);
            SignIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SignIn);

        }
        else if(id==R.id.nav_changePass){
            showChangePasswordDialog();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()==Comman.UPDATE)
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle()==Comman.DELETE)
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key, Category item) {
        category.child(key).removeValue();
        Toast.makeText(this,"Category "+item.getName()+" is Deleted",Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Category item) {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(Home.this);
        aBuilder.setTitle("Update Category");
        aBuilder.setMessage("Please Fill all Information");
        aBuilder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName=add_menu_layout.findViewById(R.id.edtName);
        btnSelect=add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload=add_menu_layout.findViewById(R.id.btnUpload);

        edtName.setText(item.getName());
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
        aBuilder.setView(add_menu_layout);
        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edtName.getText().toString().equals(null )||edtName.getText().toString().equals(""))
                {        edtName.setError("Enter Category Name");
                    return;
                }
                dialog.dismiss();

              item.setName(edtName.getText().toString());
              category.child(key).setValue(item);
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

    private void changeImage(final Category item) {

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


    private void showChangePasswordDialog() {
        final AlertDialog.Builder alertdialog=new AlertDialog.Builder(Home.this);
        alertdialog.setTitle("Change Password");
        alertdialog.setMessage("Please fill all Fields");
        LayoutInflater layoutInflater=LayoutInflater.from(Home.this);
        View passLayout=layoutInflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText pass=passLayout.findViewById(R.id.edtPassword);
        final MaterialEditText newPass=passLayout.findViewById(R.id.edtNewPassword);
        final MaterialEditText conPass=passLayout.findViewById(R.id.edtConPassword);
        alertdialog.setView(passLayout);

        alertdialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(pass.getText().toString().equals(null )||pass.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You missed Current Password Empty",Toast.LENGTH_SHORT).show();

                    pass.setError("Enter Password");
                    return;
                }
                if(newPass.getText().toString().equals(null )||newPass.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You missed New Password Empty",Toast.LENGTH_SHORT).show();

                    pass.setError("Enter New  Password");
                    return;
                }
                if(newPass.getText().toString().length()<8)
                {
                    Toast.makeText(getApplicationContext(),"New Password very small must > 8 ",Toast.LENGTH_SHORT).show();

                    pass.setError("Rang between 8 & 20 number ");
                    return;
                }
                if(conPass.getText().toString().equals(null )||conPass.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You missed Confirm Password Empty",Toast.LENGTH_SHORT).show();

                    conPass.setError("Enter confirm Password");
                    return;
                }
                dialog.dismiss();
                if(pass.getText().toString().equals(Comman.current_User.getPassword()))
                {
                    if(newPass.getText().toString().equals(conPass.getText().toString()))
                    {
                        Map<String,Object> passwordUpdate=new HashMap<>();
                        passwordUpdate.put("password",newPass.getText().toString());
                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("users");
                        user.child(Comman.current_User.getPhone()).updateChildren(passwordUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                SharedPreferences sharedPreferences=getSharedPreferences("LoginData",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.clear();
                                editor.commit();
                                Toast.makeText(getApplicationContext(),"Password was Change",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Not Matched Password",Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {


                      Toast.makeText(getApplicationContext(),"Worng Current Password",Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertdialog.show();
    }

    private void showDialogProfile() {
        AlertDialog.Builder aBuilder=new AlertDialog.Builder(Home.this);
        aBuilder.setTitle("Setect Profile Photo");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_Profile_layout=inflater.inflate(R.layout.add_profile_image_layout,null);

        ProfileImgLay=add_Profile_layout.findViewById(R.id.profileLay);
        String img=Paper.book().read(Comman.Img_Profile);

        if(img !=null) {

            Picasso.get().load(img).into(ProfileImgLay);
        }
        else {
            Cursor c = userData.getImage();
            if (!c.moveToNext()) {

            } else {

                String iamge = c.getString(0);
                Picasso.get().load(iamge).into(ProfileImgLay);

            }
        }
        btnSelect=add_Profile_layout.findViewById(R.id.btnSelect);
        btnUpload=add_Profile_layout.findViewById(R.id.btnUpload);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageProfile();
                if(saveUri !=null) {
                    Picasso.get().load(saveUri.toString()).into(ProfileImgLay);
                }

            }
        });
        aBuilder.setView(add_Profile_layout);


        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(saveUri !=null)
                {
                    String img= Paper.book().read(Comman.Img_Profile);
                    if(img !=null)
                    {
                        Paper.book().destroy();

                    }
                    else
                    {
                        Cursor c = userData.getImage();
                        if (!c.moveToNext()) {

                        } else {

                            userData.clear();

                        }
                    }

                    Paper.book().write(Comman.Img_Profile,saveUri.toString());
                    userData.addImg(saveUri.toString());
                    Picasso.get().load(saveUri.toString()).into(profileImg);
                    Toast.makeText(getApplicationContext(),"Image of  Profile  changed",Toast.LENGTH_SHORT).show();

                }
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Image of  Profile Not changed",Toast.LENGTH_SHORT).show();
            }
        });
        aBuilder.show();
    }

    private void uploadImageProfile() {
        if(saveUri !=null)
        {
            final ProgressDialog mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading.....");
            mProgressDialog.show();
            String imageName= UUID.randomUUID().toString();
            //   final  StorageReference imageFolder= storageReference.child("images/"+imageName);

            mProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Uploaded !!",Toast.LENGTH_SHORT).show();



        }

    }




}
