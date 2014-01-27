package pl.suzume.xposed;

import de.robv.android.xposed.XposedBridge;

public abstract class XposedModule {
    public void debug(final String str) {
	XposedBridge.log(str);
    }
}
