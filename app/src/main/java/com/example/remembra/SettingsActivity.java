package com.example.remembra;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView textView = (TextView) findViewById(R.id.s_head);

        TextPaint paint = textView.getPaint();
        float width = paint.measureText("Settings");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#00C9FF"),
                        Color.parseColor("#92FE9D")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);


        if(getSP("font") == "od"){
            ((Button) findViewById(R.id.od)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.od)).setTextColor(Color.parseColor("#FFFFFF"));
        }
        else if(getSP("font") == "sf"){
            ((Button) findViewById(R.id.sf)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.sf)).setTextColor(Color.parseColor("#FFFFFF"));
        }


        ((Button) findViewById(R.id.od)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="od"){
                    setSP("font","od");
                    loadData(((WebView) findViewById(R.id.sample_view)));
                    ((Button) findViewById(R.id.od)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.od)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.sf)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.sf)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ((Button) findViewById(R.id.sf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="sf"){
                    setSP("font","sf");
                    loadData(((WebView) findViewById(R.id.sample_view)));
                    ((Button) findViewById(R.id.sf)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.sf)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.od)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.od)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        final Button[] colors = {((Button) findViewById(R.id.c1)),((Button) findViewById(R.id.c2)),((Button) findViewById(R.id.c3)),((Button) findViewById(R.id.c4)),((Button) findViewById(R.id.c5)),((Button) findViewById(R.id.c6))};



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
                    loadData(((WebView) findViewById(R.id.sample_view)));
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

        ((TextView) findViewById(R.id.size_tv)).setText(getSP("size"));

        ((SeekBar) findViewById(R.id.fsize)).setProgress(Integer.valueOf(getSP("size")));

        ((SeekBar) findViewById(R.id.fsize)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSP("size",String.valueOf(progress));
                loadData(((WebView) findViewById(R.id.sample_view)));
                ((TextView) findViewById(R.id.size_tv)).setText(String.valueOf(progress)+"px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ((WebView) findViewById(R.id.sample_view)).setVerticalScrollBarEnabled(true);

        loadData(((WebView) findViewById(R.id.sample_view)));


    }

    public void loadData(WebView wv){
        String fontName = "";
        String fontURL = "";

        if(getSP("font").equals("od")){
            fontName = "OpenDyslexic";
            fontURL = "file:///android_asset/fonts/OpenDyslexic-Regular.ttf";
        }
        else if(getSP("font").equals("sf")){
            fontName = "SansForgetica";
            fontURL = "file:///android_asset/fonts/SansForgetica-Regular.otf";
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
        ((WebView) findViewById(R.id.sample_view)).loadDataWithBaseURL(null,"<html>"+css_set+"<p>In nuclear physics and nuclear chemistry, nuclear fission is a nuclear reaction or a radioactive decay process in which the nucleus of an atom splits into smaller, lighter nuclei. The fission process often produces free neutrons and gamma photons, and releases a very large amount of energy even by the energetic standards of radioactive decay.</p></html>","text/html","UTF-8",null);
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
