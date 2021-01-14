package com.example.tinyrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.tinyrouter.annotation.Router;

@Router(path = "/second")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}