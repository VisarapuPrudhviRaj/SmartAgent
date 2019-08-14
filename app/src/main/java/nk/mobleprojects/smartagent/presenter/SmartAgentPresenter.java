package nk.mobleprojects.smartagent.presenter;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nk.mobleprojects.smartagent.BuildConfig;
import nk.mobleprojects.smartagent.model.SmartAgentPojo;
import nk.mobleprojects.smartagent.service.SmartAgentService;
import nk.mobleprojects.smartagent.utils.DBHelper;
import nk.mobleprojects.smartagent.utils.DBTables;
import nk.mobleprojects.smartagent.utils.Helper;
import nk.mobleprojects.smartagent.view.SmartAgentView;

public class SmartAgentPresenter {

    DBHelper dbHelper;
    Context context;
    private SmartAgentView smartAgentView;


    public SmartAgentPresenter(Context context, SmartAgentView smartAgentView) {
        this.context = context;
        this.smartAgentView = smartAgentView;
        dbHelper = new DBHelper(context);
    }

    public String checkFileExist(String fileName) {

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




    public void setData(JSONObject response){

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
            smartAgentView.loadData();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
