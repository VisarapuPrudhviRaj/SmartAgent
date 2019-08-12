package nk.mobleprojects.smartagent.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nk.mobleprojects.smartagent.BaseActivity;
import nk.mobleprojects.smartagent.R;
import nk.mobleprojects.smartagent.adapter.SmartAgentAdapter;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.Helper;

public class SmartAgentActivity extends BaseActivity {

    DBHelper dbHelper;

    RecyclerView rv_view;
    List<SmartAgentPojo> list = new ArrayList<>();
    SmartAgentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_agent);
        setToolBar("SmartAgent App", "Files");
        dbHelper = new DBHelper(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_view = (RecyclerView) findViewById(R.id.rv_view);
        rv_view.setLayoutManager(linearLayoutManager);
        adapter = new SmartAgentAdapter(SmartAgentActivity.this, list);
        rv_view.setAdapter(adapter);

        if (Helper.isNetworkAvailable(this)) {
            serverHit();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String checkFileExist(String fileName) {

        File sdcard = new File(Environment.getExternalStorageDirectory() + "/" + Helper.FOLDER_NAME);
        if (!sdcard.exists()) {
            sdcard.mkdir();
        }
        File file = new File(sdcard, fileName);
        if (file.exists()) {

            return file.getAbsolutePath();

        } else {
            return "";
        }
    }

    private void serverHit() {
        showProgressDialog("Please Wait ...");
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Helper.url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        closeProgressDialog();
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("dependencies");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject result = jsonArray.getJSONObject(i);

                                if (dbHelper.getCountByValue(DBTables.SmartProject.TABLE_NAME, DBTables.SmartProject.id, result.getString("id")) == 0) {
                                    //file_name: SD check
                                    String file_path = checkFileExist(result.getString("name"));
                                    if (file_path.equals("")) {

                                        dbHelper.insertintoTable(DBTables.SmartProject.TABLE_NAME,
                                                DBTables.SmartProject.cols, new String[]{result.getString("id"),
                                                        result.getString("name"), result.getString("type"),
                                                        result.getString("sizeInBytes"), result.getString("cdn_path"), "", "0"});
                                    } else {
                                        dbHelper.insertintoTable(DBTables.SmartProject.TABLE_NAME,
                                                DBTables.SmartProject.cols, new String[]{result.getString("id"),
                                                        result.getString("name"), result.getString("type"),
                                                        result.getString("sizeInBytes"), result.getString("cdn_path"),file_path , "1"});
                                    }


                                }


                            }

                            loadData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        closeProgressDialog();
                    }
                }
        );

// add it to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getRequest);
    }

    private void loadData() {
        list.clear();
        List<List<String>> db_data = dbHelper.getTableData(DBTables.SmartProject.TABLE_NAME);

        if (db_data.size() > 0) {
            for (int i = 0; i < db_data.size(); i++) {
                SmartAgentPojo smartAgentPojo = new SmartAgentPojo();
                smartAgentPojo.setId(db_data.get(i).get(1));
                smartAgentPojo.setName(db_data.get(i).get(2));
                smartAgentPojo.setType(db_data.get(i).get(3));
                smartAgentPojo.setSizeInBytes(db_data.get(i).get(4));
                smartAgentPojo.setCdn_path(db_data.get(i).get(5));
                smartAgentPojo.setFilePath(db_data.get(i).get(6));
                smartAgentPojo.setDownloadStatus(db_data.get(i).get(7));
                list.add(smartAgentPojo);
            }
            adapter.notifyDataSetChanged();
        }
    }

}
