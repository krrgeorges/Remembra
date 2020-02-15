package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConvertOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_options);
        final Context c = this;
        ((Button) findViewById(R.id.cfiles)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,FileChooser.class);
                i.putExtra("fsel",1);
                startActivityForResult(i,1112);
            }
        });

        ((Button) findViewById(R.id.ctext)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,ConvertTextActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1112){
            if(resultCode == RESULT_OK){
                ArrayList<String> sfiles = data.getStringArrayListExtra("sfiles");
                if(sfiles.size() > 0){
                    ArrayList<String> docs = sfiles;
                    Intent i  = new Intent(this,ConvertFilesActivity.class);
                    i.putStringArrayListExtra("docs",docs);
                    startActivity(i);
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
