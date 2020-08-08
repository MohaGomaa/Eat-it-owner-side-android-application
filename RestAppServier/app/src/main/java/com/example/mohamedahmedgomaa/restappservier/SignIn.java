package com.example.mohamedahmedgomaa.restappservier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohamedahmedgomaa.restappservier.Comman.Comman;
import com.example.mohamedahmedgomaa.restappservier.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

public class SignIn extends AppCompatActivity {

    EditText et_phone,et_password;
    Button btnSignIn;
    CheckBox chbReme;
    FirebaseDatabase database;
    DatabaseReference user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        chbReme =  findViewById(R.id.chbRemember);
        et_password=findViewById(R.id.editPassword);
        et_phone=findViewById(R.id.editPhone);
        btnSignIn=findViewById(R.id.btnSignIn);

        database= FirebaseDatabase.getInstance();
        user=database.getReference("users");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_phone.getText().toString().equals(null )||et_phone.getText().toString().equals(""))
                {        et_phone.setError("Enter Phone Number");
                    return;
                }
                if(et_phone.getText().toString().length()<11||et_phone.getText().toString().length()>11)
                {
                    et_phone.setError("Only 11 Number");
                    return;
                }
                if(et_password.getText().toString().equals(null )||et_password.getText().toString().equals(""))
                {        et_password.setError("Enter Password");
                    return;
                }
                if(Comman.isConnectedToInternet(SignIn.this)) {

                    signInUser(et_phone.getText().toString(), et_password.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please check your connection!!", Toast.LENGTH_SHORT).show();

                    return;
                }
            }

        });
    }

    private void signInUser(final String phone, String pass) {

        final ProgressDialog mProgressDialog=new ProgressDialog(SignIn.this);
        mProgressDialog.setMessage("Please Waiting...");
        mProgressDialog.setTitle("Waiting....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final String password=pass;
        final String phonee=phone;

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phonee).exists())
                {
                    mProgressDialog.dismiss();
                    User user=  dataSnapshot.child(phone).getValue(User.class);
                    if(Boolean.parseBoolean(user.getIsStaff()))
                    {
                        if(password.equals(user.getPassword()))
                        {
                            if (chbReme.isChecked()) {
                                SharedPreferences sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString(Comman.USER_KEY, et_phone.getText().toString());
                                editor.putString(Comman.PWD_KEY, et_password.getText().toString());
                                editor.commit();

                            }
                            Intent intent = new Intent(SignIn.this, Home.class);
                            Comman.current_User = user;
                            Comman.current_User.setPhone(phone);
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Success Login",Toast.LENGTH_SHORT).show();

                            startActivity(intent);

                            finish();
                        }
                        else
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Worng Password",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Please  LogIn With Staff account",Toast.LENGTH_SHORT).show();
                    }



                }
                else
                {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"User Not Exist In Database",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
