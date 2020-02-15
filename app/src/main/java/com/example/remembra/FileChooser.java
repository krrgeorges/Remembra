package com.example.remembra;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FileChooser extends AppCompatActivity {
    String currentDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    ArrayList<String> selectedFiles = new ArrayList();
    ArrayList<String> supportedTypes = new ArrayList(){{add("pdf");add("doc");add("docx");}};
    int fsel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);



        Intent i = getIntent();
        fsel = i.getIntExtra("fsel",0);
        ((TextView)findViewById(R.id.fheader)).setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Raleway-Bold.ttf"));
        if(fsel == 0){
            ((TextView)findViewById(R.id.fheader)).setText("Choose File");
        }
        else{
            ((TextView)findViewById(R.id.fheader)).setText("Choose Files");
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1234);
        }
        else{
            showDirStuff(currentDir);
        }

        final Context c = this;
        ((Button) findViewById(R.id.fselect)).setEnabled(false);
        ((Button) findViewById(R.id.fcancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        ((Button) findViewById(R.id.fselect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent();
//                i.putStringArrayListExtra("sfiles",selectedFiles);
//                setResult(RESULT_OK,i);
//                finish();
                Intent i  = new Intent(c,ConvertFilesActivity.class);
                i.putStringArrayListExtra("docs",selectedFiles);
                startActivity(i);
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1234){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showDirStuff(currentDir);
            }
            else{
                this.onBackPressed();
            }
        }
    }

    public void showDirStuff(String path) {
        try {
            final String tempPath = path;
            File[] files = new File(path).listFiles();
            Arrays.sort(files);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 10, 0, 10);
            TextView tv = new TextView(this);
            tv.setText("...");
            tv.setTextSize(16);
            tv.setLayoutParams(lp);
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) findViewById(R.id.dirview)).removeAllViews();
                    showDirStuff(new File(tempPath).getParent());
                }
            });
            tv.setFocusableInTouchMode(false);
            ((LinearLayout) findViewById(R.id.dirview)).addView(tv);

            for (File f : files) {
                final File temp = f;
                if (f.isDirectory()) {
                    tv = new TextView(this);
                    tv.setText("  "+f.getName());
                    tv.setLayoutParams(lp);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_folder_white_24dp,0,0,0);
                    tv.setFocusableInTouchMode(false);
                    tv.setClickable(true);
                    tv.setTextSize(16);
                    tv.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Raleway-SemiBold.ttf"));
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) findViewById(R.id.dirview)).removeAllViews();
                            showDirStuff(temp.getAbsolutePath());
                        }
                    });
                    ((LinearLayout) findViewById(R.id.dirview)).addView(tv);
                } else {
                    if (supportedTypes.contains(f.getName().substring(f.getName().lastIndexOf('.') + 1, f.getName().length()))) {
                        CheckBox cb = new CheckBox(this);
                        final Context c = this;
                        cb.setTextColor(Color.parseColor("#FFFFFF"));
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    selectedFiles.add(temp.getAbsolutePath());
                                    ((Button) findViewById(R.id.fselect)).setEnabled(true);
                                    if(fsel == 0){
                                        String doc = selectedFiles.get(0);
                                        Intent i = new Intent(c,ViewerActivity.class);
                                        i.putExtra("doc",doc);
                                        startActivity(i);
                                    }
                                } else {
                                    if (selectedFiles.contains(temp.getAbsolutePath())) {
                                        selectedFiles.remove(temp.getAbsolutePath());
                                    }
                                    if(selectedFiles.size() == 0){
                                        ((Button) findViewById(R.id.fselect)).setEnabled(false);
                                    }
                                }
                            }
                        });
                        cb.setLayoutParams(lp);
                        cb.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/Raleway-SemiBold.ttf"));
                        cb.setText(f.getName());
                        cb.setTextSize(16);
                        ((LinearLayout) findViewById(R.id.dirview)).addView(cb);
                    }
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
