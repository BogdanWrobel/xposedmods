package pl.suzume.xposed;

import de.robv.android.xposed.XposedBridge;

public abstract class XposedModule {

    private enum ErrorLevel {
	INFO, WARN, ERROR
    };

    public void logInfo(final String str) {
	logDebug(str);
    }

    public void logDebug(final String str) {
	if (isDebugEnabled()) {
	    echo(str, ErrorLevel.INFO);
	}
    }

    public void logWarn(final String str) {
	echo(str, ErrorLevel.WARN);
    }

    public void logError(final String str) {
	echo(str, ErrorLevel.ERROR);
    }

    public void logError(final Throwable e) {
	echo(e.getMessage(), ErrorLevel.ERROR);
    }

    public final String getMyPackage() {
	return this.getClass().getPackage().getName();
    }

    public boolean isDebugEnabled() {
	return true;
    }

    private void echo(final String s, final ErrorLevel level) {
	XposedBridge.log((new StringBuffer().append(tag()).append(" [").append(level.toString()).append("]: ").append(s)).toString());
    }

    public abstract String tag();
}
