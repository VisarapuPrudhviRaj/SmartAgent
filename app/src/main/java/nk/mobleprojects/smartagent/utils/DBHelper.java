package nk.mobleprojects.smartagent.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteHelper {


    public static final String DB_NAME = "smartprojct";
    private static final String TAG = "DBHelper";
    private static final int DB_VERSION = 1;
    Context mContext;


    public DBHelper(Context context) {
        super(context, DB_NAME, DB_VERSION);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        CreateTablesHere(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        switch (oldVersion) {

            case 0:

            default:
                break;
        }
    }


    private void CreateTablesHere(SQLiteDatabase db) {
        db.execSQL(CreateTableStringByID(DBTables.SmartProject.TABLE_NAME,
                UID, DBTables.SmartProject.cols));

    }


    public List<List<String>> cursorToListArrary(Cursor c) {
        List<List<String>> rowList = new ArrayList<List<String>>();
        while (c.moveToNext()) {
            List<String> arr = new ArrayList<String>();
            for (int i = 0; i < c.getColumnCount(); i++) {
                arr.add(c.getString(i));
            }
            rowList.add(arr);
        }
        return rowList;
    }






}
