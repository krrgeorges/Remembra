package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CTViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctviewer);

        ((LinearLayout) findViewById(R.id.c_seter)).setVisibility(View.INVISIBLE);

        ((WebView) findViewById(R.id.c_viewer)).getSettings().setJavaScriptEnabled(true);

        if(getSP("font") == "od"){
            ((Button) findViewById(R.id.cod)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.cod)).setTextColor(Color.parseColor("#FFFFFF"));
        }
        else if(getSP("font") == "sf"){
            ((Button) findViewById(R.id.csf)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.csf)).setTextColor(Color.parseColor("#FFFFFF"));
        }


        ((Button) findViewById(R.id.cod)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="od"){
                    setSP("font","od");
                    ((WebView) findViewById(R.id.c_viewer)).evaluateJavascript("javascript:(function(){"
                            + "document.getElementsByTagName(\"body\")[0].style.fontFamily = \"OpenDyslexic\";"+
                            "})()",null);
                    ((Button) findViewById(R.id.cod)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.cod)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.csf)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.csf)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ((Button) findViewById(R.id.csf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="sf"){
                    setSP("font","sf");
                    ((WebView) findViewById(R.id.c_viewer)).evaluateJavascript("javascript:(function(){"
                            + "document.getElementsByTagName(\"body\")[0].style.fontFamily = \"SansForgetica\";"+
                            "})()",null);
                    ((Button) findViewById(R.id.csf)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.csf)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.cod)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.cod)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        final Button[] colors = {((Button) findViewById(R.id.cc1)),((Button) findViewById(R.id.cc2)),((Button) findViewById(R.id.cc3)),((Button) findViewById(R.id.cc4)),((Button) findViewById(R.id.cc5)),((Button) findViewById(R.id.cc6))};



        for(Button b:colors){
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor(b.getTag().toString()));
            if(b.getTag().toString().equals(getSP("color"))){
                gd.setStroke(10,Color.parseColor("#3498DB"));
            }
            gd.setCornerRadius(10);
            b.setBackground(gd);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSP("color",v.getTag().toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(Color.parseColor(getSP("color")));
                    }
                    String font_color = "";
                    switch (v.getTag().toString()){
                        case "#FDFEFE":font_color = "#000000";break;
                        case "#17202A":font_color = "#FFFFFF";break;
                        case "#2C3E50":font_color = "#FFFFFF";break;
                        case "#424949":font_color = "#FFFFFF";break;
                        case "#c99868":font_color = "#000000";break;
                        case "#34495E":font_color = "#FFFFFF";break;
                    }
                    ((WebView) findViewById(R.id.c_viewer)).evaluateJavascript("javascript:(function(){"
                            + "document.getElementsByTagName(\"body\")[0].style.backgroundColor = \""+v.getTag().toString()+"\";"+
                            "document.getElementsByTagName(\"body\")[0].style.color = \""+font_color+"\";"+
                            "})();",null);
                    for(Button c:colors){
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(Color.parseColor(c.getTag().toString()));
                        if(c.getTag().toString().equals(getSP("color"))){
                            gd.setStroke(10,Color.parseColor("#3498DB"));
                        }
                        gd.setCornerRadius(10);
                        c.setBackground(gd);
                    }
                }
            });
        }


        ((SeekBar) findViewById(R.id.cfsize)).setProgress(Integer.valueOf(getSP("size")));

        ((SeekBar) findViewById(R.id.cfsize)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSP("size",String.valueOf(progress));
                ((WebView) findViewById(R.id.c_viewer)).evaluateJavascript("javascript:(function(){"
                        + "document.getElementsByTagName(\"body\")[0].style.fontSize = \""+String.valueOf(progress)+"px\";"+
                        "})();",null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Intent i = getIntent();
        String text = i.getStringExtra("text");


        ((RelativeLayout) findViewById(R.id.c_main)).setBackgroundColor(Color.parseColor(getSP("color")));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getSP("color")));
        }

        ((WebView) findViewById(R.id.c_viewer)).setVerticalScrollBarEnabled(true);




        loadData(text);


        ((ImageButton) findViewById(R.id.c_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.c_seter)).setVisibility(VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(((LinearLayout) findViewById(R.id.c_seter)).getVisibility() == VISIBLE){
            ((LinearLayout) findViewById(R.id.c_seter)).setVisibility(INVISIBLE);
        }
        else{
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }
    }

    public void loadData(String text){
        String fontName = "";
        String fontURL = "";
        String oFontName = "";
        String oFontURL = "";
        if(getSP("font").equals("od")){
            fontName = "OpenDyslexic";
            fontURL = "file:///android_asset/fonts/OpenDyslexic-Regular.ttf";
            oFontName = "SansForgetica";
            oFontURL = "file:///android_asset/fonts/SansForgetica-Regular.otf";
        }
        else if(getSP("font").equals("sf")){
            fontName = "SansForgetica";
            fontURL = "file:///android_asset/fonts/SansForgetica-Regular.otf";
            oFontName = "OpenDyslexic";
            oFontURL = "file:///android_asset/fonts/OpenDyslexic-Regular.ttf";
        }

        String fontSize = getSP("size");
        String bg = getSP("color");
        String font_color = "";
        switch (bg){
            case "#FDFEFE":font_color = "#000000";break;
            case "#17202A":font_color = "#FFFFFF";break;
            case "#2C3E50":font_color = "#FFFFFF";break;
            case "#424949":font_color = "#FFFFFF";break;
            case "#c99868":font_color = "#000000";break;
            case "#34495E":font_color = "#FFFFFF";break;
        }


        String css_set = "<head>\n" +
                "<style type=\"text/css\">\n" +
                "@font-face {\n" +
                "    font-family: "+fontName+";\n" +
                "    src: url(\""+fontURL+"\");\n" +
                "}\n" +
                "@font-face {\n" +
                "    font-family: "+oFontName+";\n" +
                "    src: url(\""+oFontURL+"\");\n" +
                "}\n" +
                "body {\n" +
                "    padding: 10px;\n"+
                "    color: "+font_color+";\n"+
                "    background-color: "+bg+";\n"+
                "    font-family: "+fontName+";\n" +
                "    font-size: "+fontSize+"px;\n" +
                "    text-align: justify;\n" +
                "}\n" +
                "</style>\n" +
                "</head>";

        ((WebView) findViewById(R.id.c_viewer)).getSettings().setJavaScriptEnabled(true);

        String data = "";

        for(int i=0;i<=10;i++){
            data = text.replace("\r\n\r\n\r\n","\n\n").replace("\r\n \r\n \r\n","\n\n").replace("\n\n\n","\n\n").replace("\n","</br>").replace("\t","    ");
        }


        ((WebView) findViewById(R.id.c_viewer)).setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                ((WebView) findViewById(R.id.c_viewer)).scrollTo(0,0);
            }
        });

        ((WebView) findViewById(R.id.c_viewer)).loadDataWithBaseURL(null,"<html>"+css_set+"<body><p id='m_data'>"+data+"</p></body></html>","text/html","UTF-8",null);




        ((ImageButton) findViewById(R.id.c_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((findViewById(R.id.c_seter)).getVisibility() == VISIBLE){
                    ( findViewById(R.id.c_seter)).setVisibility(View.INVISIBLE);
                }
                else{
                    (findViewById(R.id.c_seter)).setVisibility(VISIBLE);
                }
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
