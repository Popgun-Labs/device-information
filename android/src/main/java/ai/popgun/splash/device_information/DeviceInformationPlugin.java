package ai.popgun.splash.device_information;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/** DeviceinformationPlugin */
public class DeviceInformationPlugin implements MethodCallHandler, FlutterPlugin {
  private Context applicationContext;
  private MethodChannel methodChannel;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final DeviceInformationPlugin instance = new DeviceInformationPlugin();
    instance.onAttachedToEngine(registrar.context(), registrar.messenger());
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    onAttachedToEngine(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  private void onAttachedToEngine(Context applicationContext, BinaryMessenger messenger) {
    this.applicationContext = applicationContext;
    methodChannel = new MethodChannel(messenger, "device_information");
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    applicationContext = null;
    methodChannel.setMethodCallHandler(null);
    methodChannel = null;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    try {
      if (call.method.equals("getMemoryDetails")) {
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        String totalMemory = convertMemoryToHumanized(memoryInfo.totalMem);
        String availMemory = convertMemoryToHumanized(memoryInfo.availMem);

        Map<String, String> map = new HashMap<>();
        map.put("total", totalMemory);
        map.put("free", availMemory);
        result.success(map);
        return;
      } if (call.method.equals("getModelName")) {
        result.success(getModelName());
        return;
      } else {
        result.notImplemented();
        return;
      }
    } catch (Error e) {
      result.error("Failed onMethodCall", e.getMessage(), null);
    }
  }

  public String convertMemoryToHumanized(long memoryValue)
  {
    DecimalFormat twoDecimalForm = new DecimalFormat("#.##");

    String humanizedValue = "";

    double kb = memoryValue / 1024.0;
    double mb = memoryValue / 1048576.0;
    double gb = memoryValue / 1073741824.0;
    double tb = memoryValue / 1099511627776.0;

    if (tb > 1) {
      humanizedValue = twoDecimalForm.format(tb).concat(" TB");
    } else if (gb > 1) {
      humanizedValue = twoDecimalForm.format(gb).concat(" GB");
    } else if (mb > 1) {
      humanizedValue = twoDecimalForm.format(mb).concat(" MB");
    }else if(kb > 1){
      humanizedValue = twoDecimalForm.format(mb).concat(" KB");
    } else {
      humanizedValue = twoDecimalForm.format(memoryValue).concat(" Bytes");
    }

    return humanizedValue;
  }

  public String getModelName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }
}
