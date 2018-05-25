package com.example.rbgame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Help extends Activity {
    private int loaded_picture = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        final ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
        this.loaded_picture = 1;
        mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.p1));
        mImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Bitmap bmp = null;
                switch (Help.this.loaded_picture) {
                    case 1:
                        bmp = BitmapFactory.decodeResource(Help.this.getResources(), R.drawable.p2);
                        Help.this.loaded_picture = 2;
                        break;
                    case 2:
                        bmp = BitmapFactory.decodeResource(Help.this.getResources(), R.drawable.p3);
                        Help.this.loaded_picture = 3;
                        break;
                    case 3:
                        bmp = BitmapFactory.decodeResource(Help.this.getResources(), R.drawable.p4);
                        Help.this.loaded_picture = 4;
                        break;
                    case 4:
                        Help.this.finish();
                        break;
                }
                mImageView.setImageBitmap(bmp);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 82 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }
}
