package com.example.user.mystudying;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

     private static final String DATABASE_NAME = "Studying.db";
    private static final String TABLE_NAME = "studying_table";
    private static final String COL_1 = "SUBJECT";
    private static final String COL_2 = "HOURS";
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (SUBJECT TEXT PRIMARY KEY ,HOURS INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String subject,int hours)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,subject);
        contentValues.put(COL_2,hours);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        return  result!=-1;

    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);

    }
    public boolean updateData(String subject,String hours) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,subject);
        contentValues.put(COL_2,hours);
        db.update(TABLE_NAME, contentValues, "SUBJECT = ?",new String[] { subject });
        return true;
    }
    public Integer deleteData (String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "SUBJECT = ?",new String[] {subject});
    }
}
