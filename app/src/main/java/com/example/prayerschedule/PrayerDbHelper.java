package com.example.prayerschedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrayerDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prayer_schedule.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MOSQUE = "mosque";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";

    public static final String TABLE_ANNOUNCEMENTS = "announcements";
    public static final String COLUMN_ANNOUNCEMENT = "announcement";

    public static final String TABLE_PRAYER_TIMES = "prayer_times";
    public static final String COLUMN_PRAYER_NAME = "prayer_name";
    public static final String COLUMN_PRAYER_TIME = "prayer_time";

    private static final String CREATE_TABLE_MOSQUE =
            "CREATE TABLE " + TABLE_MOSQUE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT);";

    private static final String CREATE_TABLE_ANNOUNCEMENTS =
            "CREATE TABLE " + TABLE_ANNOUNCEMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ANNOUNCEMENT + " TEXT);";

    private static final String CREATE_TABLE_PRAYER_TIMES =
            "CREATE TABLE " + TABLE_PRAYER_TIMES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PRAYER_NAME + " TEXT, " +
                    COLUMN_PRAYER_TIME + " TEXT);";

    public PrayerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MOSQUE);
        db.execSQL(CREATE_TABLE_ANNOUNCEMENTS);
        db.execSQL(CREATE_TABLE_PRAYER_TIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity, drop tables and recreate on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOSQUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRAYER_TIMES);
        onCreate(db);
    }
    // -------------------- Fungsi untuk Masjid --------------------

    public void updateMasjid(String name, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Hapus data lama (supaya hanya ada 1 record masjid)
        db.delete(TABLE_MOSQUE, null, null);

        String insertQuery = "INSERT INTO " + TABLE_MOSQUE + " (" +
                COLUMN_NAME + ", " + COLUMN_ADDRESS + ") VALUES (?, ?)";
        db.execSQL(insertQuery, new Object[]{name, address});
    }

    public android.database.Cursor getMasjid() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MOSQUE + " LIMIT 1", null);
    }

// -------------------- Fungsi untuk Pengumuman --------------------

    public void insertPengumuman(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertQuery = "INSERT INTO " + TABLE_ANNOUNCEMENTS + " (" + COLUMN_ANNOUNCEMENT + ") VALUES (?)";
        db.execSQL(insertQuery, new Object[]{text});
    }

    public void updatePengumuman(String oldText, String newText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_ANNOUNCEMENTS +
                " SET " + COLUMN_ANNOUNCEMENT + " = ? WHERE " + COLUMN_ANNOUNCEMENT + " = ?";
        db.execSQL(updateQuery, new Object[]{newText, oldText});
    }

    public void deletePengumuman(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_ANNOUNCEMENTS + " WHERE " + COLUMN_ANNOUNCEMENT + " = ?";
        db.execSQL(deleteQuery, new Object[]{text});
    }

    public java.util.ArrayList<String> getAllPengumuman() {
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ANNOUNCEMENTS, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

}
