package com.example.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.model.ModelDaftarPenyakit;
import com.example.model.ModelKonsultasi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "catcare.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase sqLiteDatabase;
    private final Context ctx;
    private boolean needUpdate = false;

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.ctx = context.getApplicationContext();

        copyDatabase();

        this.getWritableDatabase();
    }

    public void updateDatabase() throws IOException {
        if (needUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDatabase();

            needUpdate = false;
        }
    }

    public boolean openDatabase() throws SQLException {
        sqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        return sqLiteDatabase != null;
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }

    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDatabase() {
        if (!checkDatabase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
                setDatabaseWritable();
            } catch (IOException e) {
                throw new Error("ErrorCopyingDatabase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream inputStream = ctx.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] bBuffer = new byte[1024];
        int iLength;
        while ((iLength = inputStream.read(bBuffer)) > 0)
            outputStream.write(bBuffer, 0, iLength);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void setDatabaseWritable() {
        File dbFile = new File(DB_PATH + DB_NAME);
        dbFile.setWritable(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            needUpdate = true;
    }

    public ArrayList<ModelDaftarPenyakit> getDaftarPenyakit() {
        ArrayList<ModelDaftarPenyakit> draftOffline = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT id_penyakit, nama_penyakit FROM penyakit ORDER BY id_penyakit";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ModelDaftarPenyakit modelDraftOffline = new ModelDaftarPenyakit();
                modelDraftOffline.setStrKode(cursor.getString(0));
                modelDraftOffline.setStrDaftarPenyakit(cursor.getString(1));
                draftOffline.add(modelDraftOffline);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return draftOffline;
    }

    public boolean isConnected() {
        return sqLiteDatabase != null && sqLiteDatabase.isOpen();
    }

    public ArrayList<ModelKonsultasi> getDaftarGejala() {
        ArrayList<ModelKonsultasi> draftOffline = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT nama_gejala FROM gejala ORDER BY id_gejala";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ModelKonsultasi modelDraftOffline = new ModelKonsultasi();
                modelDraftOffline.setStrGejala(cursor.getString(0));
                draftOffline.add(modelDraftOffline);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return draftOffline;
    }
}

