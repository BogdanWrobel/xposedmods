package pl.suzume.xposed.samsungaltsymbols;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SamsungAltSymbols implements IXposedHookLoadPackage {
	public static final String PNAME = "com.sec.android.inputmethod";

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam)
			throws Throwable {
		int hooked = 0;
		if (PNAME.equals(lpparam.packageName)) {
			XposedBridge.log("[SamsungAltSymbols] INFO:  Initializing hooks...");
			try {
				XposedHelpers.findAndHookMethod(
						"com.diotek.ime.implement.view.KeyboardView",
						lpparam.classLoader, "setSecondarySymbolStatus",
						int.class, new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(
									MethodHookParam param) throws Throwable {
								XposedHelpers.setBooleanField(param.thisObject,
										"mEnableSecondarySymbol", true);
								return null;
							}
						});
				hooked++;
			} catch (NoSuchMethodError e) {
				XposedBridge.log("[SamsungAltSymbols] ERROR: " + e.getMessage());
			}
			try {
				XposedHelpers.findAndHookMethod(
						"com.diotek.ime.framework.inputmode.InputModeManager",
						lpparam.classLoader, "setSecondarySymbolStatus",
						int.class, new XC_MethodReplacement() {
							@Override
							protected Object replaceHookedMethod(
									MethodHookParam param) throws Throwable {
								XposedHelpers.setBooleanField(param.thisObject,
										"mEnableSecondarySymbol", true);
								return null;
							}
						});
				hooked++;
			} catch (NoSuchMethodError e) {
				XposedBridge.log("[SamsungAltSymbols] ERROR: " + e.getMessage());
			}
			switch (hooked) {
			case 0:
				XposedBridge.log("[SamsungAltSymbols] ERROR: No matching method found to hook!");
				break;
			case 1:
				XposedBridge.log("[SamsungAltSymbols] INFO:  Hooked succesfully!");
				break;
			case 2:
				XposedBridge.log("[SamsungAltSymbols] WARN:  Hooked succesfully TWO methods?!");
				break;
			}
		}
	}
}
