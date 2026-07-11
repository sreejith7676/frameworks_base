package com.android.server.spoof;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.server.NtServiceInjector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AxSpoofManager implements IAxSpoofManager {
    private static final String TAG = "AxSpoofManager";
    private static final String[] WATCHED_KEYS = {
            Settings.Secure.SPOOF_GAMEPROPS_CONFIG,
            Settings.Secure.SPOOF_PIF_PHOTOS
    };

    private final Map<String, String> mCache = new ConcurrentHashMap<>();
    private final Handler mHandler;
    private ContentResolver mResolver;

    public AxSpoofManager() {
        HandlerThread thread = new HandlerThread("AxSpoofManager");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    @Override
    public void systemReady() {
        Context context = NtServiceInjector.getCtx();
        if (context == null) return;
        mResolver = context.getContentResolver();

        for (String key : WATCHED_KEYS) refreshKey(key);

        ContentObserver observer = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (uri != null && uri.getLastPathSegment() != null) {
                    refreshKey(uri.getLastPathSegment());
                }
            }
        };
        for (String key : WATCHED_KEYS) {
            mResolver.registerContentObserver(Settings.Secure.getUriFor(key), false, observer, UserHandle.USER_ALL);
        }
        Log.i(TAG, "AxSpoofManager ready (Games & Photos)");
    }

    private void refreshKey(String key) {
        if (mResolver == null) return;
        String value = Settings.Secure.getStringForUser(mResolver, key, UserHandle.USER_CURRENT);
        if (value == null) mCache.remove(key);
        else mCache.put(key, value);
    }

    @Override
    public String getGamePropsConfig() { return mCache.get(Settings.Secure.SPOOF_GAMEPROPS_CONFIG); }
    @Override
    public String getPifSpoofPhotos() { return mCache.get(Settings.Secure.SPOOF_PIF_PHOTOS); }
}
