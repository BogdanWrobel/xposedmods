package pl.suzume.xposed.xposedsysscopeblaster;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {
	public static final String PACKAGE = "com.sec.android.app.sysscope";

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (PACKAGE.equals(lpparam.packageName)) {
			XposedBridge.log("INFO  [Blaster]: Found package, hooking...");
			try {
				XposedHelpers.findAndHookMethod(PACKAGE + ".job.RootProcessScanner", lpparam.classLoader,
						"checkIsApprivedProcess", String.class, new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
						return 1;
					}
				});
			} catch (NoSuchMethodError e) {
				XposedBridge.log("ERROR [Blaster]: " + e.getMessage());
			}
			
			try {
				XposedHelpers.findAndHookMethod(PACKAGE + ".job.KernelStatusChecker", lpparam.classLoader,
						"b", String.class, new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
						return true;
					}
				});
			} catch (NoSuchMethodError e) {
				XposedBridge.log("ERROR [Blaster]: " + e.getMessage());
			}
			
			try {
				final String logClass = PACKAGE + ".engine.Log";
				XposedHelpers.setStaticBooleanField(XposedHelpers.findClass(logClass, lpparam.classLoader), "a", true);
				XposedHelpers.findAndHookMethod(logClass, lpparam.classLoader, "setNativeLogState", boolean.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						param.args[0] = true;
					}
				});
				
			} catch (NoSuchMethodError e) {
				XposedBridge.log("ERROR [Blaster]: " + e.getMessage());
			} catch (ClassNotFoundError e) {
				XposedBridge.log("ERROR [Blaster]: " + e.getMessage());
			}
		}

	}
}
