package pager.demo.com.splciphertest;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    SQLiteDatabase database;
    File databaseFile;
    SQLiteDatabase newDb;
    TextView tx;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tx = findViewById(R.id.txt);
        SQLiteDatabase.loadLibs(this);

      insertSthToDb();
       // showData();
        //InitializeSQLCipher();

    }

    private void showData() {
        String text = "";

        try {
            File newDbf = getDatabasePath(FeedReaderDbHelper.DATABASE_NAME);
            db = SQLiteDatabase.openDatabase(newDbf.getPath(),"",null,SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.rawQuery("SELECT * FROM news '" +FeedReaderDbHelper.DATABASE_NAME+ "';", null);
          //  Cursor c = database.rawQuery("SELECT * from news", null);


            if (cursor.moveToFirst()) {
                do {

                    text = (cursor.getString(cursor.getColumnIndex("a")));

                } while (cursor.moveToNext());
                cursor.close();
                tx.setText(text);
            }

            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        /*database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});*/
    }

    private void insertSthToDb() {
        db = FeedReaderDbHelper.getInstance(this).getWritableDatabase("somePass");

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ENTRY_ID, 1);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "Helow!");
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "A thrilling f...");

        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

        Cursor cursor = db.rawQuery("SELECT * FROM '" + FeedReaderContract.FeedEntry.TABLE_NAME + "';", null);
       // Log.d(MainActivity.class.getSimpleName(), "Rows count: " + cursor.getCount());
        cursor.close();

        db.close();
      //  encrypt();
        // this will throw net.sqlcipher.database.SQLiteException: file is encrypted or is not a database: create locale table failed
        //db = FeedReaderDbHelper.getInstance(this).getWritableDatabase("");
    }

    public void ConvertNormalToSQLCipheredDB(Context context, String dbName, String dpName, String Password) {

        try {
            databaseFile = getDatabasePath(FeedReaderDbHelper.DATABASE_NAME);

            if (!databaseFile.exists()) {
                databaseFile.mkdirs();
            } else {
                databaseFile.delete();
            }
            newDb = SQLiteDatabase.openOrCreateDatabase(databaseFile, "somePass", null);
        /*    database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
                    "somePass", null);*/
            newDb.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' AS encrypted KEY '%s'", databaseFile.getAbsolutePath(), "somePass"));
            newDb.rawExecSQL("select sqlcipher_export('encrypted')");
          //  newDb.rawExecSQL("DETACH DATABASE encrypted");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen())
                db.close();
            // databaseFile.delete();
        }
    }
    private void decryptDatabase() {
        File unencryptedFile = getDatabasePath("new.db");

        if(!unencryptedFile.exists())
        {
            unencryptedFile.mkdir();
        }else{
            unencryptedFile.delete();
        }
        SQLiteDatabase newDb =SQLiteDatabase.openOrCreateDatabase(unencryptedFile, "", null);
      //  unencryptedFile.delete();
        File databaseFile2 = getDatabasePath(FeedReaderDbHelper.NEW_DATABASE_NAME);
       // unencryptedFile.delete();
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            public void preKey(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase
                        .rawExecSQL("PRAGMA cipher_default_use_hmac = off;");
            }

            public void postKey(SQLiteDatabase sqLiteDatabase) {
            }
        };
        SQLiteDatabase db1=null;

if (databaseFile2.exists()) {
     db1 = SQLiteDatabase.openDatabase(databaseFile2.getAbsolutePath(), "qwerty", null, SQLiteDatabase.OPEN_READWRITE);
        if (db1.isOpen()) {
            db1.rawExecSQL(String.format(
                    "ATTACH DATABASE '%s' as plaintext KEY '';",
                    unencryptedFile.getAbsolutePath()));
            db1.rawExecSQL("SELECT sqlcipher_export('plaintext');");
            db1.rawExecSQL("DETACH DATABASE plaintext;");
            android.database.sqlite.SQLiteDatabase sqlDB = android.database.sqlite.SQLiteDatabase
                    .openOrCreateDatabase(unencryptedFile, null);
            sqlDB.close();

        }
    db1.close();
        databaseFile2.delete();
}

    }
    public void encrypt() {
        databaseFile = getDatabasePath(FeedReaderDbHelper.DATABASE_NAME);
        File newDbf = getDatabasePath(FeedReaderDbHelper.NEW_DATABASE_NAME);
        if(!newDbf.exists())
        {

        }else{
            newDbf.delete();
        }

        //1. make empty encrypted db
        SQLiteDatabase newDb =SQLiteDatabase.openOrCreateDatabase(newDbf, "qwerty", null);
        //remove autogenerated tables
      //  newDb.rawExecSQL("drop table android_metadata;");
        //newDb.
        newDb.close();


    // open old, export values
    SQLiteDatabase db1 = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), "somePass", null, SQLiteDatabase.OPEN_READWRITE);

            try
        {
        String attachString = String.format("ATTACH DATABASE '%s' AS 'encrypted' KEY '%s';",
                newDbf.getAbsolutePath(), "qwerty");
        db1.rawExecSQL(attachString);
        db1.rawExecSQL("SELECT sqlcipher_export('encrypted');");
         db1.rawExecSQL("DETACH DATABASE encrypted");
        int version = db1.getVersion();
        db1.close();
        //open new, update version
        db1 = SQLiteDatabase.openDatabase(newDbf.getAbsolutePath(), "qwerty", null, SQLiteDatabase.OPEN_READWRITE);
        db1.setVersion(version);
        //delete old
       databaseFile.delete();
    }finally
    {
        db1.close();
    }

}

   /* public void Encrypt(string newpassword) {
        using (var db = new SQLiteConnection(_dbConnection, "")) {
            //succeeds
            int records = db.ExecuteScalar<int>("select count(*) from sqlite_master;");
            db.Close();
        }
    }*/

    @Override
    protected void onResume() {
        decryptDatabase();
        super.onResume();
    }

    @Override
    protected void onStop() {

            encrypt();
        super.onStop();
    }

}