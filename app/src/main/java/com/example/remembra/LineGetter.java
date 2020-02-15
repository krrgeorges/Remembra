package com.example.remembra;

import android.widget.TextView;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class LineGetter extends PDFTextStripper {
    ArrayList<String> lines = new ArrayList();
    String fileName;

    public LineGetter(String fileName) throws IOException {
        this.fileName = fileName;
    }

    public ArrayList<String> getLines(){
        PDDocument document = null;
        try {
            document = PDDocument.load( new File(fileName) );
            PDFTextStripper stripper = new LineGetter(this.fileName);
            stripper.setSortByPosition( true );
            stripper.setStartPage( 0 );
            stripper.setEndPage( document.getNumberOfPages() );
            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);
            if( document != null ) {
                document.close();
            }
            return lines;
        }
        catch (IOException e){
            return new ArrayList<String>(){{add("ERROR-IO");}};
        }
    }



    @Override
    protected void writeString(String text) throws IOException {
        lines.add(text);
    }
}
