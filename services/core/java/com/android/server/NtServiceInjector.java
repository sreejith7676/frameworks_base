package com.android.server;

import android.content.Context;
import com.android.server.am.ActivityManagerService;
import com.android.server.pm.PackageManagerService;
import com.android.server.wm.ActivityTaskManagerService;
import com.android.server.wm.WindowManagerService;

public class NtServiceInjector {
    private static NtServiceInjector instance = null;
    private ActivityManagerService mService;
    private Context ctx;
    private WindowManagerService mWindowService;
    private PackageManagerService mPackageService;

    private NtServiceInjector() {}

    public static synchronized NtServiceInjector get() {
        if (instance == null) {
            instance = new NtServiceInjector();
        }
        return instance;
    }

    void setCtx(Context context) { ctx = context; }
    void setActivityManagerService(ActivityManagerService activityManagerService) { mService = activityManagerService; }
    void setWindowManagerService(WindowManagerService windowManagerService) { mWindowService = windowManagerService; }
    void setPackageManagerService(PackageManagerService pm) { mPackageService = pm; }

    public WindowManagerService getWindowManagerService() { return mWindowService; }
    public ActivityTaskManagerService getActivityTaskManagerService() { return mService.mActivityTaskManager; }
    public ActivityManagerService getActivityManagerService() { return mService; }
    public PackageManagerService getPackageManagerService() { return mPackageService; }
    public Context getContext() { return ctx; }

    public static Context getCtx() { return get().getContext(); }
    public static WindowManagerService getWm() { return get().getWindowManagerService(); }
    public static ActivityManagerService getAm() { return get().getActivityManagerService(); }
    public static PackageManagerService getPm() { return get().getPackageManagerService(); }
}
