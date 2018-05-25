package com.example.rbgame;

import android.text.format.Time;

public class Timer {
    Time t = new Time();

    public int getvalue() {
        this.t.setToNow();
        return ((((((this.t.month * 36) + this.t.monthDay) * 3600) * 24) + this.t.second) + (this.t.minute * 60)) + (this.t.hour * 3600);
    }
}
