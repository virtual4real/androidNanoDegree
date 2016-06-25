package com.virtual4real.builditbiggershowjoke;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ShowJokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_joke);


        Intent intent = getIntent();
        String value = intent.getStringExtra("joke");

        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }
}
