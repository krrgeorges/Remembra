package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ViewerActivity extends AppCompatActivity {
    int sp;
    int ep;
    String doc;
    int pages;
    PDFTextStripper stripper;
    PDDocument document;
    ArrayList<String> data=new ArrayList<>();
    int currentIndex = 0;
    String vdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        ((LinearLayout) findViewById(R.id.v_seter)).setVisibility(View.INVISIBLE);

        if(getSP("font") == "od"){
            ((Button) findViewById(R.id.vod)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.vod)).setTextColor(Color.parseColor("#FFFFFF"));
        }
        else if(getSP("font") == "sf"){
            ((Button) findViewById(R.id.vsf)).setBackground(getDrawable(R.drawable.fsel_bg));
            ((Button) findViewById(R.id.vsf)).setTextColor(Color.parseColor("#FFFFFF"));
        }


        ((PhotoView) findViewById(R.id.testy)).bringToFront();


        ((Button) findViewById(R.id.vod)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="od"){
                    setSP("font","od");
                    ((WebView) findViewById(R.id.viewer)).evaluateJavascript("javascript:(function(){"
                                    + "document.getElementsByTagName(\"body\")[0].style.fontFamily = \"OpenDyslexic\";"+
                            "})()",null);
                    ((Button) findViewById(R.id.vod)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.vod)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.vsf)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.vsf)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ((Button) findViewById(R.id.vsf)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSP("font")!="sf"){
                    setSP("font","sf");
                    ((WebView) findViewById(R.id.viewer)).evaluateJavascript("javascript:(function(){"
                            + "document.getElementsByTagName(\"body\")[0].style.fontFamily = \"SansForgetica\";"+
                            "})()",null);
                    ((Button) findViewById(R.id.vsf)).setBackground(getDrawable(R.drawable.fsel_bg));
                    ((Button) findViewById(R.id.vsf)).setTextColor(Color.parseColor("#FFFFFF"));

                    ((Button) findViewById(R.id.vod)).setBackground(getDrawable(R.drawable.fchooser_btn_bg));
                    ((Button) findViewById(R.id.vod)).setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        final Button[] colors = {((Button) findViewById(R.id.vc1)),((Button) findViewById(R.id.vc2)),((Button) findViewById(R.id.vc3)),((Button) findViewById(R.id.vc4)),((Button) findViewById(R.id.vc5)),((Button) findViewById(R.id.vc6))};



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
                    ((WebView) findViewById(R.id.viewer)).evaluateJavascript("javascript:(function(){"
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


        ((SeekBar) findViewById(R.id.vfsize)).setProgress(Integer.valueOf(getSP("size")));

        ((SeekBar) findViewById(R.id.vfsize)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSP("size",String.valueOf(progress));
                ((WebView) findViewById(R.id.viewer)).evaluateJavascript("javascript:(function(){"
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
        doc = i.getStringExtra("doc");


        DBWrapper db = new DBWrapper(this);
        db.insertRecent(doc, Calendar.getInstance().getTimeInMillis());

        ((RelativeLayout) findViewById(R.id.v_main)).setBackgroundColor(Color.parseColor(getSP("color")));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getSP("color")));
        }

        ((WebView) findViewById(R.id.viewer)).setVerticalScrollBarEnabled(true);
        ((WebView) findViewById(R.id.viewer)).getSettings().setJavaScriptEnabled(true);

        PDFBoxResourceLoader.init(getApplicationContext());
        try {
            document = PDDocument.load(new File(doc));

            pages = document.getNumberOfPages();


            stripper = new PDFLayoutTextStripper();
            stripper.setSortByPosition(false);
            stripper.setShouldSeparateByBeads(true);


            sp = currentIndex;
            ep = sp+1;

            vdata = "";

            stripper.setStartPage(sp);
                        stripper.setEndPage(ep);

                        try {
                            vdata = stripper.getText(document);

                        } catch (IOException e) {
                            onBackPressed();
                        }


                        if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }


            loadData(vdata);
            ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
            ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);

            findViewById(R.id.vleft).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentIndex != 0){
                        currentIndex--;
                        sp = currentIndex;
                        ep = currentIndex+1;

                        vdata = "";

                        setSP(doc,sp+" "+ep);

                        stripper.setStartPage(sp);
                        stripper.setEndPage(ep);

                        try {
                            vdata = stripper.getText(document);

                        } catch (IOException e) {
                            onBackPressed();
                        }

                        if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }

                        loadData(vdata);
                        ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                        ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                    }
                }
            });

            findViewById(R.id.vright).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentIndex < document.getNumberOfPages()){
                        currentIndex++;
                        sp = currentIndex;
                        ep = currentIndex+1;

                        vdata = "";

                        setSP(doc,sp+" "+ep);

                        stripper.setStartPage(sp);
                        stripper.setEndPage(ep);

                        try {
                            vdata = stripper.getText(document);

                        } catch (IOException e) {
                            onBackPressed();
                        }
                        if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }



                        loadData(vdata);
                        ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                        ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                    }
                }
            });



        } catch (IOException e) {
            onBackPressed();
        }


        ((WebView) findViewById(R.id.viewer)).setOnTouchListener(new OnSwipeTouchListener(){

            @Override
            public boolean onClick() {
                if(findViewById(R.id.drawer).getVisibility() == INVISIBLE){
                    findViewById(R.id.drawer).setVisibility(VISIBLE);
                }
                else{
                    findViewById(R.id.drawer).setVisibility(INVISIBLE);
                }

                if(findViewById(R.id.vpage).getVisibility() == INVISIBLE){
                    findViewById(R.id.vpage).setVisibility(VISIBLE);
                }
                else{
                    findViewById(R.id.vpage).setVisibility(INVISIBLE);
                }

                if(findViewById(R.id.v_seter).getVisibility() == VISIBLE){
                    findViewById(R.id.v_seter).setVisibility(INVISIBLE);
                }
                return true;
            }

            @Override
            public boolean onSwipeLeft() {

                if(currentIndex < document.getNumberOfPages()){
                    currentIndex++;
                    sp = currentIndex;
                    ep = currentIndex+1;

                    vdata = "";

                    setSP(doc,sp+" "+ep);

                    stripper.setStartPage(sp);
                    stripper.setEndPage(ep);

                    try {
                        vdata = stripper.getText(document);

                    } catch (IOException e) {
                        onBackPressed();
                    }

                    if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }




                    loadData(vdata);
                    ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                    ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                }
                return true;
            }

            @Override
            public boolean onSwipeRight() {
                if(currentIndex != 0){
                    currentIndex--;
                    sp = currentIndex;
                    ep = currentIndex+1;

                    vdata = "";

                    setSP(doc,sp+" "+ep);

                    stripper.setStartPage(sp);
                    stripper.setEndPage(ep);

                    try {
                        vdata = stripper.getText(document);

                    } catch (IOException e) {
                        onBackPressed();
                    }


                    if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }
                    loadData(vdata);
                    ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                    ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                }
                return true;
            }

        });

        ((ImageButton) findViewById(R.id.testy_l)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex != 0){
                    currentIndex--;
                    sp = currentIndex;
                    ep = currentIndex+1;

                    vdata = "";

                    setSP(doc,sp+" "+ep);

                    stripper.setStartPage(sp);
                    stripper.setEndPage(ep);

                    try {
                        vdata = stripper.getText(document);

                    } catch (IOException e) {
                        onBackPressed();
                    }


                    if(sp-1 > 0){
                        showPage(sp-1);
                    }
                    else{
                        showPage(sp);
                    }
                    loadData(vdata);
                    ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                    ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                }
            }
        });

        ((ImageButton) findViewById(R.id.testy_r)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex < document.getNumberOfPages()){
                    currentIndex++;
                    sp = currentIndex;
                    ep = currentIndex+1;

                    vdata = "";

                    setSP(doc,sp+" "+ep);

                    stripper.setStartPage(sp);
                    stripper.setEndPage(ep);

                    try {
                        vdata = stripper.getText(document);

                    } catch (IOException e) {
                        onBackPressed();
                    }

                    if(sp-1 > 0){
                        showPage(sp-1);
                    }
                    else{
                        showPage(sp);
                    }




                    loadData(vdata);
                    ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
                    ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
                }
            }
        });
        final Context c = this;
        ((ImageButton) findViewById(R.id.vsearch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,SearchActivity.class);
                i.putExtra("doc",doc);
                startActivity(i);
            }
        });


        ((ImageButton) findViewById(R.id.vimage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PhotoView) findViewById(R.id.testy)).setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.testy_c)).setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.testy_l)).setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.testy_r)).setVisibility(VISIBLE);
                ((ImageButton) findViewById(R.id.testy_c)).bringToFront();
                ((ImageButton) findViewById(R.id.testy_l)).bringToFront();
                ((ImageButton) findViewById(R.id.testy_r)).bringToFront();
            }
        });

        ((ImageButton) findViewById(R.id.testy_c)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PhotoView) findViewById(R.id.testy)).setVisibility(INVISIBLE);
                ((ImageButton) findViewById(R.id.testy_c)).setVisibility(INVISIBLE);
                ((ImageButton) findViewById(R.id.testy_l)).setVisibility(INVISIBLE);
                ((ImageButton) findViewById(R.id.testy_r)).setVisibility(INVISIBLE);

            }
        });

    }

    ParcelFileDescriptor parcelFileDescriptor;
    PdfRenderer pdfRenderer;
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(doc);
        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open("rp.pdf");
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        // This is the PdfRenderer we use to render the PDF.
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    private void closeRenderer() throws IOException {
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }
    PdfRenderer.Page currentPage;

    private void showPage(int index) {
        try {
            openRenderer(this);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        if (null != currentPage) {
            currentPage.close();
        }
        currentPage = pdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        
        ((PhotoView) findViewById(R.id.testy)).setImageBitmap(bitmap);

    }


    @Override
    public void onBackPressed() {
        if(findViewById(R.id.v_seter).getVisibility() == VISIBLE){
            findViewById(R.id.v_seter).setVisibility(View.INVISIBLE);
        }
        if(findViewById(R.id.testy).getVisibility() == VISIBLE){
            ((PhotoView) findViewById(R.id.testy)).setVisibility(INVISIBLE);
            ((ImageButton) findViewById(R.id.testy_c)).setVisibility(INVISIBLE);
            ((ImageButton) findViewById(R.id.testy_l)).setVisibility(INVISIBLE);
            ((ImageButton) findViewById(R.id.testy_r)).setVisibility(INVISIBLE);
        }
        else{
            setSP(doc,sp+" "+ep);
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        setSP(doc,sp+" "+ep);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();




        if(getSP(doc).equals("") == false){
            String[] ps = getSP(doc).split(" ");
            setSP(doc,"");


            currentIndex = sp;
            sp = Integer.valueOf(ps[0]);
            ep = Integer.valueOf(ps[1]);

            Toast.makeText(getApplicationContext(),sp+" "+ep,Toast.LENGTH_LONG).show();

            stripper.setStartPage(sp);
            stripper.setEndPage(ep);
            try {
                vdata = stripper.getText(document);

            } catch (IOException e) {
                onBackPressed();
            }

            if(sp-1 > 0){
                showPage(sp-1);
            }
            else{
                showPage(sp);
            }
            
            

            loadData(vdata);
            ((TextView) findViewById(R.id.vpage)).setText("Page "+ep);
            ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
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

        ((WebView) findViewById(R.id.viewer)).getSettings().setJavaScriptEnabled(true);

        String data = "";

        for(int i=0;i<=10;i++){
            data = text.replace("\r\n\r\n\r\n","\n\n").replace("\r\n \r\n \r\n","\n\n").replace("\n\n\n","\n\n").replace("\n","</br>").replace("\t","    ");
        }


        ((WebView) findViewById(R.id.viewer)).setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                ((WebView) findViewById(R.id.viewer)).scrollTo(0,0);
            }
        });

        ((WebView) findViewById(R.id.viewer)).loadDataWithBaseURL(null,"<html>"+css_set+"<body><p id='m_data'>"+data+"</p></body></html>","text/html","UTF-8",null);




        ((ImageButton) findViewById(R.id.v_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((findViewById(R.id.v_seter)).getVisibility() == VISIBLE){
                    ( findViewById(R.id.v_seter)).setVisibility(View.INVISIBLE);
                }
                else{
                    (findViewById(R.id.v_seter)).setVisibility(VISIBLE);
                }
            }
        });

        final Context c = this;

        ((ImageButton) findViewById(R.id.vgoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c,GoToActivity.class);
                i.putExtra("npages",pages);
                i.putExtra("doc",doc);
                startActivity(i);
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

class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            result = onSwipeRight();
                        } else {
                            result = onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            result = onSwipeBottom();
                        } else {
                            result = onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

    }



    public boolean onClick(){
        return false;
    }

    public boolean onSwipeRight() {
        return false;
    }

    public boolean onSwipeLeft() {
        return false;
    }

    public boolean onSwipeTop() {
        return false;
    }

    public boolean onSwipeBottom() {
        return false;
    }


}