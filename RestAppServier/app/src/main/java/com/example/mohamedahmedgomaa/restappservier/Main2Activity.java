package com.example.mohamedahmedgomaa.restappservier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;

public class Main2Activity extends AppCompatActivity {
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        spinner =findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my way","Shipped");

    }
}
