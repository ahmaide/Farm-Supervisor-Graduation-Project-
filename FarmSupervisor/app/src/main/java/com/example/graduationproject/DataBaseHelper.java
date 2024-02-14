package com.example.graduationproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE FARMER(EMAIL TEXT PRIMARY KEY, FIRSTNAME TEXT, LASTNAME TEXT, " +
                "PASSWORD TEXT NOT NULL,PHONE TEXT,ADDRESS TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE CROPS(cropName TEXT PRIMARY KEY)");

        sqLiteDatabase.execSQL("CREATE TABLE FARMER_CROPS(EMAIL TEXT , cropName TEXT," +
                " PRIMARY KEY(EMAIL, cropName), " +
                "FOREIGN KEY (EMAIL) REFERENCES FARMER(EMAIL) ON DELETE CASCADE ON UPDATE CASCADE," +
                " FOREIGN KEY (cropName) REFERENCES CROPS(cropName) ON DELETE CASCADE ON UPDATE CASCADE)");

    }

    public boolean insertFarmer(Farmer farmer) {

        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();
        Cursor cursor = sqLiteDatabaseR.rawQuery("SELECT * FROM FARMER WHERE EMAIL = \"" + farmer.getEmail() + "\";", null);
        if (!cursor.moveToFirst()) {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("EMAIL", farmer.getEmail());
            contentValues.put("FIRSTNAME", farmer.getFirstName());
            contentValues.put("LASTNAME", farmer.getLastName());
            contentValues.put("PASSWORD", farmer.getPassword());
            contentValues.put("PHONE", farmer.getMobileNumber());
            sqLiteDatabase.insert("FARMER", null, contentValues);
            return true;
        }
        return false;

    }

    public boolean updateFarmer(Farmer farmer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // on below line we are passing all values along with its key and value pair.
        values.put("EMAIL", farmer.getEmail());
        values.put("FIRSTNAME", farmer.getFirstName());
        values.put("LASTNAME", farmer.getLastName());
        values.put("PASSWORD", farmer.getPassword());
        values.put("PHONE", farmer.getMobileNumber());

        db.update("FARMER", values, "EMAIL=?", new String[]{farmer.getEmail()});
        db.close();

        return true;
    }

    public boolean isFarmerRegistered(String email){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM FARMER WHERE EMAIL = \"" + email + "\";", null);
        return cursor.moveToFirst();
    }


    public boolean correctFarmerSignIn(String email, String password){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM FARMER WHERE EMAIL = \"" + email + "\" AND PASSWORD = \"" + password + "\";", null);
        return cursor.moveToFirst();
    }


    public Farmer getFarmerByEmail(String email){
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();
        Farmer farmer = new Farmer();
        Cursor cursor = sqLiteDatabaseR.rawQuery("SELECT * FROM FARMER WHERE EMAIL = \"" + email + "\";", null);
        if (cursor.moveToFirst()) {
            farmer.setEmail(cursor.getString(0));
            farmer.setFirstName(cursor.getString(1));
            farmer.setLastName(cursor.getString(2));
            farmer.setPassword(cursor.getString(3));
            farmer.setMobileNumber(cursor.getString(4));

        }
        return farmer;
    }

    public boolean insertCrops(String crops) {

        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();
        Cursor cursor = sqLiteDatabaseR.rawQuery("SELECT * FROM CROPS WHERE cropName = \"" + crops+ "\";", null);
        if (!cursor.moveToFirst()) {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("cropName", crops);
            sqLiteDatabase.insert("CROPS", null, contentValues);
            return true;
        }
        return false;

    }

    public void insertFarmerCrops(Farmer farmer, String cropName) {
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();
        Cursor cursor = sqLiteDatabaseR.rawQuery("SELECT * FROM FARMER_CROPS WHERE EMAIL = \"" + farmer.getEmail() + "\" AND cropName = \"" + cropName + "\";", null);
        if (!cursor.moveToFirst()) {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("EMAIL", farmer.getEmail());
            contentValues.put("cropName", cropName);
            sqLiteDatabase.insert("FARMER_CROPS", null, contentValues);
        }
    }

    public Cursor getAllCrops() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM CROPS", null);
    }

    public Cursor getAllFarmers() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM FARMER", null);
    }

    public Cursor getAllFarmerCrops(String farmerEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT fc.cropName FROM FARMER_CROPS fc JOIN CROPS c ON fc.cropName = c.cropName WHERE FC.EMAIL = ?";
        return sqLiteDatabase.rawQuery(query, new String[]{farmerEmail});
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
