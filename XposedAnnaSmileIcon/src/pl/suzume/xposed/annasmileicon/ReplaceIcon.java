package pl.suzume.xposed.annasmileicon;

import pl.suzume.xposed.XposedModule;
import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class ReplaceIcon extends XposedModule implements IXposedHookZygoteInit, IXposedHookInitPackageResources {

    private static final String MMS_PACKAGE_NAME = "com.android.mms";
    private static String MODULE_PATH = null;

    @Override
    public String tag() {
	return "ANNASMILE";
    }

    @Override
    public void handleInitPackageResources(final InitPackageResourcesParam resparam) throws Throwable {
	if (!resparam.packageName.equals(MMS_PACKAGE_NAME)) {
	    return;
	}

	final XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
	resparam.res.setReplacement(MMS_PACKAGE_NAME, "drawable", "stat_notify_message", modRes.fwd(R.drawable.conv_notify));
    }

    @Override
    public void initZygote(final StartupParam startupParam) throws Throwable {
	MODULE_PATH = startupParam.modulePath;
    }

}
