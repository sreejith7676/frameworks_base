package android.security.pif;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/** @hide */
public final class PhotosSpoofService {
    private static final String TAG = "PhotosSpoof";
    private static final String GPHOTOS_PACKAGE = "com.google.android.apps.photos";
    
    private static final Map<String, Object> PIXEL_XL_PROPS = Map.of(
        "BRAND", "google", "MANUFACTURER", "Google", "DEVICE", "marlin",
        "PRODUCT", "marlin", "HARDWARE", "marlin", "ID", "QP1A.191005.007.A3",
        "MODEL", "Pixel XL", "FINGERPRINT", "google/marlin/marlin:10/QP1A.191005.007.A3/5972272:user/release-keys"
    );

    private static final Set<String> NEXUS_FEATURES = Set.of(
        "com.google.android.apps.photos.NEXUS_PRELOAD", "com.google.android.apps.photos.nexus_preload",
        "com.google.android.feature.PIXEL_EXPERIENCE", "com.google.android.feature.GOOGLE_BUILD",
        "com.google.android.feature.GOOGLE_EXPERIENCE"
    );

    private static final Set<String> PIXEL_FEATURES = Set.of(
        "com.google.android.feature.PIXEL_2022_EXPERIENCE", "com.google.android.feature.PIXEL_2021_EXPERIENCE",
        "com.google.android.feature.PIXEL_2020_EXPERIENCE", "PIXEL_EXPERIENCE"
    );

    private static boolean isEnabled() {
        try {
            IActivityManager am = ActivityManager.getService();
            if (am == null) return false;
            String val = am.getSpoofPifSpoofPhotos();
            return val == null || "1".equals(val) || "true".equalsIgnoreCase(val);
        } catch (Exception e) { return false; }
    }

    public static void spoof(String packageName) {
        if (GPHOTOS_PACKAGE.equals(packageName) && isEnabled()) {
            for (Map.Entry<String, Object> entry : PIXEL_XL_PROPS.entrySet()) {
                setBuildField(entry.getKey(), String.valueOf(entry.getValue()));
            }
            Log.i(TAG, "Spoofed Google Photos");
        }
    }

    public static Boolean hasSystemFeature(String packageName, String name) {
        if (GPHOTOS_PACKAGE.equals(packageName) && isEnabled()) {
            if (!SystemProperties.get("ro.soc.manufacturer", "").equalsIgnoreCase("google") && PIXEL_FEATURES.contains(name)) return false;
            return NEXUS_FEATURES.contains(name);
        }
        return null;
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
