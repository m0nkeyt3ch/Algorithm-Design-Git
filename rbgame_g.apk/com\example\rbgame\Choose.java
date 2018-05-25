package com.example.rbgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class Choose extends Activity {
    private viewChoose mview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        this.mview = (viewChoose) findViewById(R.id.viewChoose1);
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                Choose.this.mview.invalidate();
                mHandler.postDelayed(this, 50);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.setClass(this, Welcome.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
