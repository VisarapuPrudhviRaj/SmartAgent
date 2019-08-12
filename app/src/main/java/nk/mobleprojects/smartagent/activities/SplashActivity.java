package nk.mobleprojects.smartagent.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import nk.mobleprojects.smartagent.BaseActivity;
import nk.mobleprojects.smartagent.R;
import nk.mobleprojects.smartagent.utils.PermissionResult;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions();

    }

    private void permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            askCompactPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    Intent i = new Intent(SplashActivity.this, SmartAgentActivity.class);
                    startActivity(i);
                    finish();


                }

                @Override
                public void permissionDenied() {
                    Toast.makeText(SplashActivity.this, "Accept Permissions!", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(SplashActivity.this, "Accept Permissions!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, SmartAgentActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }
    }
}
