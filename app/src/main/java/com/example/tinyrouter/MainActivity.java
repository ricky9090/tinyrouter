package com.example.tinyrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.tinyrouter.annotation.Router;
import com.example.tinyrouter.core.TinyRouter;

@Router(path = "/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jump_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyRouter.from(MainActivity.this)
                        .to("/sub/mainpage")
                        .start();
            }
        });
    }
}