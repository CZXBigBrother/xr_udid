import 'dart:async';

import 'package:flutter/services.dart';

class XrUdid {
  static const MethodChannel _channel = MethodChannel('xr_udid');
  static Future<String?> get xr_getUUID async {
    final String? version = await _channel.invokeMethod('xr_getUUID');
    return version;
  }

  static Future<String?> get xr_getDevice async {
    final String? version = await _channel.invokeMethod('xr_getDevice');
    return version;
  }
}
