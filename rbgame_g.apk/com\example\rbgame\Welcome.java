package com.example.rbgame;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

public class Welcome extends Activity {
    private Button buttonUpload;
    private String isOK = null;
    private EditText mTextID;
    private EditText mTextIPport;
    private EditText mTextSchool;
    MyHandler mhandler;
    private myHTTPposter mhttpposter;
    ProgressDialog progressDialog;
    private int toUploadCount;
    private String uploadstr;

    private class MyHandler extends Handler {
        private MyHandler() {
        }

        public void dispatchMessage(Message msg) {
            Welcome.this.progressDialog.dismiss();
            Welcome.this.progressDialog = null;
            System.out.println(msg.what);
            System.out.println(Welcome.this.isOK);
            Toast.makeText(Welcome.this, Welcome.this.isOK, 1).show();
            if (Welcome.this.isOK.equals("OK") && msg.what == 200) {
                Welcome.this.isOK = null;
                new Builder(Welcome.this).setTitle("上传成功").setPositiveButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                Welcome.this.buttonUpload.setEnabled(false);
                try {
                    FileOutputStream os = Welcome.this.openFileOutput("U" + new SimpleDateFormat("MMddhhmmss").format(new Date()), 0);
                    BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
                    br.write(Welcome.this.uploadstr);
                    br.close();
                    os.close();
                    String[] flist = Welcome.this.fileList();
                    Welcome.this.uploadstr = new String();
                    for (int i = 0; i < flist.length; i++) {
                        if (flist[i].startsWith("S")) {
                            Welcome.this.deleteFile(flist[i]);
                        }
                    }
                    return;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            if (msg.what >= 0) {
                Toast.makeText(Welcome.this, "Fail to upload", 0).show();
                Welcome.this.isOK = null;
            }
            if (msg.what == -1) {
                Toast.makeText(Welcome.this, "Fail to upload:ClientProtocolException", 0).show();
            } else if (msg.what == -2) {
                Toast.makeText(Welcome.this, "Fail to upload:Please check the network", 0).show();
            } else if (msg.what == -3) {
                Toast.makeText(Welcome.this, "Fail to upload:General Exception", 0).show();
            }
        }
    }

    private class myHTTPposter implements Runnable {
        private myHTTPposter() {
        }

        public void run() {
            Welcome.this.isOK = new String();
            String server = Welcome.this.mTextIPport.getText().toString();
            if (server.equals("AUTO")) {
                server = "218.241.236.109:8080";
            }
            HttpPost httpRequest = new HttpPost("http://" + server + "/save.php");
            ArrayList<NameValuePair> paras = new ArrayList();
            paras.add(new BasicNameValuePair("name", new StringBuilder(String.valueOf(new SimpleDateFormat("MMddHHmmss").format(new Date()))).append(String.valueOf(new Random().nextFloat())).toString()));
            paras.add(new BasicNameValuePair("text", Welcome.this.uploadstr));
            try {
                httpRequest.setEntity(new UrlEncodedFormEntity(paras, "UTF-8"));
                DefaultHttpClient dfh = new DefaultHttpClient(new BasicHttpParams());
                dfh.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
                dfh.getParams().setParameter("http.socket.timeout", Integer.valueOf(11000));
                HttpResponse response = dfh.execute(httpRequest);
                Welcome.this.isOK = new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
                Welcome.this.mhandler.obtainMessage(response.getStatusLine().getStatusCode()).sendToTarget();
            } catch (ClientProtocolException e) {
                Welcome.this.mhandler.obtainMessage(-1).sendToTarget();
            } catch (IOException e2) {
                Welcome.this.mhandler.obtainMessage(-2).sendToTarget();
            } catch (Exception e3) {
                Welcome.this.mhandler.obtainMessage(-3).sendToTarget();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.mhandler = new MyHandler();
        this.mTextSchool = (EditText) findViewById(R.id.editschool);
        this.mTextID = (EditText) findViewById(R.id.textID);
        this.mTextIPport = (EditText) findViewById(R.id.textIP);
        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        Button buttonHelp = (Button) findViewById(R.id.buttonHelp);
        this.buttonUpload = (Button) findViewById(R.id.buttonUpload);
        this.toUploadCount = countfile();
        if (this.toUploadCount != 0) {
            Toast.makeText(this, "找到" + String.valueOf(this.toUploadCount) + "条记录,请上传", 0).show();
        }
        this.buttonUpload.setEnabled(this.toUploadCount != 0);
        try {
            FileInputStream is = openFileInput("login.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            this.mTextSchool.setText(br.readLine());
            this.mTextID.setText(br.readLine());
            this.mTextIPport.setText(br.readLine());
            is.close();
        } catch (IOException e) {
            this.mTextSchool.setText("BUAA");
            this.mTextID.setText("TEST");
            this.mTextIPport.setText("AUTO");
        }
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (Welcome.this.mTextID.getText().toString().contains(".") || Welcome.this.mTextSchool.getText().toString().contains(".")) {
                    new Builder(Welcome.this).setTitle("ERROR").setMessage("Invalid school/ID").setPositiveButton("OK", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(Welcome.this, Choose.class);
                Bundle bundle = new Bundle();
                bundle.putString("school", Welcome.this.mTextSchool.getText().toString());
                bundle.putString("ID", Welcome.this.mTextID.getText().toString());
                if (Welcome.this.mTextIPport.getText().toString().equals("AUTO")) {
                    bundle.putString("IPport", "218.241.236.109:8080");
                } else {
                    bundle.putString("IPport", Welcome.this.mTextIPport.getText().toString());
                }
                Welcome.this.save_instance();
                intent.putExtras(bundle);
                Welcome.this.startActivity(intent);
                Welcome.this.finish();
            }
        });
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(Welcome.this, Help.class);
                Welcome.this.startActivity(intent);
            }
        });
        this.buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Welcome.this.save_instance();
                Welcome.this.startUpload();
            }
        });
    }

    private void startUpload() {
        int i;
        String[] flist = fileList();
        for (String println : flist) {
            System.out.println(println);
        }
        this.uploadstr = new String();
        for (i = 0; i < flist.length; i++) {
            if (flist[i].startsWith("S")) {
                try {
                    this.uploadstr += new BufferedReader(new InputStreamReader(openFileInput(flist[i]))).readLine() + "\n";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        if (this.uploadstr.length() <= 0) {
            Toast.makeText(this, "未发现需要上传的记录", 1).show();
            return;
        }
        new Thread(new myHTTPposter()).start();
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setMessage("Uploading .... ");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    protected void save_instance() {
        try {
            FileOutputStream os = openFileOutput("login.txt", 0);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os));
            br.write(new StringBuilder(String.valueOf(this.mTextSchool.getText().toString())).append("\n").toString());
            br.write(new StringBuilder(String.valueOf(this.mTextID.getText().toString())).append("\n").toString());
            br.write(new StringBuilder(String.valueOf(this.mTextIPport.getText().toString())).append("\n").toString());
            br.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkAndUpLoad() {
        int i;
        String[] flist = fileList();
        for (String println : flist) {
            System.out.println(println);
        }
        for (i = 0; i < flist.length; i++) {
            try {
                FileInputStream is = openFileInput(flist[i]);
                System.out.println(flist[i] + "::");
                System.out.println(new BufferedReader(new InputStreamReader(is)).readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public int countfile() {
        int ans = 0;
        String[] flist = fileList();
        for (String startsWith : flist) {
            if (startsWith.startsWith("S")) {
                ans++;
            }
        }
        return ans;
    }
}
