package com.example.remembra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedHashMap;

public class DBWrapper extends SQLiteOpenHelper {

    DBWrapper(Context c){
        super(c,"RemembraDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String for_recents = "CREATE TABLE recents(id INTEGER PRIMARY KEY autoincrement,fname TEXT UNIQUE, rtime INTEGER);";
        db.execSQL(for_recents);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE recents;");
        onCreate(db);
    }

    public long insertRecent(String path,long time){
        this.getWritableDatabase().execSQL("DELETE FROM recents WHERE fname = '"+path+"';");
        ContentValues cv = new ContentValues();
        cv.put("fname",path);
        cv.put("rtime",time);
        return this.getWritableDatabase().insert("recents",null,cv);
    }

    public LinkedHashMap<String, Integer> getRecents(){
        Cursor c = this.getReadableDatabase().rawQuery("SELECT * FROM recents order by rtime asc LIMIT 10;",null);
        LinkedHashMap<String,Integer> data = new LinkedHashMap<>();
        while(c.moveToNext()) {
            data.put(c.getString(c.getColumnIndex("fname")), c.getInt(c.getColumnIndex("rtime")));
        }
        return data;
    }

}
