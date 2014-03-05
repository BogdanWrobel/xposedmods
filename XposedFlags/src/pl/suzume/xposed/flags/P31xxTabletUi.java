package pl.suzume.xposed.flags;

import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.XResources;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class P31xxTabletUi implements IXposedHookZygoteInit, IXposedHookInitPackageResources {
	private static final String SYSTEMUI = "com.android.systemui";
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		if (Build.VERSION.SDK_INT == 16) {
			try {
				XResources.setSystemWideReplacement("android", "bool", "show_ongoing_ime_switcher", false);
				XResources.setSystemWideReplacement("android", "bool", "config_enableLockScreenRotation", true);
				XResources.setSystemWideReplacement("android", "bool", "config_allowAllRotations", true);
				
				XposedHelpers.findAndHookMethod("com.android.internal.policy.impl.PhoneWindowManager", null, "setInitialDisplaySize",
					Display.class, int.class, int.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						XposedHelpers.setBooleanField(param.thisObject, "mHasSystemNavBar", true);
						XposedHelpers.setBooleanField(param.thisObject, "mNavigationBarCanMove", false);
					}
				});
				
				XposedHelpers.findAndHookMethod("com.android.server.wm.WindowManagerService", null, "computeSizeRangesAndScreenLayout",
					boolean.class, int.class, int.class, float.class, Configuration.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						((Configuration) param.args[4]).smallestScreenWidthDp = 721;
					}
				});
				XposedBridge.log("INFO  [P31xxTabletUI]: Methods hooked, flags set.");
				
			}
			catch (NoSuchMethodError e) {
				XposedBridge.log("ERROR [P31xxTabletUI]: Failed to hook methods. Incompatible ROM?");
				XposedBridge.log(e);
			}
			catch (NotFoundException e) {
				XposedBridge.log("ERROR [P31xxTabletUI]: Failed to set flags. Incompatible ROM?");
			}
		}
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		if (Build.VERSION.SDK_INT == 16 && SYSTEMUI.equals(resparam.packageName)) {
			try {
				resparam.res.hookLayout(SYSTEMUI, "layout", "tw_system_bar_notification_panel", new XC_LayoutInflated() {
					@Override
					public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
						XposedBridge.log("Setting new layouts.");
						LinearLayout settings = (LinearLayout) liparam.view.findViewById(liparam.res.getIdentifier("settings", "id", SYSTEMUI));
						settings.getLayoutParams().height = 64;
						
						LinearLayout brightness_controller = (LinearLayout) liparam.view.findViewById(liparam.res.getIdentifier("brightness_controller", "id", SYSTEMUI));
						brightness_controller.getLayoutParams().height = 64;
						
						TextView onGoingNotificationText = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("onGoingNotificationText", "id", SYSTEMUI));
						//onGoingNotificationText.setTextSize(14);
						//onGoingNotificationText.getLayoutParams().height = 20;
						onGoingNotificationText.setVisibility(View.GONE);
						
						/*TextView clear_all_button = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("clear_all_button", "id", SYSTEMUI));
						clear_all_button.setTextSize(12);
						clear_all_button.setPadding(1, 1, 1, 1);
						clear_all_button.getLayoutParams().height = 18;
						TextView latestNotificationText = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("latestNotificationText", "id", SYSTEMUI));
						latestNotificationText.setTextSize(14);
						((RelativeLayout) latestNotificationText.getParent()).getLayoutParams().height = 20;*/
						
						TextView clock = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("clock", "id", SYSTEMUI));
						clock.setTextSize(40);
						((RelativeLayout) clock.getParent().getParent()).getLayoutParams().height -= 40;
						((RelativeLayout) clock.getParent()).setBackground(null);
					}
				});
			}
			catch (NotFoundException e) {
				XposedBridge.log("ERROR [P31xxTabletUI]: Failed to hook layouts. Incompatible ROM?");
			}
		}
	}

}
