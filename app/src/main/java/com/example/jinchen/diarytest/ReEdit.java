package com.example.jinchen.diarytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReEdit extends AppCompatActivity {
    private DiaryDB diaryDB;
    private SQLiteDatabase dbwriter;
    private Uri imageUri;
    private IWXAPI api;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int SELECT_PHOTO = 3 ;
    private ImageView picture;
    private FloatingActionButton caremabutton;
    private FloatingActionButton gallerybutton;
    private FloatingActionButton addbutton;
    private FloatingActionButton undobutton;
    private TextView textView1;
    private TextView textView2;
    private EditText editText;


    private LocationManager locationManager;
    private String provider;
    public static final int SHOW_LOCATION = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_edit);

        Toolbar myToolbar1 = (Toolbar) findViewById(R.id.toolbar2);
        myToolbar1.setNavigationIcon(android.R.drawable.ic_menu_revert);
        myToolbar1.setLogo(R.mipmap.icon_small);
        myToolbar1.setTitle("心晴日记");
        myToolbar1.inflateMenu(R.menu.editmenu);
        myToolbar1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        myToolbar1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap, imagePath);
                PopupMenu popupMenu = new PopupMenu(ReEdit.this, findViewById(R.id.send));
                Menu menu = popupMenu.getMenu();

                // 通过代码添加菜单项
                menu.add(Menu.NONE, Menu.FIRST + 0, 0, "分享到微信朋友圈");
                menu.add(Menu.NONE, Menu.FIRST + 1, 1, "分享到微博");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case Menu.FIRST + 0:
                                sharetowechat(imagePath);
                                break;
                            case Menu.FIRST + 1:
                                sharetoweibo();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

//                AlertDialog.Builder dialog = new AlertDialog.Builder(ReEdit.this);
//                dialog.setTitle("分享");
//                dialog.setMessage("想要分享给朋友看吗？.");
//                dialog.setCancelable(false);
//                dialog.setPositiveButton("分享到微信朋友圈", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sharetowechat(imagePath);
//                        finish();
//
//
//                    }
//                });
//                dialog.setNegativeButton("分享到微博", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sharetoweibo();
//                        finish();
//
//                    }
//                });
//                dialog.show();
//

                return true;
            }
        });



        api = WXAPIFactory.createWXAPI(this, "wx7b67f9f371b623b1");
        api.registerApp("wx7b67f9f371b623b1");
        diaryDB = new DiaryDB(this);
        dbwriter = diaryDB.getWritableDatabase();
        textView1 = (TextView) findViewById(R.id.biaoti1);//得到TextView控件对象
        textView2 = (TextView) findViewById(R.id.biaoti3);//得到TextView控件对象
        editText = (EditText) findViewById(R.id.ettext1);//得到EditText控件对象
        Typeface tv = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        textView1.setTypeface(tv);//使用字体

        textView2.setTypeface(tv);//使用字体


        Typeface et = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        editText.setTypeface(et);//使用字体;

        picture=(ImageView)findViewById(R.id.image);
        int flag=getIntent().getIntExtra("flag",100);
        if(flag==1){
            ReEdit();
        }
        else if (flag==0){
            InitLoctionWeather();
        }
        else
        finish();






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

       caremabutton =(FloatingActionButton)findViewById(R.id.CameraButton1);
       gallerybutton =(FloatingActionButton)findViewById(R.id.GalleryButton1);
       addbutton =(FloatingActionButton)findViewById(R.id.AddButton1);
       undobutton =(FloatingActionButton)findViewById(R.id.UndoButton1);

        findViewById(R.id.AddButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caremabutton.show();
                gallerybutton.show();
                addbutton.hide();
                undobutton.show();

            }

        });
        findViewById(R.id.UndoButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caremabutton.hide();
                gallerybutton.hide();
                addbutton.show();
                undobutton.hide();

            }

        });

        findViewById(R.id.CameraButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);


            }
        });
        findViewById(R.id.GalleryButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage2 = new File(Environment.getExternalStorageDirectory(), "tempImage2.jpg");
                imageUri = Uri.fromFile(outputImage2);
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent2.setDataAndType(imageUri, "image/*");
                startActivityForResult(intent2, SELECT_PHOTO);

            }
        });


    }
    private void ReEdit(){
        textView1.setText(getIntent().getStringExtra("diarydb.DATE"));
        textView2.setText(getIntent().getStringExtra("diarydb.WEATHER"));
        editText.setText(getIntent().getStringExtra("diarydb.CONTENT"));

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("crop", true);
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序

                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream (getContentResolver()
                                .openInputStream(imageUri));
                        picture.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }


    private void InitLoctionWeather(){
        diaryDB=new DiaryDB(this);
        dbwriter=diaryDB.getWritableDatabase();
        findViewById(R.id.SaveButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(diaryDB.DATE, textView1.getText().toString());
                values.put(diaryDB.WEATHER, textView2.getText().toString());
                values.put(diaryDB.CONTENT, editText.getText().toString());

                dbwriter.insert(diaryDB.TABLE_NAME, null, values);
                // finish();


                startActivity(new Intent(ReEdit.this, MainActivity.class));

            }
        });
        findViewById(R.id.DeleteButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(EditDiary.this,MainActivity.class));
                finish();
            }
        });



        locationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE);
// 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
// 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
// 显示当前设备的位置信息
            showLocation(location);
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
    }
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
// 关闭程序时将监听器移除
            locationManager.removeUpdates(locationListener);
        }
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            // 更新当前设备的位置信息
            showLocation(location);
        }



    };
    private Handler handler1 = new Handler()
    {
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case SHOW_LOCATION:
                    String currentPosition = (String) msg.obj;
                    textView1.setText(getTime());
                    textView2.setText(currentPosition);
                    break;
                default:
                    break;
            }
        }
    };
    private void showLocation(Location location)
    {
        String url1 = "https://api.thinkpage.cn/v3/weather/now.json?key=mtxenlmmlwjavepj&location=";
        String url2 = String.valueOf(location.getLatitude());
        String url3 = String.valueOf(location.getLongitude());
        String url4 = "&language=zh-Hans&unit=c";
        String url = url1 + url2 + ":" + url3 + url4;

        LotionWeather.sendHttpRequest(url, new HttpCallbackListener() {


            @Override
            public void onFinish(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray resultArray = jsonObject.getJSONArray("results");
                    JSONObject subObject = resultArray.getJSONObject(0);
                    JSONObject subObject1 = subObject.getJSONObject("location");
                    String address11 = subObject1.getString("path");
                    String address1 = address11.substring(3, address11.length() - 3);

                    JSONObject subObject2 = subObject.getJSONObject("now");
                    String address2 = subObject2.getString("text");
                    String address3 = subObject2.getString("temperature");
                    String address = address1 + " " + address2 + " " + address3 + "℃";

                    Message message = new Message();
                    message.what = SHOW_LOCATION;
                    message.obj = address;
                    //message.obj = address;
                    handler1.sendMessage(message);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Exception e) {


            }
        });

    }
    private String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }


}



