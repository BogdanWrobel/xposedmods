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

	private void showNotification(final Context context, final String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	private String getContactName(final Context context, final String number) {
		final String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME };
		final Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		final Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
		if (cursor.moveToFirst()) {
			final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			return name + " (" + number + ")";
		} else {
			return number;
		}
	}

	private String buildMessage(final Context context, final int type, final int status, final String address) {
		final String msg = context.getResources().getString(context.getResources().getIdentifier("delivery_toast_body", "string", PACKAGE));
		return String.format(msg, getContactName(context, address));
	}

	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if ((Build.VERSION.SDK_INT == 16) && PACKAGE.equals(lpparam.packageName)) {
			try {
				XposedHelpers.findAndHookMethod(PACKAGE + ".transaction.MessagingNotification", lpparam.classLoader, "updateReportNotification", Context.class,
						int.class, int.class, long.class, String.class, new XC_MethodHook() {
							@Override
							protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
								final Context context = (Context) param.args[0];
								final int type = (Integer) param.args[1];
								final int status = (Integer) param.args[2];
								// long threadId = (Long) param.args[3];
								final String address = (String) param.args[4];

								// XposedBridge.log("INFO  [Disabler]: type = " + type + ", status  = " + status + ", address = " + address);

								if ((type == 0) && (status == 0)) {
									showNotification(context, buildMessage(context, type, status, address));
									param.setResult(null);
								}
							}
						});

				XposedBridge.log("INFO  [Disabler]: Hooked...");
			} catch (final ClassNotFoundError e) {
				XposedBridge.log("ERROR [Disabler]: Failed to find class. Incompatible ROM?");
				XposedBridge.log(e);
			} catch (final NoSuchMethodError e) {
				XposedBridge.log("ERROR [Disabler]: Failed to hook methods. Incompatible ROM?");
				XposedBridge.log(e);
			}
		}
	}
}
