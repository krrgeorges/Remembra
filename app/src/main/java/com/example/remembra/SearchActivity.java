package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class SearchActivity extends AppCompatActivity {
    String doc;
    int sp;
    int ep;
    int pages;
    PDFTextStripper stripper;
    PDDocument document;
    int currentIndex = 0;
    String vdata;
    HashSet<String> spages = new HashSet<>();
    HashSet<String> sacpages = new HashSet<>();
    Timer t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        doc = getIntent().getStringExtra("doc");

        PDFBoxResourceLoader.init(getApplicationContext());


        try {
            document = PDDocument.load(new File(doc));
            pages = document.getNumberOfPages();


            ((ProgressBar) findViewById(R.id.spb)).setMax(pages);


            stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            stripper.setShouldSeparateByBeads(true);

            currentIndex = 0;

            sp = 0;
            ep = sp+1;



        } catch (IOException e) {
            e.printStackTrace();
        }




        final Context c = this;
        ((Button) findViewById(R.id.sselect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    t.cancel();
                }
                catch (Exception e){

                }

                ((LinearLayout) findViewById(R.id.m_res)).removeAllViews();
                ((NestedScrollView) findViewById(R.id.res_container)).setVisibility(View.GONE);
                final String s_term = ((EditText) findViewById(R.id.spage)).getText().toString();
                if(s_term.replace(" ","").length() == 0){
                    finish();
                }
                else{
                    ((LinearLayout) findViewById(R.id.sloady)).setVisibility(View.VISIBLE);
                    ((Button) findViewById(R.id.sselect)).setEnabled(false);

                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                             if(ep <= pages){
                                vdata = "";

                                stripper.setStartPage(sp);
                                stripper.setEndPage(ep);

                                try {
                                    vdata = stripper.getText(document);
                                    if(vdata.toLowerCase().contains(s_term.toLowerCase()) && spages.contains((sp-1)+"") == false){
                                        spages.add(sp+"");
                                        sacpages.add(vdata);
                                        final int tsp = sp;
                                        final int tep = ep;
                                        final String tvdata = vdata;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if(((NestedScrollView) findViewById(R.id.res_container)).getVisibility() == View.GONE){
                                                    ((NestedScrollView) findViewById(R.id.res_container)).setVisibility(View.VISIBLE);
                                                }

                                                LinearLayout ll = new LinearLayout(c);
                                                LinearLayout.LayoutParams lll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                lll.setMargins(dpToPx(3),dpToPx(3),dpToPx(3),dpToPx(3));
                                                ll.setLayoutParams(lll);
                                                ll.setOrientation(LinearLayout.VERTICAL);
                                                ll.setBackground(getDrawable(R.drawable.drawer_bg));
                                                ll.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

                                                TextView tv = new TextView(c);
                                                lll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                tv.setLayoutParams(lll);
                                                tv.setTextSize(13);
                                                tv.setText("Page " + tep);
                                                tv.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Raleway-SemiBold.ttf"));
                                                ll.addView(tv);

                                                Space s = new Space(c);
                                                lll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dpToPx(5));
                                                s.setLayoutParams(lll);
                                                ll.addView(s);



                                                tv = new TextView(c);
                                                lll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                                tv.setLayoutParams(lll);
                                                tv.setTextSize(16);
                                                int s_index = 0;
                                                int e_index = 0;
                                                if(tvdata.indexOf(s_term)-200 <= 0){
                                                    s_index = 0;
                                                }
                                                else{
                                                    s_index = tvdata.indexOf(s_term)-200;
                                                }
                                                if(tvdata.indexOf(s_term)+200 >= tvdata.length()-1){
                                                    e_index = tvdata.length();
                                                }
                                                else{
                                                    e_index = tvdata.indexOf(s_term)+200;
                                                }
                                                tv.setText("..."+tvdata.substring(s_index,e_index).replace("\n"," ")+"...");
                                                tv.setTypeface(TypefaceUtils.load(getAssets(),"fonts/Raleway-SemiBold.ttf"));
                                                ll.addView(tv);
                                                Toast.makeText(getApplicationContext(),tsp+" "+tep,Toast.LENGTH_LONG).show();
                                                ll.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        setSP(doc,(tep-1)+" "+tep);
                                                        Intent i = new Intent(c,ViewerActivity.class);
                                                        i.putExtra("selpage",tsp);
                                                        i.putExtra("doc",doc);
                                                        t.cancel();
                                                        startActivity(i);
                                                    }
                                                });

                                                ((LinearLayout) findViewById(R.id.m_res)).addView(ll);

                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    onBackPressed();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ProgressBar) findViewById(R.id.spb)).setProgress(ep);
                                        ((TextView) findViewById(R.id.sing)).setText("Searching...("+ep+"/"+pages+" pages)");
                                    }
                                });



                            }
                            else{
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         ((LinearLayout) findViewById(R.id.sloady)).setVisibility(View.GONE);
                                     }
                                 });


                                 t.cancel();

                             }

                            sp += 1;
                            ep += 1;
                        }
                    };

                    t = new Timer();
                    t.scheduleAtFixedRate(tt,1000,1000);

                }
            }
        });

        ((Button) findViewById(R.id.scancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spages.size() > 0){
                    SharedPreferences.Editor e  =  c.getSharedPreferences("remembra",MODE_PRIVATE).edit();
                    e.putStringSet("sPNOS",spages);
                    e.putStringSet("sPS",sacpages);
                    e.commit();
                }
                finish();
            }
        });
    }

    public void setSP(String key,String value){
        SharedPreferences.Editor spe = getSharedPreferences("remembra",MODE_PRIVATE).edit();
        spe.putString(key,value);
        spe.commit();
    }

    public String getSP(String key){
        return getSharedPreferences("remembra",MODE_PRIVATE).getString(key,"");
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
