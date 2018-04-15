package usthb.lfbservices.com.pfe.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import usthb.lfbservices.com.pfe.database.tables.Category;
import usthb.lfbservices.com.pfe.database.tables.City;
import usthb.lfbservices.com.pfe.database.tables.Type;
import usthb.lfbservices.com.pfe.database.tables.Wilaya;

/**
 * Created by ryadh on 14/04/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DatabaseHelper.class.getName();

    // Database Version
    private static int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LFB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Category.CREATE_TABLE);
        sqLiteDatabase.execSQL(Category.INSERT_VALUES);

        sqLiteDatabase.execSQL(Type.CREATE_TABLE);
        sqLiteDatabase.execSQL(Type.INSERT_VALUES);

        sqLiteDatabase.execSQL(Wilaya.CREATE_TABLE);
        sqLiteDatabase.execSQL(Wilaya.INSERT_VALUES);

        sqLiteDatabase.execSQL(City.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Type.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + City.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Wilaya.TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    public String[] getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> categories = new ArrayList<String>();

        String selectQuery = "SELECT " + Category.COLUMN_CATEGORY_NAME + " FROM "
                + Category.TABLE_NAME + " ORDER BY " +
                Category.COLUMN_CATEGORY_ID + " ASC; ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(Category.COLUMN_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }

        db.close();
        return categories.toArray(new String[categories.size()]);
    }


    public String[] getTypes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> types = new ArrayList<String>();

        String selectQuery = "SELECT " + Type.COLUMN_TYPE_NAME + " FROM "
                + Type.TABLE_NAME + " ORDER BY " +
                Type.COLUMN_TYPE_ID + " ASC; ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                types.add(cursor.getString(cursor.getColumnIndex(Type.COLUMN_TYPE_NAME)));
            } while (cursor.moveToNext());
        }

        db.close();
        return types.toArray(new String[types.size()]);
    }


    public String[] getWilayas() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> wilayas = new ArrayList<String>();

        String selectQuery = "SELECT " + Wilaya.COLUMN_WILAYA_NAME + " FROM "
                + Wilaya.TABLE_NAME + " ORDER BY " +
                Wilaya.COLUMN_WILAYA_ID + " ASC; ";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                wilayas.add(cursor.getString(cursor.getColumnIndex(Wilaya.COLUMN_WILAYA_NAME)));
            } while (cursor.moveToNext());
        }

        db.close();
        return wilayas.toArray(new String[wilayas.size()]);
    }
}
