package com.devapps.affiliate.Favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "MyDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(id integer primary key autoincrement, title varchar(20), webLink varchar(255), image varchar(40))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*new version */

    }


    public void insertData(Integer id, String title, String webLink, String image) {
        SQLiteDatabase sd = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("webLink", webLink);
        contentValues.put("image", image);


        sd.insert("user", null, contentValues);
        sd.close();
    }

    public Integer deleteTitle(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("user", "id= ?", new String[]{id});

    }

    public Cursor readData() {
        SQLiteDatabase sd = this.getReadableDatabase();
        Cursor c = sd.rawQuery("select * from user", null);
        return c;
    }
}
