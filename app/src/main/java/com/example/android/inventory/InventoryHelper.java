package com.example.android.inventory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.InventoryContract.Inventory;

/**
 * Created by Jason on 04/07/2016.
 */
public class InventoryHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String FLOAT_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Inventory.TABLE_NAME + " (" + Inventory._ID + " " +
                    "INTEGER PRIMARY KEY," + Inventory.PRODUCT_NAME + TEXT_TYPE + COMMA_SEP +
                    Inventory.PRODUCT_QUANTITY + INTEGER_TYPE + COMMA_SEP +
                    Inventory.PRODUCT_PRICE + FLOAT_TYPE + COMMA_SEP +
                    Inventory.PRODUCT_IMAGE + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Inventory.TABLE_NAME;

    /**
     * Constructor
     * Creates the database and assigns a version number to it
     *
     * @param context
     */
    public InventoryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }


}
