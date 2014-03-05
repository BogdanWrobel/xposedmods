package pl.suzume.xposed.tabletui;

import android.content.res.Configuration;
import android.content.res.XResources;
import android.view.Display;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TabletUi implements IXposedHookZygoteInit {
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		try {
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
		}
		catch (NoSuchMethodError e) {
			XposedBridge.log("Failed to hook methods. Incompatible ROM?");
			XposedBridge.log(e);
		}
	}

}
