package com.example.rbgame;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

public class Game extends Activity {
    String ID;
    Bundle bundle;
    viewGame myGameview;
    String schoolname;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.bundle = getIntent().getExtras();
        this.schoolname = this.bundle.getString("school");
        this.ID = this.bundle.getString("ID");
        int level = this.bundle.getInt("toPlay");
        Toast.makeText(this, "Welcome to level " + String.valueOf(level) + "! " + this.schoolname + ":" + this.ID, 1).show();
        this.myGameview = (viewGame) findViewById(R.id.viewGame1);
        this.myGameview.setmG(new Graph(level, this, this.ID + this.schoolname));
        this.myGameview.postInvalidate();
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                Game.this.myGameview.invalidate();
                mHandler.postDelayed(this, 50);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (this.myGameview.tryRoolback()) {
            return true;
        }
        new Builder(this).setTitle("确认退出").setMessage("push Back to resume").setPositiveButton("Quit", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(Game.this, Choose.class);
                intent.putExtras(Game.this.bundle);
                Game.this.startActivity(intent);
                Game.this.finish();
            }
        }).setNegativeButton("Back", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
        return true;
    }
}
