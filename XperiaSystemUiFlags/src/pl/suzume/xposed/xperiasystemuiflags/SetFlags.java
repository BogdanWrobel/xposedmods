package pl.suzume.xposed.xperiasystemuiflags;

import pl.suzume.xposed.XposedModule;
import android.content.res.Resources.NotFoundException;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class SetFlags extends XposedModule implements IXposedHookInitPackageResources {

	private static final String TARGET_PKG = "com.android.systemui";

	@Override
	public String tag() {
		return "XperiaSystemUiFlags";
	}

	@Override
	public void handleInitPackageResources(final InitPackageResourcesParam resparam) throws Throwable {
		if (TARGET_PKG.equals(resparam.packageName)) {
			logInfo("Package found!");
			try {
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_recents_interface_for_tablets", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_recents_thumbnail_image_fits_to_xy", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_showRATIconAlways", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_statusBarShowNumber", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_showPhoneRSSIForData", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_showMin3G", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_showRotationLock", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "quick_settings_show_rotation_lock", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_hasSettingsPanel", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_hasFlipSettingsPanel", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_show4GForLTE", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "config_showOperatorNameInStatusBar", Boolean.TRUE);
			} catch (final NotFoundException e) {
				logError(e);
			}
		}
	}
}
