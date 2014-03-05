package pl.suzume.xposed.dormant;

import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class DormantDisabler implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if ("com.android.settings".equals(lpparam.packageName)) {
			try {
				XposedHelpers.findAndHookMethod("com.android.settings.dormantmode.DormantModeNotiReceiver", lpparam.classLoader,
							"notificationCreate", Context.class, XC_MethodReplacement.DO_NOTHING);
			}
			catch (Throwable t) {
				XposedBridge.log("............... Exception in handleLoadPackage ...............");
				XposedBridge.log(t);
			}
		}
		
	}

}
