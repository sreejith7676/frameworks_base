package android.security.pif;

import android.annotation.NonNull;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/** @hide */
public final class PlayIntegritySpoofService {
    private static final String TAG = "PlayIntegritySpoof";
    
    private static final Map<String, String> PIF_PROPS = new HashMap<>();
    
    static {
        PIF_PROPS.put("MANUFACTURER", "motorola");
        PIF_PROPS.put("MODEL", "moto g54 5G");
        PIF_PROPS.put("FINGERPRINT", "motorola/cancunf_g_sysq/cancunf:15/V1TDS35H.83-20-5-11/450c26-b11610:user/release-keys");
        PIF_PROPS.put("PRODUCT", "cancunf");
        PIF_PROPS.put("DEVICE", "cancunf");
        PIF_PROPS.put("VERSION.RELEASE", "17");
        PIF_PROPS.put("ID", "CP21.260330.011");
        PIF_PROPS.put("VERSION.INCREMENTAL", "15499021");
        PIF_PROPS.put("TYPE", "user");
        PIF_PROPS.put("TAGS", "release-keys");
        PIF_PROPS.put("VERSION.DEVICE_INITIAL_SDK_INT", "32");
    }

    public static void spoof(@NonNull String processName) {
        // Only target the DroidGuard integrity process and Play Store
        if ("com.google.android.gms.unstable".equals(processName) || "com.android.vending".equals(processName)) {
            for (Map.Entry<String, String> entry : PIF_PROPS.entrySet()) {
                setBuildField(entry.getKey(), entry.getValue());
            }
            Log.i(TAG, "Spoofed Play Integrity props for " + processName);
        }
    }

    private static void setBuildField(String key, String value) {
        try {
            boolean isVersion = key.startsWith("VERSION.");
            String fieldName = isVersion ? key.substring(8) : key;
            Class<?> targetClass = isVersion ? Build.VERSION.class : Build.class;

            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            
            // Handle integer types (like SDK_INT) and Strings
            Class<?> fieldType = field.getType();
            if (fieldType == int.class) {
                field.set(null, Integer.parseInt(value));
            } else if (fieldType == String.class) {
                field.set(null, value);
            }
            field.setAccessible(false);
        } catch (Exception e) {
            Log.e(TAG, "Failed to spoof field: " + key, e);
        }
    }
}
