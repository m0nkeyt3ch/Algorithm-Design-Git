package com.example.rbgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class viewGame extends View {
    public static int MAXFIRESTATE = 30;
    static int MAX_Trigger_count = 1000;
    private int ballsize;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmapmatch;
    public int click_time_extended;
    Handler click_time_extended_handler;
    private int dot_selected;
    private int dot_to_delete;
    private Bitmap fire1;
    private Bitmap fire2;
    public int fireState;
    private int fireheight;
    private int firewidth;
    private Paint font;
    private Levelchecker levelchecker;
    private Paint linepaint;
    private Graph mG;
    public float matchFlydX;
    public float matchFlydY;
    public float matchX;
    public float matchY;
    private int matchsize;
    private int rmvDot;
    private Runnable set_click_time_extended;
    private float starttouchX;
    private float starttouchY;
    private int triggerforcecount;
    private int viewh;
    private int vieww;

    public viewGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.fireState = 0;
        this.levelchecker = null;
        this.mG = null;
        this.dot_selected = -1;
        this.dot_to_delete = -1;
        this.triggerforcecount = 100;
        this.click_time_extended = 0;
        this.click_time_extended_handler = new Handler();
        this.set_click_time_extended = new Runnable() {
            public void run() {
                viewGame.this.click_time_extended = 1;
            }
        };
        this.fireState = 0;
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!viewGame.this.mG.located) {
                    return false;
                }
                if (viewGame.this.fireState != 0) {
                    return false;
                }
                viewGame.this.triggerforcecount = viewGame.MAX_Trigger_count;
                float pX;
                float pY;
                switch (event.getAction()) {
                    case 0:
                        pX = event.getX();
                        pY = event.getY();
                        viewGame.this.starttouchX = pX;
                        viewGame.this.starttouchY = pY;
                        viewGame.this.dot_selected = viewGame.this.mG.getIdByXY((int) pX, (int) pY, ((float) (viewGame.this.ballsize * viewGame.this.ballsize)) * 1.3f);
                        if (viewGame.this.dot_selected >= 0) {
                            viewGame.this.click_time_extended = 0;
                            viewGame.this.click_time_extended_handler.postDelayed(viewGame.this.set_click_time_extended, 100);
                        }
                        viewGame.this.invalidate();
                        return true;
                    case 1:
                        pX = event.getX();
                        pY = event.getY();
                        if (viewGame.this.dot_selected >= 0 && viewGame.this.click_time_extended == 0) {
                            if (viewGame.this.mG.matchleft == 0) {
                                Toast.makeText((Activity) viewGame.this.getContext(), "No Matchs left, you have to UNDO~", 1).show();
                                viewGame.this.dot_selected = -1;
                            } else {
                                viewGame.this.dot_to_delete = viewGame.this.dot_selected;
                                viewGame.this.start_to_delete_dot(viewGame.this.dot_to_delete);
                            }
                        }
                        viewGame.this.invalidate();
                        break;
                    case 2:
                        pX = event.getX();
                        pY = event.getY();
                        if (viewGame.this.dot_selected >= 0) {
                            viewGame.this.mG.setDotPosition(viewGame.this.dot_selected, (int) pX, (int) pY);
                        }
                        viewGame.this.invalidate();
                        break;
                }
                return false;
            }
        });
    }

    private void start_to_delete_dot(int dot_to_delete) {
        this.fireState = 1;
        this.rmvDot = dot_to_delete;
        this.matchX = ((float) this.mG.matchleft) * ((float) this.matchsize);
        this.matchY = 0.0f;
        float dx = (this.mG.dotX[dot_to_delete] * 1.0f) - (this.matchX + ((float) this.matchsize));
        float dy = (this.mG.dotY[dot_to_delete] * 1.0f) - (((float) this.matchsize) * 0.5f);
        float ddl = (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
        dy /= ddl;
        this.matchFlydX = (((float) this.vieww) * 0.05f) * (dx / ddl);
        this.matchFlydY = (((float) this.vieww) * 0.05f) * dy;
    }

    public void upgrade() {
        this.levelchecker = new Levelchecker((Activity) getContext());
        int newlevel = this.mG.level + 1;
        if (this.mG.level == 100) {
            newlevel = this.mG.level;
            Toast.makeText((Activity) getContext(), "Congratulations ~, don not forget to submit your results", 1).show();
        } else {
            Toast.makeText((Activity) getContext(), "Move to level " + String.valueOf(newlevel) + "   " + this.mG.uname, 1).show();
        }
        if (this.levelchecker.get_maxplayed() == this.mG.level - 1) {
            this.levelchecker.levelup();
        }
        this.mG = new Graph(newlevel, (Activity) getContext(), this.mG.uname);
    }

    public void setmG(Graph G) {
        this.mG = G;
    }

    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (!this.mG.located) {
            loadpictures();
            this.mG.shuffleDots(this.ballsize, this.ballsize, this.vieww - this.ballsize, this.viewh - this.ballsize);
        }
        if (this.fireState == 0) {
            int i2 = this.triggerforcecount;
            this.triggerforcecount = i2 - 1;
            if (i2 >= MAX_Trigger_count / 2) {
                this.mG.triggerForce(this.vieww, (1.0f * ((float) this.triggerforcecount)) / ((float) MAX_Trigger_count));
            }
        }
        for (i = 0; i < this.mG.matchleft; i++) {
            if (i != this.mG.matchleft - 1 || this.fireState == 0) {
                canvas.drawBitmap(this.bitmapmatch, (float) (this.matchsize * i), 0.0f, this.font);
            }
        }
        if (this.fireState == 1) {
            this.matchX += this.matchFlydX;
            this.matchY += this.matchFlydY;
            float dx = (1.0f * this.mG.dotX[this.rmvDot]) - (this.matchX + ((float) this.matchsize));
            float dy = (1.0f * this.mG.dotY[this.rmvDot]) - (this.matchY + (((float) this.matchsize) * 0.5f));
            if ((dx * dx) + (dy * dy) < (((float) (this.vieww * this.vieww)) * 0.05f) * 0.05f) {
                this.fireState = 2;
                this.dot_to_delete = -1;
            }
            canvas.drawBitmap(this.bitmapmatch, this.matchX, this.matchY, this.font);
        }
        if (this.fireState >= 2) {
            this.fireState++;
            i = 0;
            while (i < this.mG.size) {
                if (this.mG.dotExist[i] && this.mG.map[i][this.rmvDot]) {
                    int px = ((int) ((this.mG.dotX[i] * ((float) this.fireState)) + (this.mG.dotX[this.rmvDot] * ((float) (MAXFIRESTATE - this.fireState))))) / MAXFIRESTATE;
                    int py = ((int) ((this.mG.dotY[i] * ((float) this.fireState)) + (this.mG.dotY[this.rmvDot] * ((float) (MAXFIRESTATE - this.fireState))))) / MAXFIRESTATE;
                    canvas.drawLine(this.mG.dotX[i], this.mG.dotY[i], (float) px, (float) py, this.linepaint);
                    if (this.fireState % 2 == 0) {
                        canvas.drawBitmap(this.fire1, (float) (px - (this.firewidth / 2)), (float) (py - this.fireheight), this.font);
                    } else {
                        canvas.drawBitmap(this.fire2, (float) (px - (this.firewidth / 2)), (float) (py - this.fireheight), this.font);
                    }
                }
                i++;
            }
        }
        i = 0;
        while (i < this.mG.size) {
            if (this.mG.dotExist[i]) {
                int j = i + 1;
                while (j < this.mG.size) {
                    if (this.mG.map[i][j] && this.mG.dotExist[j] && (this.fireState < 2 || !(this.rmvDot == i || this.rmvDot == j))) {
                        canvas.drawLine(this.mG.dotX[i], this.mG.dotY[i], this.mG.dotX[j], this.mG.dotY[j], this.linepaint);
                    }
                    j++;
                }
            }
            i++;
        }
        for (i = 0; i < this.mG.size; i++) {
            if (this.mG.dotExist[i]) {
                if (i == this.dot_to_delete) {
                    canvas.drawBitmap(this.bitmap1, this.mG.dotX[i] - ((float) (this.ballsize / 2)), this.mG.dotY[i] - ((float) (this.ballsize / 2)), this.font);
                } else {
                    canvas.drawBitmap(this.bitmap2, this.mG.dotX[i] - ((float) (this.ballsize / 2)), this.mG.dotY[i] - ((float) (this.ballsize / 2)), this.font);
                }
                canvas.drawText(String.valueOf(this.mG.deg[i]), (this.mG.dotX[i] - ((float) ((this.ballsize * 3) / 16))) - ((float) ((String.valueOf(i).length() * this.ballsize) / 18)), this.mG.dotY[i] + ((float) ((this.ballsize * 2) / 8)), this.font);
            }
        }
        if (this.fireState == MAXFIRESTATE) {
            this.fireState = 0;
            if (this.mG.deleteDot(this.rmvDot)) {
                upgrade();
            }
        }
    }

    public boolean tryRoolback() {
        if (this.dot_to_delete != -1 || this.fireState != 0) {
            return true;
        }
        if (this.mG.delstack == 0) {
            return false;
        }
        this.mG.undelDot();
        return true;
    }

    private void loadpictures() {
        this.vieww = getWidth();
        this.viewh = getHeight();
        this.ballsize = this.vieww / 10;
        this.matchsize = this.vieww / 12;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.yellowstar);
        int oldwidth = bmp.getWidth();
        float ratio = ((float) this.ballsize) / ((float) oldwidth);
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        this.bitmap1 = Bitmap.createBitmap(bmp, 0, 0, oldwidth, oldwidth, matrix, true);
        this.bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.redstar), 0, 0, oldwidth, oldwidth, matrix, true);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.match);
        oldwidth = bmp.getWidth();
        ratio = ((float) this.matchsize) / ((float) oldwidth);
        matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        this.bitmapmatch = Bitmap.createBitmap(bmp, 0, 0, oldwidth, oldwidth, matrix, true);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fire1);
        matrix = new Matrix();
        matrix.postScale(((float) this.matchsize) / ((float) bmp.getWidth()), ((float) this.matchsize) / ((float) bmp.getWidth()));
        this.fire1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        this.fire2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.fire2), 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        this.firewidth = this.fire1.getWidth();
        this.fireheight = this.fire1.getHeight();
        this.font = new Paint();
        this.linepaint = new Paint();
        this.linepaint.setColor(-16776961);
        this.linepaint.setStrokeWidth((float) (((double) this.vieww) * 0.006d));
        this.font.setColor(-256);
        this.font.setTextSize((float) ((this.ballsize * 2) / 3));
    }
}
