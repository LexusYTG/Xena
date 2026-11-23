package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

final class Consent {
    private Consent() {}

    private static final String FILE = "xena_consent";
    private static final String KEY = "accepted";
    private static final String KEY_VERSION = "eula_version";
    private static final String KEY_DATE = "accepted_at";
    static final String VERSION = "1.0";

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static boolean granted(Context c) {
        try {
            SharedPreferences p = prefs(c);
            return p.getBoolean(KEY, false) && VERSION.equals(p.getString(KEY_VERSION, ""));
        } catch (Throwable e) {
            return false;
        }
    }

    static void accept(Context c) {
        prefs(c).edit()
                .putBoolean(KEY, true)
                .putString(KEY_VERSION, VERSION)
                .putLong(KEY_DATE, System.currentTimeMillis())
                .commit();
    }

    static boolean ensure(Activity a) {
        if (granted(a)) return true;
        a.startActivity(new Intent(a, ConsentActivity.class));
        a.finish();
        return false;
    }

    static void wipe(Activity a) {
        a.finishAndRemoveTask();
        boolean ok = false;
        try {
            ok = ((ActivityManager) a.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
        } catch (Throwable e) {
        }
        if (!ok) android.os.Process.killProcess(android.os.Process.myPid());
    }
}
