package Share;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    Context context;
    private static final String CREATE_BILLITEM_SQL=
            "create table BillItem (" +
                    "id integer primary key autoincrement,"+
                    "year integer," +
                    "month integer," +
                    "day integer," +
                    "amount real," +
                    "mainType var(20)," +
                    "subType var(20)," +
                    "description var(100)"+
                    ");";
    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BILLITEM_SQL);
    }

}
