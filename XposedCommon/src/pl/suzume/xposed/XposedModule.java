package pl.suzume.xposed;

import de.robv.android.xposed.XposedBridge;

/**
 * Base class for all Xposed modules
 * 
 * @author bogdan.wrobel
 */
public abstract class XposedModule {

    private enum ErrorLevel {
	INFO, WARN, ERROR
    };

    /**
     * Alias for {@link #logDebug(String)}
     */
    public void logInfo(final String str) {
	logDebug(str);
    }

    /**
     * Logs debug info string if enabled.
     * 
     * @see #isDebugEnabled()
     * @param str
     *            message to log
     */
    public void logDebug(final String str) {
	if (isDebugEnabled()) {
	    echo(str, ErrorLevel.INFO);
	}
    }

    /**
     * Logs warning string.
     * 
     * @param str
     *            message to log
     */
    public void logWarn(final String str) {
	echo(str, ErrorLevel.WARN);
    }

    /**
     * Logs error string.
     * 
     * @param str
     *            message to log
     */
    public void logError(final String str) {
	echo(str, ErrorLevel.ERROR);
    }

    /**
     * Logs error message from Throwable.
     * 
     * @param e
     *            Throwable to get message from
     */
    public void logError(final Throwable e) {
	echo(e.getMessage(), ErrorLevel.ERROR);
    }

    /**
     * Gets current module package name.
     * 
     * @return current module package name
     */
    public final String getMyPackage() {
	return this.getClass().getPackage().getName();
    }

    /**
     * Decides if debug messaging should be enabled ({@code true} by default).
     * Override this to enable debug log on/off
     * 
     * @return flag if debug logging should be enabled
     */
    public boolean isDebugEnabled() {
	return true;
    }

    /**
     * Logs given string prefixing it with module tag and error level
     * 
     * @param s
     *            message to log
     * @param level
     *            desired error level
     */
    private void echo(final String s, final ErrorLevel level) {
	XposedBridge.log((new StringBuffer().append(tag()).append(" [").append(level.toString()).append("]: ").append(s)).toString());
    }

    /**
     * Returns application tag
     * 
     * @return application tag
     */
    public abstract String tag();
}
