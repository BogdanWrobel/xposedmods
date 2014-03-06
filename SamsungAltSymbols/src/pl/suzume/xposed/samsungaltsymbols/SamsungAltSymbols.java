package pl.suzume.xposed.samsungaltsymbols;

import pl.suzume.xposed.XposedModule;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SamsungAltSymbols extends XposedModule implements IXposedHookLoadPackage {
	private static final String TARGET_METHOD_NAME = "setSecondarySymbolStatus";
	public static final String PNAME = "com.sec.android.inputmethod";

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		int hooked = 0;
		final XC_MethodReplacement enabler = new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
				XposedHelpers.setBooleanField(param.thisObject, "mEnableSecondarySymbol", true);
				return null;
			}
		};
		if (PNAME.equals(lpparam.packageName)) {
			logInfo("Initializing hooks...");
			try {
				XposedHelpers.findAndHookMethod("com.diotek.ime.implement.view.KeyboardView", lpparam.classLoader, TARGET_METHOD_NAME, int.class, enabler);
				hooked++;
			} catch (final NoSuchMethodError e) {
				logInfo("First method not found.");
			}
			try {
				XposedHelpers.findAndHookMethod("com.diotek.ime.framework.inputmode.InputModeManager", lpparam.classLoader, TARGET_METHOD_NAME, int.class,
						enabler);
				hooked++;
			} catch (final NoSuchMethodError e) {
				logInfo("Second method not found.");
			}
			switch (hooked) {
			case 0:
				logError("No matching method found to hook!");
				break;
			case 1:
				logInfo("Hooked succesfully!");
				break;
			case 2:
				logWarn("Hooked succesfully TWO methods?!");
				break;
			}
		}
	}

	@Override
	public String tag() {
		return "SamsungAltSymbols";
	}
}
