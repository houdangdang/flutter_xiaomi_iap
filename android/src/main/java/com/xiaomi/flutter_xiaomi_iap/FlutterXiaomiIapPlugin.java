package com.xiaomi.flutter_xiaomi_iap;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

import com.xiaomi.gamecenter.appjoint.MiCommplatform;

/** FlutterXiaomiIapPlugin */
public class FlutterXiaomiIapPlugin implements FlutterPlugin, ActivityAware {
  private MethodChannel mMethodChannel;
  private MethodCallHandlerImpl mMethodCallHandler;

  private void onAttachedToEngine(@NonNull BinaryMessenger messenger) {
    mMethodChannel = new MethodChannel(messenger, "flutter_xiaomi_iap");
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    mMethodChannel = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    final Activity activity = binding.getActivity();
    final Application application = activity.getApplication();
    MiCommplatform.setApplication(application);
    final PayListActivityMonitor monitor = new PayListActivityMonitor();
    application.registerActivityLifecycleCallbacks(monitor);
    mMethodCallHandler = new MethodCallHandlerImpl(activity, monitor);
    mMethodChannel.setMethodCallHandler(mMethodCallHandler);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    mMethodChannel.setMethodCallHandler(null);
    mMethodCallHandler = null;
  }
}
