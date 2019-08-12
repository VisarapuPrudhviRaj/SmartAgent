package nk.mobleprojects.smartagent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nk.mobleprojects.smartagent.utils.PermissionResult;
import nk.mobleprojects.smartagent.utils.UnCaughtException;


public class BaseActivity extends AppCompatActivity {

    private final int KEY_PERMISSION = 999;
    private final String KEY_DATE = "DATE";


    public ProgressDialog pd;
    private PermissionResult permissionResult;
    private String permissionsAsk[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new
                UnCaughtException(BaseActivity.this, getString(R.string.app_name)));


    }


    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titleName);
        toolbar.setSubtitle(subtitle);


        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        setSupportActionBar(toolbar);

    }


    public boolean isPermissionGranted(Context context, String permission) {
        return ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED));
    }


    public boolean isPermissionsGranted(Context context, String permissions[]) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        boolean granted = true;
        for (String permission : permissions) {
            if (!(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED))
                granted = false;
        }
        return granted;
    }

    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        System.out.println("Total:" + permissionAsk.length);
        for (int i = 0; i < permissionAsk.length; i++) {
            System.out.println("ASking:" + permissionAsk[i].toString());
            if (!isPermissionGranted(BaseActivity.this, permissionAsk[i])) {
                System.out.println(i + ": Not Granted");
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }

        if (permissionsNotGranted.isEmpty()) {
            if (permissionResult != null)
                permissionResult.permissionGranted();
        } else {
            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            ActivityCompat.requestPermissions(BaseActivity.this, arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != KEY_PERMISSION) {
            return;
        }

        List<String> permissionDenied = new LinkedList<>();
        boolean granted = true;
        System.out.println("Total grantResults:" + grantResults.length);
        for (int i = 0; i < grantResults.length; i++) {
            if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                System.out.println(i + ":Denied :" + grantResults[i] + ":" + permissions[i]);
                granted = false;
                permissionDenied.add(permissions[i]);
            }
        }

        if (permissionResult != null) {
            if (granted) {
                permissionResult.permissionGranted();
            } else {
                for (String s : permissionDenied) {
                    try {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                            permissionResult.permissionForeverDenied();
                            return;
                        }
                    } catch (Exception e) {
                        Toast.makeText(BaseActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        permissionResult.permissionDenied();
                    }

                }
                permissionResult.permissionDenied();
            }
        }
    }

    public void askCompactPermission(String permission, PermissionResult permissionResult) {
        permissionsAsk = new String[]{permission};
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public void askCompactPermissions(String permissions[], PermissionResult permissionResult) {
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }


    public void openSettingsApp(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void showProgressDialog(String msg) {
        try {
            pd = new ProgressDialog(this);
            // pd = CustomProgressDialog.ctor(this, msg);
            // pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            pd.setMessage(msg);
            pd.setCancelable(true);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeProgressDialog() {
        try {
            if (pd != null)
                pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

