package com.example.submodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.tinyrouter.annotation.Router;
import com.example.tinyrouter.core.TinyRouter;

@Router(path = "/sub/mainpage")
public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        findViewById(R.id.sub_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyRouter.from(SubActivity.this)
                        .to("/second")
                        .start();
            }
        });
    }
}