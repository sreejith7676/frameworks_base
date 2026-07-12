package com.android.server;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.SystemService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HideAppListService extends SystemService {
    private static final String TAG = "HideAppListService";
    private final Context mContext;

    public HideAppListService(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        Slog.i(TAG, "Starting HideAppListService");
    }

    @Override
    public void onBootPhase(int phase) {
        if (phase == SystemService.PHASE_BOOT_COMPLETED) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
            filter.addDataScheme("package");
            mContext.registerReceiver(new PackageUninstallReceiver(), filter);
        }
    }

    private class PackageUninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName != null) {
                Slog.i(TAG, "Package uninstalled: " + packageName);
                removeFromHideAppList(packageName);
            }
        }
    }

    private void removeFromHideAppList(String packageName) {
        ContentResolver cr = mContext.getContentResolver();
        String apps = Settings.Secure.getString(cr, Settings.Secure.HIDE_APPLIST);

        if (apps == null || apps.isEmpty() || apps.equals(",")) return;

        Set<String> appSet = new HashSet<>(Arrays.asList(apps.split(",")));
        if (appSet.remove(packageName)) {
            Slog.i(TAG, "Removing package due to reason: UNINSTALLED: " + packageName);
            Settings.Secure.putString(cr, Settings.Secure.HIDE_APPLIST, String.join(",", appSet));
        }
    }
}
