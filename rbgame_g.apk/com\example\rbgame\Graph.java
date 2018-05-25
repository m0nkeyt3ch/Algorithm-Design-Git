package com.example.rbgame;

import android.app.Activity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Graph {
    public int[] deg;
    public int[] delhistory;
    public int delstack;
    public int[] deltogethernumber;
    public boolean[] dotExist;
    public float[] dotX;
    public float[] dotY;
    int height;
    public ArrayList<String> history;
    public int level;
    public boolean located;
    public boolean[][] map;
    public int matchleft;
    Activity owner;
    Random random;
    int size;
    int startx;
    int starty;
    public int timeCreated = new Timer().getvalue();
    String uname;
    int width;

    private class pair_float {
        float x;
        float y;

        pair_float(float _x, float _y) {
            this.x = _x;
            this.y = _y;
        }
    }

    public Graph(int gameid, Activity Owner, String Username) {
        int i;
        this.level = gameid;
        this.uname = Username;
        this.owner = Owner;
        this.located = false;
        this.history = new ArrayList();
        this.history.add("C" + String.valueOf(gameid) + "," + String.valueOf(this.timeCreated) + "," + Username + "," + String.valueOf(this.timeCreated));
        String S = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.owner.getResources().getAssets().open("graph.txt")));
            for (i = 1; i < gameid; i++) {
                br.readLine();
            }
            S = br.readLine();
            br.close();
        } catch (IOException e) {
        }
        String[] iS = S.split(" ");
        this.size = Integer.parseInt(iS[0]);
        this.matchleft = Integer.parseInt(iS[1]);
        this.deg = new int[this.size];
        this.delhistory = new int[this.size];
        this.deltogethernumber = new int[this.size];
        this.dotExist = new boolean[this.size];
        this.delstack = 0;
        this.map = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.size, this.size});
        int ee = Integer.parseInt(iS[2]);
        for (i = 0; i < this.size; i++) {
            this.dotExist[i] = true;
            for (int j = 0; j < this.size; j++) {
                this.map[i][j] = false;
            }
        }
        int qq = 0;
        for (i = 0; i < ee; i++) {
            int x = Integer.parseInt(iS[(i + 3) + i]);
            int y = Integer.parseInt(iS[(i + 4) + i]);
            boolean[] zArr = this.map[x];
            this.map[y][x] = true;
            zArr[y] = true;
            int[] iArr = this.deg;
            iArr[x] = iArr[x] + 1;
            iArr = this.deg;
            iArr[y] = iArr[y] + 1;
            qq += (x + y) + this.deg[x];
        }
        this.random = new Random((long) qq);
    }

    public void setDotPosition(int id, int X, int Y) {
        this.dotX[id] = ((float) X) * 1.0f;
        this.dotY[id] = ((float) Y) * 1.0f;
    }

    public void shuffleDots(int X1, int Y1, int X2, int Y2) {
        this.width = X2 - X1;
        this.height = Y2 - Y1;
        this.startx = X1;
        this.starty = Y1;
        int best = ((this.size * this.size) * this.size) * this.size;
        float[] bX = null;
        float[] bY = null;
        for (int qq = 1; qq <= 10; qq++) {
            int i;
            this.dotX = new float[this.size];
            this.dotY = new float[this.size];
            for (i = 0; i < this.size; i++) {
                this.dotX[i] = ((float) this.random.nextInt(this.width)) * 1.0f;
                this.dotY[i] = ((float) this.random.nextInt(this.height)) * 1.0f;
            }
            int nowcalc = calcIntersection();
            if (nowcalc < best) {
                best = nowcalc;
                bX = this.dotX;
                bY = this.dotY;
            }
        }
        this.dotX = bX;
        this.dotY = bY;
        this.located = true;
        for (i = 2000; i >= 1000; i--) {
            triggerForce(this.width, (((float) i) * 1.0f) / 100.0f);
        }
        scaleinto(X2 - X1, Y2 - Y1, X1, Y1);
        this.located = true;
    }

    public float triggerForce(int bestlen, float f) {
        return locate(bestlen / 5, (float) (bestlen * bestlen), 0.02f, f);
    }

    private pair_float vector_line_to_point(int a, int b, int c) {
        float abx = this.dotX[b] - this.dotX[a];
        float aby = this.dotY[b] - this.dotY[a];
        float k = ((abx * (this.dotX[c] - this.dotX[a])) + (aby * (this.dotY[c] - this.dotY[a]))) / ((abx * abx) + (aby * aby));
        return new pair_float(this.dotX[c] - ((this.dotX[a] * k) + ((1.0f - k) * this.dotX[b])), this.dotY[c] - ((this.dotY[a] * k) + ((1.0f - k) * this.dotY[b])));
    }

    private void scaleinto(int width, int height, int STX, int STY) {
        int i;
        float x1 = 2.1474832E7f;
        float x2 = -1269028616;
        float y1 = 2.1474832E7f;
        float y2 = x2;
        for (i = 0; i < this.size; i++) {
            if (this.dotExist[i]) {
                if (this.dotX[i] < x1) {
                    x1 = this.dotX[i];
                } else if (this.dotX[i] > x2) {
                    x2 = this.dotX[i];
                }
                if (this.dotY[i] < y1) {
                    y1 = this.dotY[i];
                } else if (this.dotY[i] > y2) {
                    y2 = this.dotY[i];
                }
            }
        }
        for (i = 0; i < this.size; i++) {
            this.dotX[i] = ((float) STX) + (((this.dotX[i] - x1) * ((float) width)) / (x2 - x1));
            this.dotY[i] = ((float) STY) + (((this.dotY[i] - y1) * ((float) height)) / (y2 - y1));
        }
    }

    private float locate(int bestlength, float G, float switch_ratio, float force) {
        int i;
        float[] dx = new float[this.size];
        float[] dy = new float[this.size];
        for (i = 0; i < this.size; i++) {
            if (this.dotExist[i]) {
                for (int j = i + 1; j < this.size; j++) {
                    if (this.dotExist[j]) {
                        float ddx = this.dotX[j] - this.dotX[i];
                        float ddy = this.dotY[j] - this.dotY[i];
                        float sl = (float) Math.sqrt((((double) ddx) * ((double) ddx)) + ((double) (ddy * ddy)));
                        float iddx = ddx / sl;
                        float iddy = ddy / sl;
                        float sucks = (-G) / (sl * sl);
                        if (sl < ((float) bestlength) * 0.2f) {
                            sucks *= (((float) bestlength) * 0.2f) / sl;
                        }
                        if (this.map[i][j]) {
                            sucks = (float) (((double) sucks) + ((((double) sl) - (((double) bestlength) * 1.0d)) * ((double) switch_ratio)));
                        }
                        if (((double) sl) < 1.0d) {
                            sucks = ((float) bestlength) * 0.3f;
                            iddx = this.random.nextFloat();
                            iddy = this.random.nextFloat();
                        }
                        dx[i] = dx[i] + (iddx * sucks);
                        dy[i] = dy[i] + (iddy * sucks);
                        dx[j] = dx[j] - (iddx * sucks);
                        dy[j] = dy[j] - (iddy * sucks);
                    }
                }
            }
        }
        float tooclose = ((float) bestlength) * 0.2f;
        float minx = ((float) this.startx) + tooclose;
        float miny = ((float) this.starty) + tooclose;
        float maxx = ((float) (this.startx + this.width)) - tooclose;
        float maxy = ((float) (this.starty + this.height)) - tooclose;
        for (i = 0; i < this.size; i++) {
            if (this.dotX[i] < minx) {
                dx[i] = dx[i] + sig(minx - this.dotX[i], tooclose);
            }
            if (this.dotY[i] < miny) {
                dy[i] = dy[i] + sig(miny - this.dotY[i], tooclose);
            }
            if (this.dotX[i] > maxx) {
                dx[i] = dx[i] - sig(this.dotX[i] - maxx, tooclose);
            }
            if (this.dotY[i] > maxy) {
                dy[i] = dy[i] - sig(this.dotY[i] - maxy, tooclose);
            }
        }
        force *= 0.1f;
        for (i = 0; i < this.size; i++) {
            dx[i] = dx[i] * force;
            dy[i] = dy[i] * force;
        }
        float ans = 0.0f;
        for (i = 0; i < this.size; i++) {
            float[] fArr = this.dotX;
            fArr[i] = fArr[i] + ((float) ((int) dx[i]));
            fArr = this.dotY;
            fArr[i] = fArr[i] + ((float) ((int) dy[i]));
            ans += Math.abs(dx[i]) + Math.abs(dy[i]);
        }
        return ans;
    }

    float sig(float x, float tooclose) {
        if (((double) x) > ((double) tooclose) * 0.98d) {
            x = tooclose * 0.98f;
        }
        return (0.4f * tooclose) / (tooclose - x);
    }

    private int calcIntersection() {
        int aa = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = i + 1; j < this.size; j++) {
                for (int k = i + 1; k < this.size; k++) {
                    for (int l = k + 1; l < this.size; l++) {
                        if (intersection(i, j, k, l)) {
                            aa++;
                        }
                    }
                }
            }
        }
        return aa;
    }

    private boolean intersection(int a, int b, int c, int d) {
        return inter(a, b, c, d) && inter(c, d, a, b);
    }

    private boolean inter(int a, int b, int c, int d) {
        return cross(a, b, c) * cross(a, b, d) < 0;
    }

    private int cross(int a, int b, int c) {
        float q = ((this.dotX[b] - this.dotX[a]) * (this.dotY[c] - this.dotY[a])) - ((this.dotX[c] - this.dotX[a]) * (this.dotY[b] - this.dotY[a]));
        if (((double) q) > 0.01d) {
            return 1;
        }
        return ((double) q) < -0.01d ? -1 : 0;
    }

    public boolean deleteDot(int id) {
        int ds2 = this.delstack;
        boolean[] zArr = this.dotExist;
        int[] iArr = this.delhistory;
        int i = this.delstack;
        this.delstack = i + 1;
        iArr[i] = id;
        zArr[id] = false;
        int qq = 1;
        int i2 = 0;
        while (i2 < this.size) {
            if (this.dotExist[i2] && this.deg[i2] == 1 && this.map[id][i2]) {
                this.deg[i2] = 0;
                this.dotExist[i2] = false;
                qq++;
                zArr = this.dotExist;
                iArr = this.delhistory;
                i = this.delstack;
                this.delstack = i + 1;
                iArr[i] = i2;
                zArr[i2] = false;
            }
            i2++;
        }
        for (i2 = ds2; i2 < this.delstack; i2++) {
            this.deltogethernumber[i2] = qq;
        }
        calcdegree();
        this.history.add("D," + String.valueOf(id) + "," + String.valueOf(new Timer().getvalue() - this.timeCreated));
        this.matchleft--;
        System.out.println(this.delstack);
        if (this.delstack != this.size) {
            return false;
        }
        savehistory();
        return true;
    }

    private boolean savehistory() {
        try {
            FileOutputStream os = this.owner.openFileOutput("S" + this.uname + "." + String.valueOf(this.level) + "." + String.valueOf(this.timeCreated) + "T" + String.valueOf(new Random().nextInt(1000)) + ".rs", 0);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
            for (int i = 0; i < this.history.size(); i++) {
                System.out.println((String) this.history.get(i));
                br.write(new StringBuilder(String.valueOf((String) this.history.get(i))).append(";").toString());
            }
            br.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void undelDot() {
        if (this.delstack != 0) {
            int qq = this.deltogethernumber[this.delstack - 1];
            for (int i = 0; i < qq; i++) {
                boolean[] zArr = this.dotExist;
                int[] iArr = this.delhistory;
                int i2 = this.delstack - 1;
                this.delstack = i2;
                zArr[iArr[i2]] = true;
            }
            calcdegree();
            this.history.add("U," + String.valueOf(new Timer().getvalue() - this.timeCreated));
            this.matchleft++;
        }
    }

    private void calcdegree() {
        int i;
        for (i = 0; i < this.size; i++) {
            this.deg[i] = 0;
        }
        i = 0;
        while (i < this.size) {
            if (this.dotExist[i]) {
                int j = 0;
                while (j < this.size) {
                    if (this.dotExist[j] && this.map[i][j]) {
                        int[] iArr = this.deg;
                        iArr[i] = iArr[i] + 1;
                    }
                    j++;
                }
            }
            i++;
        }
    }

    private int getIdByXY(int X, int Y) {
        float min = 2.14748339E9f;
        int id = -1;
        for (int i = 0; i < this.size; i++) {
            if (this.dotExist[i]) {
                float dist = ((((float) X) - this.dotX[i]) * (((float) X) - this.dotX[i])) + ((((float) Y) - this.dotY[i]) * (((float) Y) - this.dotY[i]));
                if (dist < min) {
                    id = i;
                    min = dist;
                }
            }
        }
        return id;
    }

    public int getIdByXY(int X, int Y, float distance2) {
        int id = getIdByXY(X, Y);
        return ((((float) X) - this.dotX[id]) * (((float) X) - this.dotX[id])) + ((((float) Y) - this.dotY[id]) * (((float) Y) - this.dotY[id])) <= distance2 ? id : -1;
    }

    public float getDistanc2between(int id1, int id2) {
        return ((this.dotX[id1] - this.dotX[id2]) * (this.dotX[id1] - this.dotX[id2])) + ((this.dotY[id1] - this.dotY[id2]) * (this.dotY[id1] - this.dotY[id2]));
    }
}
