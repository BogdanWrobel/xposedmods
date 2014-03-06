package pl.suzume.xposed.xperiahomeflags;

import pl.suzume.xposed.XposedModule;
import android.content.res.Resources.NotFoundException;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class SetFlags extends XposedModule implements IXposedHookInitPackageResources {

	private static final String TARGET_PKG = "com.sonyericsson.home";

	@Override
	public String tag() {
		return "XperiaHomeFlags";
	}

	@Override
	public void handleInitPackageResources(final InitPackageResourcesParam resparam) throws Throwable {
		if (TARGET_PKG.equals(resparam.packageName)) {
			logInfo("Package found!");
			try {
				resparam.res.setReplacement(TARGET_PKG, "bool", "enable_stage_icon_labels", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "autoRotate", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "enable_desktop_multi_line_labels", Boolean.TRUE);
				resparam.res.setReplacement(TARGET_PKG, "bool", "desktop_pagination_autohide", Boolean.TRUE);
			} catch (final NotFoundException e) {
				logError(e);
			}
		}
	}
}
