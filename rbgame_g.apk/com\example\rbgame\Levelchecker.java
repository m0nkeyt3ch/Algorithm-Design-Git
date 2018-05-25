package com.example.rbgame;

import android.app.Activity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Levelchecker {
    int maxplayed = read_it();
    Activity owner;

    public Levelchecker(Activity who) {
        this.owner = who;
    }

    public int get_maxplayed() {
        return 100;
    }

    private int read_it() {
        try {
            FileInputStream is = this.owner.openFileInput("maxlevel.txt");
            this.maxplayed = Integer.parseInt(new BufferedReader(new InputStreamReader(is)).readLine());
            is.close();
            if (this.maxplayed < 0) {
                this.maxplayed = 0;
                write_it();
            }
            return this.maxplayed;
        } catch (IOException e) {
            this.maxplayed = 0;
            write_it();
            return 0;
        }
    }

    private void write_it() {
        try {
            FileOutputStream os = this.owner.openFileOutput("maxlevel.txt", 0);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
            br.write(new StringBuilder(String.valueOf(String.valueOf(this.maxplayed))).append("\n").toString());
            br.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void levelup() {
        this.maxplayed++;
        if (this.maxplayed < 100) {
            write_it();
        }
    }
}
