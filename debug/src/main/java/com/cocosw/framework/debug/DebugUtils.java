package com.cocosw.framework.debug;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.accessory.utils.Utils;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Administrator on 14-2-18.
 */
public class DebugUtils {

    /**
     * ViewServer only apply for unrooted <r11 device
     *
     * @param context
     * @return
     */
    public static boolean isViewServerNeeded(Context context) {
        return (Build.VERSION.SDK_INT > 11 && !Utils.isRooted(context));
    }

    public static void setupStrictMode() {
        if (UIUtils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static void setupStetho(Context context) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    public static void setupLeakCanary(Application app) {
        LeakCanary.install(app);
    }

}
