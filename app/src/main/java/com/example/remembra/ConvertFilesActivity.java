package com.example.remembra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.stream.IntStream;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConvertFilesActivity extends AppCompatActivity {
    int currentIndex,sp,ep;
    String vdata;
    ArrayList<PDDocument> pdocs;
    int tp;
    Timer t;
    PDFLayoutTextStripper stripper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_files);


        ArrayList<String> docs = getIntent().getStringArrayListExtra("docs");

        pdocs = new ArrayList<>();

        tp = 0;

        PDFBoxResourceLoader.init(getApplicationContext());
        PDDocument document;


        for(String doc:docs){
            try {
                document = PDDocument.load(new File(doc));
                pdocs.add(document);
                tp += document.getNumberOfPages();
            } catch (IOException e) {
                continue;
            }
        }

        String fontname = "";

        if(getSP("font").equals("od")){
            fontname = "OpenDyslexic-Regular";
        }
        else if(getSP("font").equals("sf")){
            fontname = "SansForgetica-Regular";
        }

        File f = new File(getCacheDir()+"/"+fontname+".ttf");
        if (!f.exists()){
            try {
                InputStream is = getAssets().open("fonts/"+fontname+".ttf");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();


                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {  }
        }

        ((ProgressBar) findViewById(R.id.cfpb)).setMax(tp);

        stripper = null;
        try {
            stripper = new PDFLayoutTextStripper();
        } catch (IOException e) {
            finish();
        }
        stripper.setSortByPosition(false);
        stripper.setShouldSeparateByBeads(true);

        PDType0Font font = null;

        File b = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remembra");
        if(b.exists() == false){
            b.mkdir();
        }



        for(PDDocument pdoc:pdocs){
            PDDocument npdoc = new PDDocument();
            try {
                font = PDType0Font.load(npdoc, f);
            } catch (IOException e) {
                finish();
            }
            for(int i=0;i<= pdoc.getNumberOfPages();i++){
                ((ProgressBar) findViewById(R.id.cfpb)).setProgress(((ProgressBar) findViewById(R.id.cfpb)).getProgress()+1);
                currentIndex = i;
                sp = i;
                ep = sp+1;
                vdata = "";
                stripper.setStartPage(sp);
                stripper.setEndPage(ep);
                stripper.setStartPage(sp);
                stripper.setEndPage(ep);
                try {
                    vdata = stripper.getText(pdoc);
                    PDPage npage = new PDPage();
                } catch (IOException e) {
                    finish();
                }
                PDPage page = new PDPage(PDRectangle.A4);

                PDPageContentStream contentStream = null;
                try {
                    contentStream = new PDPageContentStream(npdoc, page);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(0, 0);
                    contentStream.setFont(font,Integer.valueOf(getSP("size")));

                    contentStream.showText(vdata.replaceAll("\\p{Cc}", ""));
                    contentStream.endText();
                    contentStream.close();

                } catch (IOException e) {
                    finish();
                }
                npdoc.addPage(page);
            }
            try {
                npdoc.save(new File(Environment.getExternalStorageDirectory()+"/Remembra/"+new File(docs.get(pdocs.indexOf(pdoc))).getName()));
                npdoc.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"HEHEH",Toast.LENGTH_LONG).show();
            }
        }



        final Context c = this;
        ((Button) findViewById(R.id.cfcancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    t.cancel();
                }
                catch (Exception e){

                }
                Intent i = new Intent(c,MainActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

    }

    public void setSP(String key, String value){
        SharedPreferences.Editor spe = getSharedPreferences("remembra",MODE_PRIVATE).edit();
        spe.putString(key,value);
        spe.commit();
    }

    public String getSP(String key){
        return getSharedPreferences("remembra",MODE_PRIVATE).getString(key,"");
    }
}
