package com.example.remembra;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSP("font") == ""){
            setSP("font","sf");
        }

        if(getSP("color") == ""){
            setSP("color","#FDFEFE");
        }

        if(getSP("size") == ""){
            setSP("size","14");
        }


        TextView textView = (TextView) findViewById(R.id.app_head);

        TextPaint paint = textView.getPaint();
        float width = paint.measureText("Remembra");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#00C9FF"),
                        Color.parseColor("#92FE9D")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);


        final Context c = this;
        ((Button) findViewById(R.id.getty)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,FileChooser.class);
                i.putExtra("fsel",0);
                startActivityForResult(i,1111);
            }
        });

        ((Button) findViewById(R.id.converty)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,ConvertOptionsActivity.class);

                startActivity(i);
            }
        });

        ((ImageButton) findViewById(R.id.settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,SettingsActivity.class);
                startActivity(i);
            }
        });

        ((ImageButton) findViewById(R.id.about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,AboutActivity.class);
                startActivity(i);
            }
        });

    }

    public void loadRecords(){
        LinkedHashMap<String,Integer> data = new DBWrapper(this).getRecents();
        if(data.size() > 0){
            String lastOpened = data.keySet().toArray()[0].toString();

            ((RelativeLayout) findViewById(R.id.last)).removeAllViews();
            ((LinearLayout) findViewById(R.id.recents)).removeAllViews();
            File lod = new File(lastOpened);
            LinearLayout view = getCardView(lod.getName(),lod.getParent(),lastOpened);
            ((RelativeLayout) findViewById(R.id.last)).addView(view);

            ArrayList<String> l = new ArrayList<String>(data.keySet());
            for (int i = l.size()-1; i >= 0; i--) {
                String file = l.get(i);
                lod = new File(file);
                view = getCardView(lod.getName(),lod.getParent(),file);
                ((LinearLayout) findViewById(R.id.recents)).addView(view);
                Space s = new Space(this);
                LinearLayout.LayoutParams sllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dpToPx(5));
                s.setLayoutParams(sllp);
                ((LinearLayout) findViewById(R.id.recents)).addView(s);
            }
        }
    }

    public LinearLayout getCardView(String fileName, String parentPath, final String fullPath){
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lllp);
        ll.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        ll.setBackground(getDrawable(R.drawable.filerec_bg));
        ll.setOrientation(LinearLayout.VERTICAL);
        final Context c = this;
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,ViewerActivity.class);
                i.putExtra("doc",fullPath);
                startActivity(i);
            }
        });
        TextView tv = new TextView(this);
        tv.setLayoutParams(lllp);
        tv.setTypeface(Typeface.createFromAsset(getResources().getAssets(),"fonts/Raleway-Bold.ttf"));
        tv.setTextSize(18);
        tv.setTextColor(getResources().getColor(R.color.black));
        tv.setText(fileName);
        ll.addView(tv);

        Space s = new Space(this);
        LinearLayout.LayoutParams sllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dpToPx(10));
        s.setLayoutParams(sllp);
        ll.addView(s);


        tv = new TextView(this);
        tv.setLayoutParams(lllp);
        tv.setTypeface(Typeface.createFromAsset(getResources().getAssets(),"fonts/Raleway-SemiBold.ttf"));
        tv.setTextColor(getResources().getColor(R.color.black));
        tv.setText(parentPath);
        ll.addView(tv);

        return ll;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1111){
            if(resultCode == RESULT_OK){
                ArrayList<String> sfiles = data.getStringArrayListExtra("sfiles");
                if(sfiles.size() > 0){
                    String doc = sfiles.get(0);
                    Intent i = new Intent(this,ViewerActivity.class);
                    i.putExtra("doc",doc);
                    startActivity(i);


                }
            }
        }

    }

    public void setSP(String key,String value){
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
