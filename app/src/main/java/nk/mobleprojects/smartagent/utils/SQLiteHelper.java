package nk.mobleprojects.smartagent.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String UID = "UID";
    private static final String TAG = "SQLiteHelper";
    private static final String ASSET_DB_PATH = "databases";

    private final String DB_NAME;
    private final int DB_VERSION;
    private final Context context;


    public SQLiteHelper(Context context, String DB_NAME, int DB_VERSION) {
        super(context, DB_NAME, null, DB_VERSION);
        this.DB_NAME = DB_NAME;
        this.DB_VERSION = DB_VERSION;
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        System.out.println("oldVersion:" + oldVersion);


    }

    // insertion data

    /**
     * This method is used to insert data into local data base i.e.,SQLite database.
     *
     * @param tableName-name  of table.
     * @param colNames-column names for that table.
     * @param colVals-column  values corresponding to that columns in that table.
     * @return
     */
    public long insertintoTable(String tableName, String[] colNames,
                                String[] colVals) {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < colNames.length; i++) {
                cv.put(colNames[i], colVals[i]);
            }
            result = db.insert(tableName, null, cv);
        } catch (Exception e) {
            return result;
        } finally {
            db.close();
            return result;
        }


    }



    /**
     * This method is used to get table data by passing table name.
     *
     * @param TableName-name of the table from where we want data.
     * @return
     */
    public List<List<String>> getTableData(String TableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TableName, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get table column data by passing condition.
     *
     * @param TableName-name          of the table.
     * @param outColNamesByComma-name of the columns with comma separation.
     * @param wherecolnames-column    names to pass in where condition.
     * @param wherecolumnValue-column values to pass in where condition.
     * @return
     */
    public List<List<String>> getTableColDataByCond(String TableName,
                                                    String outColNamesByComma, String wherecolnames[],
                                                    String wherecolumnValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE ";
        for (int k = 0; k < wherecolnames.length; k++)
            query = query + wherecolnames[k] + "='" + wherecolumnValue[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        // //System.out.println("Final Query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method will return the list of rows in that table.
     *
     * @param c-cursor name
     * @return
     */
    public List<List<String>> cursorToListArr(Cursor c) {
        List<List<String>> rowList = new ArrayList<List<String>>();
        while (c.moveToNext()) {
            List<String> arr = new ArrayList<String>();
            for (int i = 0; i < c.getColumnCount(); i++) {
                arr.add(c.getString(i));
            }
            rowList.add(arr);
        }
        c.close();
        return rowList;
    }
    // get data from n number of tables


    /**
     * This method is used to update data by using values.
     *
     * @param tablename-name      of the table.
     * @param columnNames-column  names with comma separation.
     * @param columnValues-column values with comma separation.
     * @param whereColumn-column  name to pass in where condition.
     * @param whereValue-column   value to pass in where condition.
     * @return
     */
    public boolean updateByValues(String tablename, String[] columnNames,
                                  String[] columnValues, String whereColumn[], String whereValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnNames.length; i++) {
            cv.put(columnNames[i], columnValues[i]);
        }
        String query = "";
        for (int i = 0; i < whereColumn.length; i++)
            query = query + whereColumn[i] + "='" + whereValue[i] + "' AND ";
        query = query.substring(0, query.length() - 5);
        System.out.println("query:" + query);
        boolean flag = db.update(tablename, cv, query, null) > 0;
        db.close();

        return flag;
    }
    // checks the value already there in the table


    /**
     * This method is used to get total records count in that table in corresponding to the column.
     *
     * @param tableName-name  of the Table.
     * @param colName-name    of the column.
     * @param colValue-column value.
     * @return
     */
    public int getCountByValue(String tableName, String colName, String colValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE "
                + colName + "='" + colValue + "'";
        // System.out.println("CountQuery:" + query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }


    /**
     * This method create a table with all column types are TEXT Except ID As Primary key.
     *
     * @param tablename-name        of the Table.
     * @param primarycolumn-primary column.
     * @param restcolumns-rest      columns.
     * @return
     */
    public String CreateTableStringByID(String tablename, String primarycolumn,
                                        String[] restcolumns) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + tablename + " ("
                + primarycolumn + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        for (String data : restcolumns) {
            CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
        }
        CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
                + ")";
        return CREATE_TABLE;
    }


}
