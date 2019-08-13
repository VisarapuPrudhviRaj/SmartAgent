package nk.mobleprojects.smartagent.broadcastrecevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nk.mobleprojects.smartagent.service.SmartAgentService;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SmartAgentService.class));;
    }
}
