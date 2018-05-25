package com.example.rbgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class viewChoose extends View implements OnTouchListener {
    private String IDschoolname = null;
    private Bitmap bitmap1 = null;
    private Bitmap bitmap2 = null;
    private Bitmap bitmap3 = null;
    private int column;
    private int count;
    private int deltaY;
    public boolean[] doneidlist;
    private Paint font;
    private int iconsize;
    private int isdown;
    private int max_row;
    private int maxplayed;
    private int shiftY;
    private float starttouchX;
    private float starttouchY;
    private String stringID = null;
    private String stringschool = null;
    public boolean[] uploadedlist;
    private int viewh;
    private int vieww;
    private int x;

    public boolean onTouch(View v, MotionEvent event) {
        float pX;
        float pY;
        switch (event.getAction()) {
            case 0:
                pX = event.getX();
                pY = event.getY();
                this.starttouchX = pX;
                this.starttouchY = pY;
                this.isdown = 1;
                invalidate();
                return true;
            case 1:
                pX = event.getX();
                pY = event.getY();
                move_to(pY - this.starttouchY);
                freshydel();
                if (Math.abs(pX - this.starttouchX) + Math.abs(pY - this.starttouchY) > 40.0f) {
                    this.isdown = 0;
                }
                if (this.isdown != 0) {
                    check_and_go((this.starttouchX + pX) * 0.5f, (this.starttouchY + pY) * 0.5f);
                }
                this.isdown = 0;
                invalidate();
                break;
            case 2:
                pX = event.getX();
                pY = event.getY();
                if (Math.abs(pX - this.starttouchX) + Math.abs(pY - this.starttouchY) > 20.0f) {
                    this.isdown = 0;
                }
                move_to(pY - this.starttouchY);
                invalidate();
                break;
        }
        return false;
    }

    private void check_and_go(float x, float y) {
        int i = 0;
        while (i <= get_todo() && i < get_count()) {
            if (Math.abs(x - ((float) (((i % get_column()) * this.iconsize) + (this.iconsize / 2)))) + Math.abs(y - ((float) (((((i / get_column()) * this.iconsize) + this.shiftY) + this.deltaY) + (this.iconsize / 2)))) < ((float) ((this.iconsize * 4) / 5))) {
                Bundle bundle = ((Activity) getContext()).getIntent().getExtras();
                Bundle newBundle = new Bundle();
                newBundle.putString("school", bundle.getString("school"));
                newBundle.putString("ID", bundle.getString("ID"));
                newBundle.putString("IPport", bundle.getString("IPport"));
                newBundle.putInt("toPlay", i + 1);
                Intent intent = new Intent();
                intent.setClass((Activity) getContext(), Game.class);
                intent.putExtras(newBundle);
                ((Activity) getContext()).startActivity(intent);
                ((Activity) getContext()).finish();
            }
            i++;
        }
    }

    private int get_todo() {
        return this.maxplayed;
    }

    public int get_count() {
        this.count = 100;
        return 100;
    }

    private int get_column() {
        this.column = 6;
        this.max_row = get_count() / this.column;
        if (get_count() % this.column != 0) {
            this.max_row++;
        }
        return this.column;
    }

    private void mydetails() {
        int i;
        setOnTouchListener(this);
        Bundle bundle = ((Activity) getContext()).getIntent().getExtras();
        this.stringschool = bundle.getString("school");
        this.stringID = bundle.getString("ID");
        this.IDschoolname = this.stringID + this.stringschool;
        this.shiftY = 0;
        this.deltaY = 0;
        this.maxplayed = new Levelchecker((Activity) getContext()).get_maxplayed();
        this.doneidlist = new boolean[get_count()];
        this.uploadedlist = new boolean[get_count()];
        String[] flist = ((Activity) getContext()).fileList();
        for (i = 0; i < flist.length; i++) {
            if (flist[i].startsWith("S" + this.IDschoolname)) {
                int pos1 = flist[i].indexOf(".");
                this.doneidlist[Integer.parseInt(flist[i].substring(pos1 + 1, flist[i].indexOf(".", pos1 + 1))) - 1] = true;
            }
        }
        for (i = 0; i < flist.length; i++) {
            if (flist[i].startsWith("U" + this.IDschoolname)) {
                pos1 = flist[i].indexOf(".");
                this.uploadedlist[Integer.parseInt(flist[i].substring(pos1 + 1, flist[i].indexOf(".", pos1 + 1))) - 1] = true;
            }
        }
        if (this.stringID.equals("TEST")) {
            new Builder((Activity) getContext()).setTitle("warning").setMessage("Username TEST, your record will be uploaded anonymously").setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    }

    public viewChoose(Context context) {
        super(context);
        mydetails();
    }

    public viewChoose(Context context, AttributeSet attrs) {
        super(context, attrs);
        mydetails();
    }

    public viewChoose(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mydetails();
    }

    private void freshydel() {
        this.deltaY += this.shiftY;
        this.shiftY = 0;
    }

    private void move_to(float dd) {
        if (this.deltaY + ((int) dd) <= 0 && ((double) ((this.deltaY + ((int) dd)) + (this.iconsize * this.max_row))) >= ((double) this.viewh) * 0.8d) {
            this.shiftY = (int) dd;
        }
    }

    private void load_pic() {
        this.vieww = getWidth();
        this.viewh = getHeight();
        get_count();
        this.iconsize = this.vieww / get_column();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.boxgreen);
        int oldwidth = bmp.getWidth();
        float ratio = ((float) this.iconsize) / ((float) oldwidth);
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        this.bitmap1 = Bitmap.createBitmap(bmp, 0, 0, oldwidth, oldwidth, matrix, true);
        this.bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.boxblue), 0, 0, oldwidth, oldwidth, matrix, true);
        this.bitmap3 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.boxgreen), 0, 0, oldwidth, oldwidth, matrix, true);
        this.font = new Paint();
        this.font.setColor(-65536);
        this.font.setTextSize((float) (this.iconsize / 3));
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.bitmap1 == null) {
            load_pic();
        }
        this.x++;
        Paint mPaint = new Paint();
        for (int i = 0; i < get_count(); i++) {
            int dx = (i % get_column()) * this.iconsize;
            int dy = (((i / get_column()) * this.iconsize) + this.shiftY) + this.deltaY;
            if (dy >= (-this.iconsize) && dy <= this.viewh) {
                if (this.doneidlist[i]) {
                    canvas.drawBitmap(this.bitmap2, (float) dx, (float) dy, mPaint);
                } else {
                    canvas.drawBitmap(this.bitmap1, (float) dx, (float) dy, mPaint);
                }
                canvas.drawText(String.valueOf(i + 1), (float) ((((this.iconsize * 5) / 8) + dx) - ((String.valueOf(i).length() * this.iconsize) / 8)), (float) (((this.iconsize * 5) / 8) + dy), this.font);
            }
        }
    }
}
