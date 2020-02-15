package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GoToActivity extends AppCompatActivity {
    int selpage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to);
        Intent i = getIntent();
        final int pages = i.getIntExtra("npages",0);
        final String doc = i.getStringExtra("doc");
        ((SeekBar) findViewById(R.id.gtsize)).setMax(pages);

        ((TextView) findViewById(R.id.gtindic)).setText("1 / "+ pages);

        ((EditText)findViewById(R.id.eselpage)).setText(1+"");

        selpage = 1;
        ((SeekBar) findViewById(R.id.gtsize)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    ((EditText)findViewById(R.id.eselpage)).setText(progress+"");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((EditText)findViewById(R.id.eselpage)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (((EditText) findViewById(R.id.eselpage)).getText().toString() == "") {
                        selpage = 1;
                    } else if ((Integer.valueOf(((EditText) findViewById(R.id.eselpage)).getText().toString())) < pages) {
                        int progress = Integer.valueOf(((EditText) findViewById(R.id.eselpage)).getText().toString());
                        selpage = progress;
                        ((TextView) findViewById(R.id.gtindic)).setText(progress + " / " + pages);
                        ((SeekBar) findViewById(R.id.gtsize)).setProgress(progress);
                    }
                }
                catch (Exception e){
                    selpage = 1;
                }
            }
        });

        final Context c = this;
        ((Button) findViewById(R.id.gtselect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSP(doc,(selpage-1)+" "+selpage);
                Intent i = new Intent(c,ViewerActivity.class);
                i.putExtra("selpage",selpage);
                i.putExtra("doc",doc);
                startActivity(i);
            }
        });

        ((Button) findViewById(R.id.gtcancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    public void setSP(String key, String value){
        SharedPreferences.Editor spe = getSharedPreferences("remembra",MODE_PRIVATE).edit();
        spe.putString(key,value);
        spe.commit();
    }

    public String getSP(String key){
        return getSharedPreferences("remembra",MODE_PRIVATE).getString(key,"");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
