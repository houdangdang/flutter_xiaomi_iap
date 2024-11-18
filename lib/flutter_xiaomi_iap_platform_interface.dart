import 'package:flutter_xiaomi_iap/model/mi_buy_info.dart';
import 'package:flutter_xiaomi_iap/model/mi_buy_result.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_xiaomi_iap_method_channel.dart';
import 'model/mi_account_info.dart';

abstract class FlutterXiaomiIapPlatform extends PlatformInterface {
  /// Constructs a FlutterXiaomiIapPlatform.
  FlutterXiaomiIapPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterXiaomiIapPlatform _instance = MethodChannelFlutterXiaomiIap();

  /// The default instance of [FlutterXiaomiIapPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterXiaomiIap].
  static FlutterXiaomiIapPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterXiaomiIapPlatform] when
  /// they register themselves.
  static set instance(FlutterXiaomiIapPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> init({
    required String appId,
    required String appKey,
  }) {
    throw UnimplementedError('init() has not been implemented.');
  }

  Future<MiAccountInfo> miLogin({
    required int loginType,
    required String accountType,
    String? anonId,
  }) {
    throw UnimplementedError('miLogin() has not been implemented.');
  }

  Future<MiAccountInfo?> getLoginInfo() async {
    throw UnimplementedError('getLoginInfo() has not been implemented.');
  }

  Future<MiBuyResult> createPurchase({required MiBuyInfo miBuyInfo}) async {
    throw UnimplementedError('createPurchase() has not been implemented.');
  }
}
