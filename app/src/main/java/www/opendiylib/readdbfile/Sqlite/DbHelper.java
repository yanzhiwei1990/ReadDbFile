package www.opendiylib.readdbfile.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private final static String TAG = DbHelper.class.getSimpleName();

    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "user_data";
    private static final String CLEAR_TABLE_DATA = "delete from ";
    private static final String DROP_TABLE = "drop table if exists ";
    private static DbHelper instance = null;
    private static SQLiteDatabase db = null;
    private Context mContext = null;

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table usertable(_id integer primary key autoincrement,name text,number text)";
        db.execSQL(sql);
        Log.d(TAG, "create Database------------->");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "update Database-------------> oldVersion = " + oldVersion + ", newVersion = " + newVersion);
        db.execSQL(DROP_TABLE + TABLE_NAME);
    }

    private static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context,"user_data", null, DB_VERSION);
        }
        return instance;
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (db == null) {
            db = getInstance(context).getWritableDatabase();
        }
        return db;
    }

    public static void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }
}
