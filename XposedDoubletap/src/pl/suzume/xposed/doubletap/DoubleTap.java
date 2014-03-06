package pl.suzume.xposed.doubletap;

import java.lang.reflect.Method;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class DoubleTap implements IXposedHookZygoteInit {
	public void initZygote(final StartupParam startupParam) throws Throwable {
		try {
			final Class<?> resolverActivityClass = XposedHelpers.findClass("com.android.internal.app.ResolverActivity", null);
			final Method onItemClick = XposedHelpers
					.findMethodExact(resolverActivityClass, "onItemClick", AdapterView.class, View.class, int.class, long.class);
			final Method onButtonClick = XposedHelpers.findMethodExact(resolverActivityClass, "onButtonClick", View.class);
			final Method onCreate = XposedHelpers.findMethodExact(resolverActivityClass, "onCreate", Bundle.class);

			final XC_MethodHook methodEnricher = new XC_MethodHook() {
				int selectedItem = -1;

				@Override
				protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
					if ("onItemClick".equals(param.method.getName())) {
						final int selected = (Integer) param.args[2];
						if ((this.selectedItem == -1) || (this.selectedItem != selected)) {
							this.selectedItem = selected;
						} else {
							final Button mOnceButton = (Button) XposedHelpers.getObjectField(param.thisObject, "mOnceButton");
							onButtonClick.invoke(param.thisObject, mOnceButton);
						}
					} else if ("onCreate".equals(param.method.getName())) {
						this.selectedItem = -1;
					}
				}
			};
			XposedBridge.hookMethod(onItemClick, methodEnricher);
			XposedBridge.hookMethod(onCreate, methodEnricher);
		} catch (final Throwable t) {
			XposedBridge.log("............. Zygote Exception intercepted .............");
			XposedBridge.log(t);
		}
	}
}
