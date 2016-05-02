package com.example.jinchen.diarytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar myToolbar2 = (Toolbar) findViewById(R.id.toolbar3);
        myToolbar2.setNavigationIcon(android.R.drawable.ic_menu_revert);
      //  myToolbar2.set
        myToolbar2.inflateMenu(R.menu.searchmenu);
        myToolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
