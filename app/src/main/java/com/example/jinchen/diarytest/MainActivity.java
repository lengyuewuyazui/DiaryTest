package com.example.jinchen.diarytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import android.widget.CursorAdapter;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class  MainActivity extends AppCompatActivity {
    private DiaryDB diarydb;
    private SQLiteDatabase dbReader;
    private Cursor cursor;
   // private String[] data={"你","我","他","她","它"};
//   @Override
//   public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.actionbartest, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
  //      setContentView(R.layout.activity_main);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
//        myToolbar.setLogo(R.mipmap.icon_small);
//        setSupportActionBar(myToolbar);
        initView();
        findViewById(R.id.NewDiaryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,EditDiary.class));
            }
        });
    }
    public void initView()
    {
//        ArrayAdapter<String>adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,data);
//        ListView listView=(ListView)findViewById(R.id.listView);
//        listView.setAdapter(adapter);
        diarydb=new DiaryDB(this);
        dbReader=diarydb.getReadableDatabase();
        cursor = dbReader.query(diarydb.TABLE_NAME, null, null, null, null, null, null);
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        final ListView listView=(ListView)findViewById(R.id.listView);

       final SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this,R.layout.listview_layout , cursor,
                new String[] {DiaryDB.DATE},
                new int[] { R.id.test_id},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent i = new Intent(MainActivity.this, ReEdit.class);
                i.putExtra("diarydb.ID", cursor.getInt(cursor.getColumnIndex(diarydb.ID)));
                i.putExtra("diarydb.DATE", cursor.getString(cursor.getColumnIndex(diarydb.DATE)));
                i.putExtra("diarydb.WEATHER", cursor.getString(cursor.getColumnIndex(diarydb.WEATHER)));
                i.putExtra("diarydb.CONTENT", cursor.getString(cursor.getColumnIndex(diarydb.CONTENT)));
                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                //Toast.makeText(getApplicationContext(), "你想删除吗？", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder (MainActivity.this);
                dialog.setTitle("提醒");
                dialog.setMessage("你确定要删除么？.");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbReader.delete(diarydb.TABLE_NAME, "_id=" + id, null);

                        //adapter.notifyDataSetChanged();
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));


                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

                return false;
            }
        });


    }


}
