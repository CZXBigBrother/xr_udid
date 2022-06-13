package com.xrtech.xr_udid;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.UUID;

/** XrUdidPlugin */
public class XrUdidPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native
  /// Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine
  /// and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "xr_udid");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("xr_getUUID")) {
       result.success(getUniquePsuedoID());
//      result.success(getDeviceUUID(context));
    } else if (call.method.equals("xr_getDevice")) {
      // result.success(getUniquePsuedoID());
      result.success(getCurrentDeviceModel());

    } else {
      result.notImplemented();
    }
    // if (call.method.equals("getPlatformVersion")) {
    // result.success("Android " + android.os.Build.VERSION.RELEASE);
    // } else {
    // result.notImplemented();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  // 获得独一无二的Psuedo ID
  public static String getUniquePsuedoID() {
    String serial = null;

    String m_szDevIDShort = "35" +
        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

        Build.USER.length() % 10; // 13 位

    try {
      serial = android.os.Build.class.getField("SERIAL").get(null).toString();
      // API>=9 使用serial号
      return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    } catch (Exception exception) {
      // serial需要一个初始化
      serial = "serial"; // 随便一个初始化
    }
    // 使用硬件信息拼凑出来的15位号码
    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
  }

  public static String getAndroidID(Context context) {
    String id = Settings.Secure.getString(
        context.getApplicationContext().getContentResolver(),
        Settings.Secure.ANDROID_ID);
    return id == null ? "" : id;
  }

  // 获取UUID
  public String getDeviceUUID(Context context) {
    String androidId = getAndroidID(context);
    UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
    String uuid;
    uuid = deviceUuid.toString().replace("-", "");
    return uuid;
  }

  // 手机的型号
  private String getCurrentDeviceModel() {
    return Build.MODEL;
  }
}
