package pl.suzume.xposed.xperiamultimini;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XperiaMultiMini implements IXposedHookLoadPackage {
	public static final String PNAME = "com.sony.smallapp.managerservice";

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (PNAME.equals(lpparam.packageName)) {
			XposedHelpers.findAndHookMethod("com.sony.smallapp.managerservice.SmallAppManagerService",
					lpparam.classLoader, "init", new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					XposedHelpers.setBooleanField(param.thisObject, "mAllowMultiple", true);
				}
			});
		}
	}
}
