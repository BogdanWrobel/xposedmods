package pl.suzume.xposed.disabler;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Disabler implements IXposedHookLoadPackage {
	private static final String PACKAGE = "com.android.mms";

	private void showNotification(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	private String getContactName(Context context, String number) {
		String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};
		Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
		if (cursor.moveToFirst()) {
		    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		    return name + " (" + number + ")";
		}
		else {
		    return number;
		}
	}
	
	private String buildMessage(Context context, int type, int status, String address) {
		String msg = context.getResources().getString(context.getResources().getIdentifier("delivery_toast_body", "string", PACKAGE));
		return String.format(msg, getContactName(context, address));
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (Build.VERSION.SDK_INT == 16 && PACKAGE.equals(lpparam.packageName)) {
			try {
				XposedHelpers.findAndHookMethod(PACKAGE + ".transaction.MessagingNotification", lpparam.classLoader, "updateReportNotification",
						Context.class, int.class, int.class, long.class, String.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						Context context = (Context) param.args[0];
						int type = (Integer) param.args[1];
						int status = (Integer) param.args[2];
						//long threadId = (Long) param.args[3];
						String address = (String) param.args[4];
						
						//XposedBridge.log("INFO  [Disabler]: type = " + type + ", status  = " + status + ", address = " + address);
						
						if (type == 0 && status == 0) {
							showNotification(context, buildMessage(context, type, status, address));
							param.setResult(null);
						}
					}
				});
						
				XposedBridge.log("INFO  [Disabler]: Hooked...");
			}
			catch (ClassNotFoundError e) {
				XposedBridge.log("ERROR [Disabler]: Failed to find class. Incompatible ROM?");
				XposedBridge.log(e);
			}
			catch (NoSuchMethodError e) {
				XposedBridge.log("ERROR [Disabler]: Failed to hook methods. Incompatible ROM?");
				XposedBridge.log(e);
			}
		}
	}
}
