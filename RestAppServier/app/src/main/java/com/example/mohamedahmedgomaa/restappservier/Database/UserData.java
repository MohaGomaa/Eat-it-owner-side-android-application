package com.example.mohamedahmedgomaa.restappservier.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserData extends SQLiteOpenHelper {
    private static final String TableName="User";
    private  static  final String col1="Image";

    public UserData(Context context) {
        super(context, TableName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TableName+" ("+col1+" TEXT PRIMARY KEY UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
     db.execSQL("DROP TABLE IF EXISTS "+TableName);
        onCreate(db);
    }

    public  void addImg(String id)
    {  try {


        ContentValues cv = new ContentValues();
        cv.put(col1, id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TableName, null, cv);
          }
        catch (Exception e)
        {
          }
    }


    public void removeImg(String id)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+TableName+" WHERE "+col1+" = '"+id+"'");
        }
        catch (Exception e)
        {
        }

    }
    public void clear()
    {  try {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableName);

    }
    catch (Exception e)
    {
    }
    }
    public Cursor getImage()
    {

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TableName,null);
        return c;
    }
    public boolean isFavorites(String FoodId)
    {
        SQLiteDatabase db=getReadableDatabase();
        //String query=String.format("SELECT * FROM Favorites WHERE FoodId='$s';",FoodId);
      //  Cursor cursor=db.rawQuery(query,null);
        Cursor c = db.rawQuery("SELECT * FROM " + TableName + " WHERE " + col1 + " = '" + FoodId+"'", null);

        if(c.getCount()<=0)
        {
            c.close();
            return false;
        }
        c.close();
        return  true;
    }


}
