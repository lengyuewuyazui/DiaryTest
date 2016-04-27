package com.example.jinchen.diarytest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EditDiary extends Activity {
    private TextView timeTextView;
    private TextView positionTextView;
    private EditText editText;
    private LocationManager locationManager;
    private String provider;
    public static final int SHOW_LOCATION = 0;
    private DiaryDB diaryDB;
    private SQLiteDatabase dbwriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);
        timeTextView = (TextView) findViewById(R.id.biaoti);//得到TextView控件对象
        positionTextView = (TextView) findViewById(R.id.biaoti2);//得到TextView控件对象
        Typeface tv = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        timeTextView.setTypeface(tv);
        positionTextView.setTypeface(tv);//使用字体

        editText = (EditText) findViewById(R.id.ettext);//得到EditText控件对象
        Typeface et = Typeface.createFromAsset(getAssets(), "fonts/newfont.ttf"); //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        editText.setTypeface(et);//使用字体;
//        Toolbar myToolbar1 = (Toolbar) findViewById(R.id.toolbar2);
//        myToolbar1.setNavigationIcon(android.R.drawable.ic_menu_revert);
//        myToolbar1.setLogo(R.mipmap.icon_small);
//        myToolbar1.setTitle("心晴日记");
//        myToolbar1.inflateMenu(R.menu.editmenu);
//        myToolbar1.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        int flag = getIntent().getIntExtra("flag",100);
        if(flag==0) {
            InitLoctionWeather();
        }

    }
    private void InitLoctionWeather(){
        diaryDB=new DiaryDB(this);
        dbwriter=diaryDB.getWritableDatabase();
        findViewById(R.id.SaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(diaryDB.DATE, timeTextView.getText().toString());
                values.put(diaryDB.WEATHER, positionTextView.getText().toString());
                values.put(diaryDB.CONTENT, editText.getText().toString());

                dbwriter.insert(diaryDB.TABLE_NAME, null, values);
                // finish();


                startActivity(new Intent(EditDiary.this, MainActivity.class));

            }
        });
        findViewById(R.id.DeleteButton).setOnClickListener(new View.OnClickListener() {
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
                    timeTextView.setText(getTime());
                    positionTextView.setText(currentPosition);
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

