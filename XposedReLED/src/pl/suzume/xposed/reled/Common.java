package pl.suzume.xposed.reled;

import de.robv.android.xposed.XposedBridge;

public class Common {
    public static class Const {
	public static final String TAG = "RELED";
	public static final String MY_PACKAGE = Common.class.getPackage().getName();
	public static final String PREFS = "ReLED";

	public static class Colors {
	    public static final Integer RED = 0xFFFF0000;
	    public static final Integer GREEN = 0xFF00FF00;
	    public static final Integer BLUE = 0xFF0000FF;
	    public static final Integer WHITE = 0xFFFFFFFF;
	    public static final Integer YELLOW = 0xFFFFFF00;
	    public static final Integer ORANGE = 0xFFFF8000;
	    public static final Integer MAGENTA = 0xFFFF00FF;
	    public static final Integer CYAN = 0xFF00FFFF;
	    public static final Integer NAVY = 0xFF000080;
	    public static final Integer PURPLE = 0xFF990099;
	    public static final Integer NONE = 0x00000000;
	}
    }

    public static class Log {
	public static void info(final String msg) {
	    printMsg(msg, false);
	}

	public static void error(final String msg) {
	    printMsg(msg, true);
	}

	private static void printMsg(final String msg, final boolean isError) {
	    XposedBridge.log((new StringBuffer().append(Const.TAG).append(" [").append(isError ? "ERROR" : "INFO").append("]: ").append(msg)).toString());
	}
    }
}
