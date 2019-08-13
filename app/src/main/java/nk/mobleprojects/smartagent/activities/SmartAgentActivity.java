package nk.mobleprojects.smartagent.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import nk.mobleprojects.smartagent.bottomsheet.BottomSheetFileSizeFragment;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;
import nk.mobleprojects.smartagent.service.SmartAgentService;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.Helper;

public class SmartAgentActivity extends BaseActivity implements SmartAgentAdapter.ViewDownloadListener {

    DBHelper dbHelper;

    RecyclerView rv_view;
    List<SmartAgentPojo> list = new ArrayList<>();
    SmartAgentAdapter adapter;

    Intent mServiceIntent;
    BroadcastReceiver broadcastReceiver;
    private SmartAgentService smartAgentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_agent);
        setToolBar("SmartAgent App", "Files");
        dbHelper = new DBHelper(this);


        smartAgentService = new SmartAgentService(this);
        mServiceIntent = new Intent(this, smartAgentService.getClass());
        startBackgroundService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_view = (RecyclerView) findViewById(R.id.rv_view);
        rv_view.setLayoutManager(linearLayoutManager);
        adapter = new SmartAgentAdapter(SmartAgentActivity.this, list, this);
        rv_view.setAdapter(adapter);

        if (Helper.isNetworkAvailable(this)) {
            serverHit();

        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void startBackgroundService() {
        if (!isMyServiceRunning(smartAgentService.getClass())) {
            startService(mServiceIntent);
        } else {
            // startService(mServiceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Downloaded");
                if (status.trim().equals("DONE")) {
                    loadData();
                } else {
                    adapter.notifyDataSetChanged();
                }
                startBackgroundService();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Helper.FILEDOWLOADED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("onDestroy", "Service Stopped!");
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<? extends SmartAgentService> aClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                serverHit();
                break;
            default:
                break;
        }
        return true;
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
                                                        result.getString("sizeInBytes"), result.getString("cdn_path"), file_path, "1"});
                                    }


                                } else {

                                    String file_path = checkFileExist(result.getString("name"));
                                    if (file_path.trim().equals("")) {
                                        dbHelper.updateByValues(DBTables.SmartProject.TABLE_NAME,
                                                new String[]{DBTables.SmartProject.downloadStatus, DBTables.SmartProject.filePath},
                                                new String[]{"0", ""}
                                                , new String[]{DBTables.SmartProject.id}, new String[]{result.getString("id")});
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

    @Override
    public void viewDownClickListner(SmartAgentPojo agentPojo) {

        BottomSheetFileSizeFragment bottomSheetFileSizeFragment = new BottomSheetFileSizeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("filePath", agentPojo);
        bottomSheetFileSizeFragment.setArguments(bundle);
        bottomSheetFileSizeFragment.show(getSupportFragmentManager(), "bottomSheetFragment");

    }
}
