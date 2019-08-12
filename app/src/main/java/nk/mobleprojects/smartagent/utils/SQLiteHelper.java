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

/**
 * Created by nagendra on 22/3/17.
 */


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


    //sample bulkinsert
    public boolean bulkInsertRecords(String tableName, JSONArray jsonAPlants, String habCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + tableName + "(hab_Code,plant_UId,plant_GPS,plant_Id,plant_Name,plant_imageurl,plant_Remark,PlantSurvival) VALUES (?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        try {
            for (int i = 0; i < jsonAPlants.length(); i++) {
                JSONObject jsonObjplant = jsonAPlants.getJSONObject(i);
                statement.clearBindings();
                statement.bindString(1, habCode);
                statement.bindString(2, jsonObjplant.getString("UId").toString().trim());
                statement.bindString(3, jsonObjplant.getString("GPS").toString().trim());
                statement.bindString(4, jsonObjplant.getString("PlantId").toString().trim());
                statement.bindString(5, jsonObjplant.getString("PlantName").toString().trim());
                statement.bindString(6, jsonObjplant.getString("ImageUrl").toString().trim());
                statement.bindString(7, jsonObjplant.getString("Remark").toString().trim());
                statement.bindString(8, jsonObjplant.getString("PlantSurvival").toString().trim());
                statement.execute();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
            db.close();
            return true;
        }
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

    public long insertintoTableWithImages(String tableName, String[] colNames, String[] colImgs,
                                          String[] colVals, List<byte[]> imgVals) {
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < colNames.length; i++) {
                cv.put(colNames[i], colVals[i]);
            }
            for (int i = 0; i < colImgs.length; i++) {
                cv.put(colImgs[i], imgVals.get(i));
            }
            result = db.insert(tableName, null, cv);
        } catch (Exception e) {
            return result;
        } finally {
            db.close();
            return result;
        }


    }
    // get table rows


    /**
     * This method is used to insert data into local data base i.e.,SQLite database.
     *
     * @param query-string insert string.
     * @return
     */
    public boolean insertintoTable(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(query);
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
            return true;
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
     * This method is used to get table column data by passing table name and column names.
     *
     * @param TableName-name            of the Table
     * @param outColNamesByComma-column names with comma seperation.
     * @return
     */
    public List<List<String>> getTableColData(String TableName,
                                              String outColNamesByComma) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT " + outColNamesByComma + " FROM "
                + TableName, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get content values by passing column names and values.
     *
     * @param colNames-name of the column names with comma separation.
     * @param values-values with comma separation.
     * @return
     */
    public ContentValues getContentValues(String[] colNames, String[] values) {
        ContentValues cv = new ContentValues();
        for (int i = 0; i < colNames.length; i++) {
            cv.put(colNames[i], values[i]);
        }
        return cv;
    }
    // get table rows

    /**
     * This method is used to get whole table data by passing condition.
     *
     * @param TableName-name          of the table
     * @param wherecolnames-column    names for passing in where condition.
     * @param wherecolumnValue-column value for passing in where condition.
     * @return
     */
    public List<List<String>> getTableDataByCond(String TableName,
                                                 String wherecolnames[], String wherecolumnValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName + " WHERE ";
        for (int k = 0; k < wherecolnames.length; k++)
            query = query + wherecolnames[k] + "='" + wherecolumnValue[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        System.out.println("Final Query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get record with single array by passing query.
     *
     * @param query-query to get record list.
     * @return
     */
    public String[] getRecordWithSingleArray(String query) {
        String records[] = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean recordsStatus = true;

        if (cursor != null) {
            int colCount = cursor.getColumnCount();
            records = new String[colCount];
            while (cursor.moveToNext()) {
                recordsStatus = false;
                for (int col = 0; col < colCount; col++) {
                    records[col] = cursor.getString(col);
                    System.out.println("record: " + records[col]);
                }
            }
        }
        cursor.close();
        db.close();
        if (recordsStatus) {
            records = null;
        }

        return records;
    }
    // get table rows

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
     * This methos is used to get data form n number of tables by using join condition.
     *
     * @param context-Object  of Context, context from where the activity is going
     *                        to start.
     * @param dColTab1-column names of the first table.
     * @param dColTab2-column names of the second table.
     * @param tableNames-name of the tables.
     * @param equiTCols1-equi columns of the table1.
     * @param equiTCols2-equi columns of the table2.
     * @param jointype-join   type
     * @return
     */
    public List<List<String>> getLeftJoinDataFromTwoTables(Context context,
                                                           String[] dColTab1, String[] dColTab2, String[] tableNames,
                                                           String[] equiTCols1, String[] equiTCols2, String jointype) {
        List<List<String>> list = new ArrayList<List<String>>();
        try {
            String alisT[] = {"a", "b"};

            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT distinct ";
            for (int g = 0; g < dColTab1.length; g++)
                query = query + "a." + dColTab1[g] + ",";
            for (int g = 0; g < dColTab2.length; g++)
                query = query + "ifnull(b." + dColTab2[g] + ",'NA'),";
            query = query.substring(0, query.length() - 1) + " FROM ";
            for (int g = 0; g < tableNames.length; g++)
                query = query + tableNames[g] + " " + alisT[g] + " " + jointype
                        + " join ";

            query = query.substring(0, query.lastIndexOf(jointype + " join "));
            query = query.substring(0, query.length() - 1);
            query = query + " ON ";
            for (int g = 0; g < equiTCols1.length; g++)
                query = query + "a." + equiTCols1[g] + "=b." + equiTCols2[g]
                        + " AND ";
            query = query.substring(0, query.length() - 5);
            System.out.println("Final Query:" + query);
            Cursor cur = db.rawQuery(query, null);
            if (cur.getCount() > 0)
                list = cursorToListArr(cur);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    // DELETE

    /**
     * This method is used to delete data by using values.
     *
     * @param tablename-name           of the Table.
     * @param wherecolumnNames-column  names to pass in where condition.
     * @param wherecolumnValues-column values to pass in where condition.
     * @return
     */
    public boolean deleteByValues(String tablename, String[] wherecolumnNames,
                                  String[] wherecolumnValues) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cond = "";
        for (int i = 0; i < wherecolumnNames.length; i++) {
            cond = cond + wherecolumnNames[i] + " = '" + wherecolumnValues[i]
                    + "' AND ";
        }
        cond = cond.substring(0, cond.length() - 5);
        boolean flag = db.delete(tablename, cond, null) > 0;
        // System.out.println("deleted?" + flag);
        db.close();
        return flag;
    }

    /**
     * This method is used to delete data by passing table name and query.
     *
     * @param tablename-name of the table.
     * @param Query-query
     * @return
     */
    public boolean deleteByQuery(String tablename, String Query) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tablename, Query, null) > 0;
        // System.out.println("deleted?" + flag);
        db.close();
        return flag;
    }

    /**
     * This method is used to delete whole table data by passing table name.
     *
     * @param tablename-name of the table.
     * @return
     */
    public boolean deleteAll(String tablename) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tablename, null, null) > 0;
        db.close();
        return flag;
    }

    /**
     * This method is used to update data by condition.
     *
     * @param tablename-name      of the table.
     * @param columnNames-column  names.
     * @param columnValues-column values.
     * @param Condition-condition to pass.
     * @return
     */
    public boolean updateByQuery(String tablename, String[] columnNames,
                                 String[] columnValues, String Condition) {
        // System.out.println("G:" + Condition);
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnNames.length; i++) {
            cv.put(columnNames[i], columnValues[i]);
        }
        boolean flag = db.update(tablename, cv, Condition, null) > 0;
        db.close();
        return flag;
    }

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
     * This method is used to check the value is already there in the table.
     *
     * @param tablename-name     eof the table.
     * @param columnname-name    of the column.
     * @param columnvalue-column value.
     * @return
     */
    public boolean checkAlreadyExistsByValue(String tablename,
                                             String columnname[], String columnvalue[]) {
        boolean value = false;
        String countQuery = "SELECT  * FROM " + tablename + " WHERE ";
        for (int k = 0; k < columnname.length; k++)
            countQuery = countQuery + columnname[k] + "='" + columnvalue[k]
                    + "' AND ";
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        // System.out.println("countQuery:" + countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        if (count > 0)
            value = true;
        cursor.close();
        db.close();
        // return count
        return value;
    }
    // checks the value already there int he table

    /**
     * This method is used to check exist record except the particular column.
     *
     * @param tablename-name           of the Table.
     * @param exceptcolumnname-column  name for except condition.
     * @param exceptcolumnvalue-column value for except condition.
     * @param columnname-column        name.
     * @param columnvalue-column       value.
     * @return
     */
    public boolean checkExceptExistRecordByValue(String tablename,
                                                 String exceptcolumnname[], String exceptcolumnvalue[],
                                                 String columnname[], String columnvalue[]) {
        boolean value = false;
        String countQuery = "SELECT  * FROM " + tablename + " WHERE ";
        // Except cols
        for (int k = 0; k < exceptcolumnname.length; k++)
            countQuery = countQuery + exceptcolumnname[k] + "<>'"
                    + exceptcolumnvalue[k] + "' AND ";

        for (int k = 0; k < columnname.length; k++)
            countQuery = countQuery + columnname[k] + "='" + columnvalue[k]
                    + "' AND ";
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        System.out.println("countQuery:" + countQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        if (count > 0)
            value = true;

        cursor.close();
        db.close();
        // return count
        return value;
    }

    /**
     * This method is used to get total records count in that table.
     *
     * @param tableName-name of the Table.
     * @return
     */
    public int getCount(String tableName) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }
    // not myne

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
     * This method is used to get total records count in that table except particular value
     *
     * @param tableName-name  of the table.
     * @param colName-name    of the column.
     * @param colValue-column value.
     * @return
     */
    public int getCountByNotValue(String tableName, String colName,
                                  String colValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName
                + " WHERE " + colName + "<>'" + colValue + "'", null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }

    /**
     * This method is used to get data by passing query.
     *
     * @param query-query to get data.
     * @return
     */
    public List<List<String>> getDataByQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get data by query as json object.
     *
     * @param query-query to get data.
     * @return
     */
    public List<JSONObject> getDataByQueryAsJObject(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(query, null);
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (cur.getCount() > 0)
            list = cursorToListJson(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get table data except the value we are passing.
     *
     * @param TableName-name     of the Table.
     * @param notcolumname-not   column name.
     * @param notcolumnValue-not column value.
     * @return
     */
    public List<List<String>> getTableDataByNotValue(String TableName,
                                                     String notcolumname, String notcolumnValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TableName + " WHERE "
                + notcolumname + "<>'" + notcolumnValue + "'", null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();

        return list;
    }

    /**
     * This method is used to get table column data except the value we are passing.
     *
     * @param TableName-name            of the Table.
     * @param columnNamesByComma-column names with comma separation.
     * @param notcolumname-not          column name.
     * @param notcolumnValue-not        column value.
     * @return
     */
    public List<List<String>> getTableColDataByNotValue(String TableName,
                                                        String columnNamesByComma, String notcolumname,
                                                        String notcolumnValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT " + columnNamesByComma + " FROM "
                + TableName + " WHERE " + notcolumname + "<>'" + notcolumnValue
                + "'", null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();

        return list;
    }
    // FETCH

    /**
     * This method is used to Table data by using AND Condition.
     *
     * @param TableName-name      of the Table.
     * @param condcolnames-column names to use as condition.
     * @param condcolValue-column value to use as condition.
     * @return
     */
    public List<List<String>> getTableDataByANDCond(String TableName,
                                                    String condcolnames[], String condcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName + " WHERE ";
        for (int k = 0; k < condcolnames.length; k++)
            query = query + condcolnames[k] + "='" + condcolValue[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }
    // FETCH

    /**
     * This method is used to Table column data by using AND Condition.
     *
     * @param TableName-name            of the Table.
     * @param outColNamesByComma-column names with comma separation.
     * @param condcolnames-column       names to use as condition.
     * @param condcolValue-column       value to use as condition.
     * @return
     */
    public List<List<String>> getTableColDataByANDCond(String TableName,
                                                       String outColNamesByComma, String condcolnames[],
                                                       String condcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE ";
        for (int k = 0; k < condcolnames.length; k++)
            query = query + condcolnames[k] + "='" + condcolValue[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }
    // FETCH

    /**
     * This method is used to get table data by OR Condition.
     *
     * @param TableName-name      of the Table.
     * @param condcolnames-column names for condition.
     * @param condcolValue-column values for condition.
     * @return
     */
    public List<List<String>> getTableDataByORCond(String TableName,
                                                   String condcolnames[], String condcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName + " WHERE ";
        for (int k = 0; k < condcolnames.length; k++)
            query = query + condcolnames[k] + "='" + condcolValue[k] + "'"
                    + " OR ";
        query = query.substring(0, query.length() - 4);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }
    // FETCH

    /**
     * This method is used to get Table column data by using OR condition.
     *
     * @param TableName-nam             eof the Table.
     * @param outColNamesByComma-column names with comma separation.
     * @param condcolnames-column       names for condition.
     * @param condcolValue-column       value for condition.
     * @return
     */
    public List<List<String>> getTableColDataByORCond(String TableName,
                                                      String outColNamesByComma, String condcolnames[],
                                                      String condcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE ";
        for (int k = 0; k < condcolnames.length; k++)
            query = query + condcolnames[k] + "='" + condcolValue[k] + "'"
                    + " OR ";
        query = query.substring(0, query.length() - 4);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get table data by using AND,OR conditions.
     *
     * @param TableName-name       of the Table.
     * @param andcolumnames-column names for AND condition,
     * @param andvalues-column     values for AND condition.
     * @param orcolnames-column    names for OR condition.
     * @param orcolValue-column    values for OR condition
     * @return
     */
    // FETCH
    public List<List<String>> getTableDataByANDORCond(String TableName,
                                                      String andcolumnames[], String andvalues[], String orcolnames[],
                                                      String orcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName + " WHERE ";
        for (int k = 0; k < andcolumnames.length; k++)
            query = query + andcolumnames[k] + "='" + andvalues[k] + "'"
                    + " AND ( ";
        for (int k = 0; k < orcolnames.length; k++)
            query = query + orcolnames[k] + "<>'" + orcolValue[k] + "'"
                    + " OR ";
        query = query.substring(0, query.length() - 4);
        query = query + " )";
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }
    // FETCH

    /**
     * This method is used to get table column data by passing AND,OR conditions.
     *
     * @param TableName-name            of the Table.
     * @param outColNamesByComma-column names with comma separation.
     * @param andcolumnames-column      names for AND condition,
     * @param andvalues-column          values for AND condition.
     * @param orcolnames-column         names for OR condition.
     * @param orcolValue-column         values for OR condition
     * @return
     */
    public List<List<String>> getTableColDataByANDORCond(String TableName,
                                                         String outColNamesByComma, String andcolumnames[],
                                                         String andvalues[], String orcolnames[], String orcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE ";

        for (int k = 0; k < andcolumnames.length; k++)
            query = query + andcolumnames[k] + "='" + andvalues[k] + "'"
                    + " AND ";

        query = query + "(";
        for (int k = 0; k < orcolnames.length; k++)
            query = query + orcolnames[k] + "='" + orcolValue[k] + "'" + " OR ";

        query = query.substring(0, query.length() - 4) + ")";

        System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * @param TableName
     * @param notcolumname
     * @param notvalues
     * @param eqlcolnames
     * @param eqlcolValue
     * @return
     */
    // FETCH
    public List<List<String>> getTableDataByNotANDEquiValues(String TableName,
                                                             String notcolumname[], String notvalues[], String eqlcolnames[],
                                                             String eqlcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TableName + " WHERE ";
        for (int k = 0; k < eqlcolnames.length; k++)
            query = query + eqlcolnames[k] + "='" + eqlcolValue[k] + "'"
                    + " AND ";
        for (int k = 0; k < notcolumname.length; k++)
            query = query + notcolumname[k] + "<>'" + notvalues[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * @param TableName
     * @param outColNamesyComma
     * @param notcolumname
     * @param notvalues
     * @param eqlcolnames
     * @param eqlcolValue
     * @return
     */
    // FETCH
    public List<List<String>> getTableColDataByNotANDEquiValues(
            String TableName, String outColNamesyComma, String notcolumname[],
            String notvalues[], String eqlcolnames[], String eqlcolValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesyComma + " FROM " + TableName
                + " WHERE ";
        for (int k = 0; k < eqlcolnames.length; k++)
            query = query + eqlcolnames[k] + "='" + eqlcolValue[k] + "'"
                    + " AND ";
        for (int k = 0; k < notcolumname.length; k++)
            query = query + notcolumname[k] + "<>'" + notvalues[k] + "'"
                    + " AND ";
        query = query.substring(0, query.length() - 5);
        // System.out.println("query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method returns values of a based on column value we pass.
     *
     * @param tablename-nma       eof the table.
     * @param outcolumname-column names.
     * @return
     */
    public String getTabCol(String tablename, String outcolumname) {
        String countQuery = "SELECT " + outcolumname + " FROM " + tablename;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String SID = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SID = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return SID;
    }

    public String getTabColCond(String tablename, String outcolumname, String wherecol, String whereVal) {
        String countQuery = "SELECT " + outcolumname + " FROM " + tablename + " where " + wherecol + "='" + whereVal+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String SID = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SID = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return SID;
    }

    /**
     * This method returns values of table based on column value we pass
     *
     * @param tablename-nam             eof the Table.
     * @param outcolumname-column       names.
     * @param condcolumname-condition   column name.
     * @param condcolumnvalue-condition column value.
     * @return
     */
    // it
    public String getValueByIds(String tablename, String outcolumname,
                                String[] condcolumname, String[] condcolumnvalue) {
        String countQuery = "SELECT " + outcolumname + " FROM " + tablename
                + " WHERE ";
        for (int i = 0; i < condcolumname.length; i++) {
            countQuery += condcolumname[i] + "='" + condcolumnvalue[i]
                    + "' AND ";
        }
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        String SID = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SID = cursor.getString(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return SID;
    }

    /**
     * This method is used to get single column data in that table.
     *
     * @param tableName-name            of the Table.
     * @param outColName-column         name.
     * @param condcolumname-condition   column name.
     * @param condcolumnvalue-condition column value.
     * @return
     */
    public List<String> getTabSingleColumnData(String tableName,
                                               String outColName, String[] condcolumname, String[] condcolumnvalue) {
        List<String> ldbrow = new ArrayList<String>();
        String countQuery = "SELECT " + outColName + " FROM " + tableName
                + " WHERE ";
        for (int i = 0; i < condcolumname.length; i++) {
            countQuery += condcolumname[i] + "='" + condcolumnvalue[i]
                    + "' AND ";
        }
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ldbrow.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ldbrow;
    }

    /**
     * This method create a table with all column types are TEXT.
     *
     * @param tablename-table    name.
     * @param columnnames-column names.
     * @return
     */
    public String CreateTableString(String tablename, String[] columnnames) {
        String CREATE_TABLE = "CREATE TABLE " + tablename + " (";
        for (String data : columnnames) {
            CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
        }
        CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
                + ")";
        return CREATE_TABLE;
    }
    // auto incrment

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

    public String CreateTableStringByIDWithImage(String tablename, String primarycolumn,
                                                 String[] restcolumns, String[] imagecolums) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + tablename + " ("
                + primarycolumn + " INTEGER PRIMARY KEY AUTOINCREMENT,";
        for (String data : restcolumns) {
            CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
        }

        for (String data : imagecolums) {
            CREATE_TABLE = CREATE_TABLE + data + " BLOB,";
        }
        CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
                + ")";
        return CREATE_TABLE;
    }

    /**
     * This method  create a table with manual column name and column type using hash Map.
     *
     * @param tablename-name of the Table.
     * @param hm-hash        map
     * @return
     */
    public String CreateTableByIDValue(String tablename,
                                       HashMap<String, String> hm) {
        String CREATE_TABLE = "CREATE TABLE " + tablename + " (";
        Iterator itr = hm.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pairs = (Map.Entry) itr.next();
            CREATE_TABLE = CREATE_TABLE + pairs.getKey() + " "
                    + pairs.getValue() + ",";
        }
        CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
                + ")";
        return CREATE_TABLE;
    }

    /**
     * This is used to convert cursor to list array.
     *
     * @param c-cursor
     * @return
     */
    // /////////// convertors
    public List<String> cursorToList(Cursor c) {
        List<String> al = new ArrayList<String>();
        if (c != null) {
            while (c.moveToNext()) {
                al.add(c.getString(0));
            }
            c.close();
        }
        return al;
    }
    // store table data in file.
    // it returns values of a based on column value we pass.

    /**
     * This method is used to convert cursor to list JSON Array.
     *
     * @param c-cursor.
     * @return
     */
    public List<JSONObject> cursorToListJson(Cursor c) {
        List<JSONObject> rowList = new ArrayList<JSONObject>();
        JSONObject jobjtemp = null;
        while (c.moveToNext()) {

            for (int i = 0; i < c.getColumnCount(); i++) {
                jobjtemp = new JSONObject();
                try {
                    jobjtemp.put(c.getColumnName(i), c.getString(i));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            rowList.add(jobjtemp);
        }
        c.close();
        return rowList;
    }

    /**
     * This method is used to delete row data which we pass.
     *
     * @param context-Object  of Context, context from where the activity is going
     *                        to start.
     * @param tablename-name  of the Table.
     * @param columnName-name of the Column.
     * @param value-column    value.
     * @return
     */
    public boolean deleteRows(Context context, final String tablename,
                              String columnName, String value) {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tablename, columnName + "='" + value + "'",
                null) > 0;
        db.close();
        return flag;
    }

    /**
     * This method is used to delete particular row data.
     *
     * @param context-Object  of Context, context from where the activity is going
     *                        to start.
     * @param tablename-name  of the Table.
     * @param columnName-name of the column.
     * @param value-column    value.
     * @return
     */
    public boolean deleteRowData(Context context,
                                 final String tablename, String[] columnName, String[] value) {

        SQLiteDatabase db = this.getReadableDatabase();
        String whereClause = "";
        for (int k = 0; k < columnName.length; k++) {
            whereClause = whereClause + columnName[k] + "='" + value[k] + "'"
                    + " AND ";
        }
        whereClause = whereClause.substring(0, whereClause.length() - 5);

        boolean flag = db.delete(tablename, whereClause, null) > 0;
        // System.out.println("flag in database:" + flag);
        db.close();
        return flag;

    }

    /**
     * This method is used to delete data of all rows.
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param tablename-name of the Table.
     * @return
     */
    public boolean deleteAllRows(Context context, final String tablename) {

        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tablename, null, null) > 0;
        db.close();
        return flag;

    }

    /**
     * This method is used to update data of a particular row.
     *
     * @param context-Object      of Context, context from where the activity is going
     *                            to start.
     * @param tablename-name      of the Table.
     * @param columnNames-column  names.
     * @param columnValues-column values.
     * @param whereColumn-where   column name.
     * @param whereValue-where    column value.
     * @return
     */
    public boolean updateRowData(Context context,
                                 final String tablename, String[] columnNames,
                                 String[] columnValues, String[] whereColumn, String[] whereValue) {

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnNames.length; i++) {
            cv.put(columnNames[i], columnValues[i]);
        }
        boolean flag = false;
        if (whereColumn != null) {
            String whereClause = "";
            for (int k = 0; k < whereColumn.length; k++) {
                whereClause = whereClause + whereColumn[k] + "='"
                        + whereValue[k] + "'" + " AND ";
            }
            whereClause = whereClause.substring(0, whereClause.length() - 5);
            System.out.println("whereclause......" + whereClause);
            flag = db.update(tablename, cv, whereClause, null) > 0;
        } else {
            flag = db.update(tablename, cv, null, null) > 0;
        }

        db.close();
        System.out.println("flag.in updating ................." + flag);
        return flag;
    }

    /**
     * This method is used to get table column data by condition and using like.
     *
     * @param TableName-name            of the Table.
     * @param outColNamesByComma-column names with comma separation.
     * @param wherecolnames-where       column names.
     * @param wherecolumnValue-where    column values.
     * @return
     */
    public List<List<String>> getTableColDataByCondLike(String TableName,
                                                        String outColNamesByComma, String wherecolnames,
                                                        String wherecolumnValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE " + wherecolnames + " like '%" + wherecolumnValue
                + "%'";

        System.out.println(query);
        // query = query.substring(0, query.length() - 5);
        System.out.println("Final Query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get table column data by condition and ORDERBY
     *
     * @param TableName-name            of the Table.
     * @param outColNamesByComma-column nmaes with comma separation.
     * @param wherecolnames-where       column names.
     * @param wherecolumnValue-where    column values.
     * @param OrderBy-ORDER             BY CONDITION to get data in order
     * @return
     */
    public List<List<String>> getTableColDataByCondORDEYBY(String TableName,
                                                           String outColNamesByComma, String wherecolnames,
                                                           String wherecolumnValue, String OrderBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM " + TableName
                + " WHERE " + wherecolnames + " like '%" + wherecolumnValue
                + "%'" + " order by " + OrderBy;

        System.out.println(query);

        // query = query.substring(0, query.length() - 5);
        System.out.println("Final Query:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    /**
     * This method is used to get table column data by where condition and other condition.
     *
     * @param TableName-name            of the Table.
     * @param outColNamesByComma-column names with comma separation.
     * @param wherecolnames-where       column names.
     * @param wherecolumnValue-where    column values.
     * @param OtherWherecondition-other where condition.
     * @return
     */
    public List<List<String>> getTableDataByCondS(String TableName, String outColNamesByComma,
                                                  String wherecolnames[],
                                                  String wherecolumnValue[], String OtherWherecondition) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + outColNamesByComma + " FROM "
                + TableName + " WHERE ";

        if (wherecolnames.length > 0) {
            for (int k = 0; k < wherecolnames.length; k++) {
                query = query + wherecolnames[k] + "='" + wherecolumnValue[k] + "'"
                        + " AND ";
            }
            query = query.substring(0, query.length() - 5);
        }

        if (OtherWherecondition.trim().length() > 0) {
            query = query + OtherWherecondition;
        }

        System.out.println("Final Query by cond IN:" + query);
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }


}
