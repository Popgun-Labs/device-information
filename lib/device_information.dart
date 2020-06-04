import 'dart:async';

import 'package:flutter/services.dart';

class DeviceInformation {
  static const MethodChannel _channel =
  const MethodChannel('device_information');

  static Future<AndroidMemoryDetails> get getAndroidMemoryDetails async {
    final Map<String, String> map = await _channel.invokeMapMethod('getMemoryDetails');
    return AndroidMemoryDetails(
      total: map["total"],
      free: map["free"],
    );
  }

  // iOS does not return free space because implementation is inaccurate
  static Future<IOSMemoryDetails> get getIOSMemoryDetails async {
    final Map<String, String> map = await _channel.invokeMapMethod('getMemoryDetails');
    return IOSMemoryDetails(
      total: map["total"],
    );
  }

  static Future<String> get getModelName async {
    final String modelName = await _channel.invokeMethod('getModelName');
    return modelName;
  }
}

class AndroidMemoryDetails {
  AndroidMemoryDetails({
    this.total,
    this.free,
  });

  final String total;
  final String free;
}

class IOSMemoryDetails {
  IOSMemoryDetails({
    this.total,
  });

  final String total;
}