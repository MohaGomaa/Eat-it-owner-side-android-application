package com.example.mohamedahmedgomaa.restappservier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button  btnSignIn;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn=findViewById(R.id.btnSignIn);
        txtSlogan=findViewById(R.id.txtSlogan);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignIn=new Intent(MainActivity.this, SignIn.class);
                startActivity(SignIn);
            }
        });

        SharedPreferences sharedPreferences =getSharedPreferences("LoginData",MODE_PRIVATE);
        String  phone=sharedPreferences.getString(Comman.USER_KEY,null);
        String  pwd=sharedPreferences.getString(Comman.PWD_KEY,null);


        if(phone!=null && pwd !=null)
        {
            if(!phone.isEmpty()&& !pwd.isEmpty())
                login(phone,pwd);
        }
    }

    private void login(final String phone, final String pwd) {
        final ProgressDialog mProgressDialog=new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Please waiting.....");
        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table=database.getReference("users");

        if (Comman.isConnectedToInternet(getApplicationContext())) {


            mProgressDialog.show();

            table.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(phone).exists()) {

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (pwd.equals(user.getPassword())) {
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Comman.current_User = user;
                            Toast.makeText(getApplicationContext(),  "Success Login ", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();
                            startActivity(intent);


                            finish();
                        } else {
                            mProgressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mProgressDialog.dismiss();

                         Toast.makeText(getApplicationContext(), "User not Exists in Database!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
             Toast.makeText(getApplicationContext(),"Please check your connection!!", Toast.LENGTH_SHORT).show();

            return;
        }
    }
}
