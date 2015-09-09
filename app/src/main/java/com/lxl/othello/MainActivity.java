package com.lxl.othello;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_new_game).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalGameActivity.navigateFrom(MainActivity.this);
            }
        });
    }
}
