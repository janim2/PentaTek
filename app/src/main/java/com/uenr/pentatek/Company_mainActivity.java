package com.uenr.pentatek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Company_mainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_main);

        getSupportActionBar().setTitle("PentaTek | Company");
    }
}
