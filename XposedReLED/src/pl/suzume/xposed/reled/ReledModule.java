package pl.suzume.xposed.reled;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Notification;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Changes requested notification LED colors basing on provided pkg => color map
 * 
 * @author bogdan.wrobel
 */
public class ReledModule implements IXposedHookZygoteInit {
    private static final String NOTIFY_METHOD = "notify";
    private static final String NOTIFICATION_MANAGER_PACKAGE = "android.app.NotificationManager";
    private static final Map<String, Integer> colors = new HashMap<String, Integer>() {
	private static final long serialVersionUID = -906537509425835169L;
	{
	    put("", Common.Const.Colors.CYAN);
	    put("com.facebook.orca", Common.Const.Colors.BLUE);
	    put("com.facebook.katana", Common.Const.Colors.BLUE);
	    put("com.android.mms", Common.Const.Colors.WHITE);
	    put("com.badoo.mobile.premium", Common.Const.Colors.MAGENTA);
	    put("com.android.email", Common.Const.Colors.YELLOW);
	    put("com.android.calendar", Common.Const.Colors.ORANGE);
	    put("com.android.phone", Common.Const.Colors.PURPLE);
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
		    final Notification n = (Notification) param.args[param.args.length - 1];
		    if (isNotificationValid(n)) {
			// debugNotification(n);
			changeLedColor(n, getNewLedColor(n));
		    }
		} catch (final Exception e) {
		    Common.Log.error("Exception in hook: " + e.getMessage());
		}
	    }
	};
	try {
	    XposedHelpers.findAndHookMethod(NOTIFICATION_MANAGER_PACKAGE, null, NOTIFY_METHOD, int.class, Notification.class, hook);
	    Common.Log.info("First hook set!");
	} catch (final Exception e) {
	    Common.Log.error("Exception from first hook: " + e.getMessage());
	}
	try {
	    XposedHelpers.findAndHookMethod(NOTIFICATION_MANAGER_PACKAGE, null, NOTIFY_METHOD, String.class, int.class, Notification.class, hook);
	    Common.Log.info("Second hook set!");
	} catch (final Exception e) {
	    Common.Log.error("Exception from second hook: " + e.getMessage());
	}
    }

    /**
     * Dumps notification debug info to log
     * 
     * @param n
     */
    @SuppressWarnings("unused")
    private static void debugNotification(final Notification n) {
	final boolean isDefault = (n.defaults & Notification.DEFAULT_LIGHTS) == Notification.DEFAULT_LIGHTS;
	final boolean useLights = (n.flags & Notification.FLAG_SHOW_LIGHTS) == Notification.FLAG_SHOW_LIGHTS;
	Common.Log.info("Notification from: " + n.contentIntent.getIntentSender().getCreatorPackage());
	Common.Log.info("Color: " + Integer.toHexString(n.ledARGB) + ", default lights: " + isDefault + ", show lights: " + useLights);
    }

    /**
     * Clears "default lights" flag to enable color changes
     * 
     * @param n
     */
    private static void resetNotificationLights(final Notification n) {
	n.defaults = n.defaults & ~Notification.DEFAULT_LIGHTS;
	n.ledOffMS = 2000;
	n.ledOnMS = 1000;
	n.flags = n.flags | Notification.FLAG_SHOW_LIGHTS;
    }

    /**
     * Checks if provided notification is valid (has a sender and uses LED
     * notification flags).
     * 
     * @param n
     * @return info if notification is interesting for us
     */
    private static boolean isNotificationValid(final Notification n) {
	return (n != null) && (n.contentIntent != null) && (n.contentIntent.getIntentSender() != null)
		&& (!"com.android.providers.downloads".equals(n.contentIntent.getIntentSender().getCreatorPackage())) && usesLights(n);
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
     * Extracts sender package name from notification
     * 
     * @param n
     * @return sender package name
     */
    @SuppressLint("DefaultLocale")
    private static String getSenderPackage(final Notification n) {
	return n.contentIntent.getIntentSender().getCreatorPackage().toLowerCase();
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
    private static Integer getNewLedColor(final Notification n) {
	final String pkg = getSenderPackage(n);
	return colors.containsKey(pkg) ? colors.get(pkg) : colors.get("");
    }
}
