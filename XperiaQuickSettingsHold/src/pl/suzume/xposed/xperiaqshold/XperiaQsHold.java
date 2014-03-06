package pl.suzume.xposed.xperiaqshold;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pl.suzume.xposed.XposedModule;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XperiaQsHold extends XposedModule implements IXposedHookLoadPackage {
	private static final String TOOLS_MAIN = "com.sonymobile.systemui.statusbar.tools.ToolsMain";
	public static final String PNAME = "com.android.systemui";
	static final List<String> types = new ArrayList<String>();

	public void handle(final String key, final Context mContext) {
		final Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		if ("wifi".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_WIFI_SETTINGS);
		} else if ("bluetooth".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
		} else if ("datatraffic".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
		} else if ("brightness".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
		} else if ("sound".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_SOUND_SETTINGS);
		} else if ("tethering".equals(key)) {
			intent.setClassName("com.android.settings", "com.android.settings.TetherSettings");
		} else if ("gps".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		} else if ("airplanemode".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS);
		} else if ("autorotate".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
		} else if ("settings".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_SETTINGS);
		} else if ("roaming".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
		} else if ("autosync".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_SYNC_SETTINGS);
		} else if ("nfc".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_NFC_SETTINGS);
		} else if ("stamina".equals(key)) {
			intent.setAction(Intent.ACTION_POWER_USAGE_SUMMARY);
		} else if ("lte".equals(key)) {
			intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
		} else {
			intent.setAction(android.provider.Settings.ACTION_SETTINGS);
		}

		try {
			final Object service = mContext.getSystemService("statusbar");
			final Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
			final Method collapse = statusbarManager.getMethod("collapsePanels");
			collapse.setAccessible(true);
			collapse.invoke(service);
			mContext.startActivity(intent);
		} catch (final Exception e) {
			Toast.makeText(mContext, "Error launching shortcut: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (PNAME.equals(lpparam.packageName)) {
			final Method reCreateButtons = XposedHelpers.findMethodExact(TOOLS_MAIN, lpparam.classLoader, "reCreateButtons");
			final Method create = XposedHelpers.findMethodExact(TOOLS_MAIN, lpparam.classLoader, "create", String.class);

			XposedBridge.hookMethod(create, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
					types.add((String) param.args[0]);
				}
			});

			XposedBridge.hookMethod(reCreateButtons, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
					types.clear();
				}

				@SuppressWarnings("rawtypes")
				@Override
				protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
					final LinkedList mButtons = (LinkedList) XposedHelpers.getObjectField(param.thisObject, "mButtons");
					final Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
					int i = 0;
					for (final Object o : mButtons) {
						final FrameLayout button = (FrameLayout) o;
						final String key = types.get(i);
						button.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(final View v) {
								handle(key, mContext);
								return true;
							}
						});
						i++;
					}
				}
			});
		}
	}

	@Override
	public String tag() {
		return "XperiaQSHold";
	}
}
