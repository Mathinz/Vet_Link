package com.vet.link.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "accessme";
    private static final String TABLE = "favplace";
    private boolean isexists;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

//--------------------------------------------------------------------------------------create table

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "create table favplace(place_id TEXT not null);";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
//--------------------------------------------------------------------------------------upgrade table

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        onCreate(db);
    }
//--------------------------------------------------------------------------------------insert into table

    public void addtoFav(String place_id) {


        db = this.getWritableDatabase();

        String sql = "insert into favplace(place_id) values('" + place_id + "')";
        db.execSQL(sql);
        db.close();

    }

    public boolean isDataExists(String place_id) {
        db = this.getWritableDatabase();
        try {
            Cursor cursor;
            String sql = "SELECT place_id FROM " + TABLE + " WHERE place_id = '" + place_id + "'";
            cursor = db.rawQuery(sql, null);
            Log.e("Cursor Count : ", "" + cursor.getCount());

            isexists = cursor.getCount() > 0;
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isexists;

    }

//--------------------------------------------------------------------------------------select from table

/*
    public List<Model> getAllFav() {
        List<Model> FavoriteList = new ArrayList<>();
        db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Model model = new Model();
                model.setProduct_id(cursor.getInt(0));
               */
/* model.setTitle(cursor.getString(1));
                model.setDescription(cursor.getString(2));
                model.setLatitude(cursor.getDouble(3));
                model.setLongitude(cursor.getDouble(4));
                model.setDistance(cursor.getFloat(5));
                model.setPrice(cursor.getInt(6));
                model.setCurrency(cursor.getString(7));
                model.setCategory(cursor.getString(8));*//*


                FavoriteList.add(model);

            } while (cursor.moveToNext());
        }
        db.close();

        return FavoriteList;
    }
*/

//--------------------------------------------------------------------------------------delete from table

/*
    public void removefromFav(Integer product_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from favproduct where product_id = " + product_id);
        db.close();
    }
*/


}