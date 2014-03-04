package pl.suzume.xposed.reled;

import java.util.HashMap;
import java.util.Map;

import pl.suzume.xposed.XposedModule;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedHelpers;

/**
 * Changes requested notification LED colors basing on provided pkg => color map
 * 
 * @author bogdan.wrobel
 */
public class ReledModule extends XposedModule implements IXposedHookZygoteInit {
    private static final String TAG = "RELED";
    private static final String NOTIFY_METHOD = "notify";
    private static final String NOTIFICATION_MANAGER_PACKAGE = "android.app.NotificationManager";
    private static final Map<String, Integer> colors = new HashMap<String, Integer>() {
	private static final long serialVersionUID = -906537509425835169L;
	{
	    put("", LedConfig.Colors.CYAN);
	    put("com.facebook.orca", LedConfig.Colors.BLUE);
	    put("com.facebook.katana", LedConfig.Colors.BLUE);
	    put("com.android.mms", LedConfig.Colors.WHITE);
	    put("com.sonyericsson.conversations", LedConfig.Colors.WHITE);
	    put("com.badoo.mobile.premium", LedConfig.Colors.MAGENTA);
	    put("com.android.email", LedConfig.Colors.YELLOW);
	    put("com.android.calendar", LedConfig.Colors.ORANGE);
	    put("com.android.phone", LedConfig.Colors.PURPLE);
	}
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void initZygote(final StartupParam startupParam) throws Throwable {
	final XC_MethodHook hook = new XC_MethodHook() {
	    @Override
	    protected void beforeHookedMethod(final MethodHookParam param) {
		try {
		    final Notification n = getNotification(param);
		    final String pkg = getSenderPackage(param);
		    if (isNotificationValid(n, pkg)) {
			changeLedColor(n, getNewLedColor(pkg));
		    }
		} catch (final Exception e) {
		    logError("Exception in hook: " + e.getMessage());
		}
	    }
	};
	try {
	    XposedHelpers.findAndHookMethod(NOTIFICATION_MANAGER_PACKAGE, null, NOTIFY_METHOD, int.class, Notification.class, hook);
	    logInfo("First hook set!");
	} catch (final Exception e) {
	    logError("Exception from first hook: " + e.getMessage());
	}
	try {
	    XposedHelpers.findAndHookMethod(NOTIFICATION_MANAGER_PACKAGE, null, NOTIFY_METHOD, String.class, int.class, Notification.class, hook);
	    logInfo("Second hook set!");
	} catch (final Exception e) {
	    logError("Exception from second hook: " + e.getMessage());
	}
    }

    /**
     * Clears "default lights" flag to enable color changes
     * 
     * @param n
     */
    private static void resetNotificationLights(final Notification n) {
	n.defaults = n.defaults & ~Notification.DEFAULT_LIGHTS;
	n.ledOffMS = 1000;
	n.ledOnMS = 2000;
	n.flags = n.flags | Notification.FLAG_SHOW_LIGHTS;
    }

    /**
     * Checks if provided notification is valid (has a sender and uses LED
     * notification flags).
     * 
     * @param n
     * @param pkg
     *            package name
     * @return info if notification is interesting for us
     */
    private static boolean isNotificationValid(final Notification n, final String pkg) {
	return !"com.android.providers.downloads".equals(pkg) && usesLights(n);
    }

    /**
     * Checks whether any LED settings are enabled
     * 
     * @param n
     * @return info if lights are used by the notification
     */
    private static boolean usesLights(final Notification n) {
	return usesDefaultLights(n) || usesCustomLights(n);
    }

    /**
     * Checks whether default LED settings are set
     * 
     * @param n
     * @return
     */
    private static boolean usesDefaultLights(final Notification n) {
	return (n.defaults & Notification.DEFAULT_LIGHTS) == Notification.DEFAULT_LIGHTS;
    }

    /**
     * Checks whether custom LED settings are set
     * 
     * @param n
     * @return
     */
    private static boolean usesCustomLights(final Notification n) {
	return (n.flags & Notification.FLAG_SHOW_LIGHTS) == Notification.FLAG_SHOW_LIGHTS;
    }

    /**
     * Extracts sender package name from method parameter
     * 
     * @param n
     * @return sender package name
     */
    @SuppressLint("DefaultLocale")
    private static String getSenderPackage(final MethodHookParam param) {
	return ((Context) XposedHelpers.getObjectField(param.thisObject, "mContext")).getPackageName().toLowerCase();
    }

    /**
     * Extracts Notification from method parameter
     * 
     * @param param
     * @return Notification retrieved by manage
     */
    private static Notification getNotification(final MethodHookParam param) {
	return (Notification) param.args[param.args.length - 1];
    }

    /**
     * Updates notification LED color, resetting "defaults" flag if needed
     * 
     * @param n
     * @param color
     */
    private static void changeLedColor(final Notification n, final Integer color) {
	resetNotificationLights(n);
	n.ledARGB = color;
    }

    /**
     * Retrieves new mapped color for LED notification
     * 
     * @param n
     * @return mapped color
     */
    private static Integer getNewLedColor(final String pkg) {
	return colors.containsKey(pkg) ? colors.get(pkg) : colors.get("");
    }

    @Override
    public String tag() {
	return TAG;
    }
}
