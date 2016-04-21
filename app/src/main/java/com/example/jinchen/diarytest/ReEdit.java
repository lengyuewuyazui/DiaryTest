package com.example.jinchen.diarytest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;



import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ReEdit extends AppCompatActivity {
    private DiaryDB diaryDB;
    private SQLiteDatabase dbwriter;

    private IWXAPI api;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_edit);

        api = WXAPIFactory.createWXAPI(this, "wx7b67f9f371b623b1");
        api.registerApp("wx7b67f9f371b623b1");

        final TextView textView1 = (TextView) findViewById(R.id.biaoti1);//得到TextView控件对象
        final TextView textView2 = (TextView) findViewById(R.id.biaoti3);//得到TextView控件对象

        Typeface tv = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        textView1.setTypeface(tv);//使用字体
        textView1.setText(getIntent().getStringExtra("diarydb.DATE"));
        textView2.setTypeface(tv);//使用字体
        textView2.setText(getIntent().getStringExtra("diarydb.WEATHER"));

        final EditText editText = (EditText) findViewById(R.id.ettext1);//得到EditText控件对象
        Typeface et = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        editText.setTypeface(et);//使用字体;


        diaryDB = new DiaryDB(this);
        dbwriter = diaryDB.getWritableDatabase();

        editText.setText(getIntent().getStringExtra("diarydb.CONTENT"));
        findViewById(R.id.DeleteButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(EditDiary.this,MainActivity.class));
                finish();
            }
        });
        findViewById(R.id.SaveButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                //   values.put(diaryDB.TITLE, textView.getText().toString());
                values.put(diaryDB.CONTENT, editText.getText().toString());
                int a = getIntent().getIntExtra("diarydb.ID", 0);
                dbwriter.update(diaryDB.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(a)});
                startActivity(new Intent(ReEdit.this, MainActivity.class));
            }
        });
        findViewById(R.id.ShareButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
                AlertDialog.Builder dialog = new AlertDialog.Builder(ReEdit.this);
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap, imagePath);
                dialog.setTitle("分享");
                dialog.setMessage("想要分享给朋友看吗？.");
                dialog.setCancelable(false);
                dialog.setPositiveButton("分享到微信朋友圈", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharetowechat(imagePath);
                        finish();


                    }
                });
                dialog.setNegativeButton("分享到微博", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharetoweibo();
                        finish();

                    }
                });
                dialog.show();



            }
        });


    }
    private void sharetoweibo() {
        finish();



    }
    private  void sharetowechat(File imagePath)
    {
        Bitmap bmp = BitmapFactory.decodeFile(String.valueOf(imagePath));
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
       // Bitmap thumbBmp=BitmapFactory.decodeResource(getResources(),R.drawable.sharebutton);

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 90, 160, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction ="image";

        req.message = msg;
        req.scene =  SendMessageToWX.Req.WXSceneTimeline ;
        api.sendReq(req);
    }
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap,File imagePath) {


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

}



