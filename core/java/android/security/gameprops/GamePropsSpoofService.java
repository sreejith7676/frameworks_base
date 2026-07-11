package android.security.gameprops;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.os.Build;
import android.util.Log;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.Iterator;

/** @hide */
public final class GamePropsSpoofService {
    private static final String TAG = "GamePropsSpoof";

    public static void spoof(String packageName) {
        try {
            IActivityManager am = ActivityManager.getService();
            if (am == null) return;

            String config = am.getSpoofGamePropsConfig();
            if (config == null || config.isEmpty()) return;

            JSONObject json = new JSONObject(config);
            if (!json.optBoolean("enabled", false)) return;

            JSONObject games = json.optJSONObject("games");
            if (games == null || !games.has(packageName)) return;

            JSONObject props = games.getJSONObject(packageName);
            Iterator<String> keys = props.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                setBuildField(key, props.getString(key));
            }
            Log.i(TAG, "Spoofed game props for " + packageName);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing game props", e);
        }
    }

    private static void setBuildField(String key, String value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (Exception e) {
            try {
                Field field = Build.VERSION.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(null, value);
                field.setAccessible(false);
            } catch (Exception ignored) {}
        }
    }
}
