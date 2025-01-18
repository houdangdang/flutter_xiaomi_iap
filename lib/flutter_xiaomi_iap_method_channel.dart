import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_xiaomi_iap_platform_interface.dart';
import 'model/mi_buy_info.dart';
import 'model/mi_buy_result.dart';
import 'model/mi_account_info.dart';

/// An implementation of [FlutterXiaomiIapPlatform] that uses method channels.
class MethodChannelFlutterXiaomiIap extends FlutterXiaomiIapPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_xiaomi_iap');

  /// 初始化小米 IAP SDK
  @override
  Future<bool> init({
    required String appId,
    required String appKey,
  }) async {
    bool result = await methodChannel.invokeMethod(
      'init',
      <String, String>{'appId': appId, 'appKey': appKey},
    );
    return result;
  }

  /// 小米账号/自有账号登录
  @override
  Future<MiAccountInfo> miLogin({
    required int loginType,
    required String accountType,
    String? anonId,
  }) async {
    final result = await methodChannel.invokeMethod(
      'miLogin',
      <String, dynamic>{
        'loginType': loginType,
        'accountType': accountType,
        'anonId': anonId
      },
    );
    return MiAccountInfo.fromJson(result);
  }

  /// 获取登录信息，null：表示未登录
  @override
  Future<MiAccountInfo?> getLoginInfo() async {
    final result = await methodChannel.invokeMethod('getLoginInfo');
    return result != null ? MiAccountInfo.fromJson(result) : null;
  }

  /// 获取支付、订阅结果
  @override
  Future<MiBuyResult> createPurchase({required MiBuyInfo miBuyInfo}) async {
    final result = await methodChannel.invokeMethod(
      'createPurchase',
      miBuyInfo.toMap(),
    );
    return MiBuyResult.fromJson(result);
  }

  /// 是否启用支付按钮的模拟点击
  @override
  Future<bool> setClickEnabled({ required bool enable }) async {
    bool result = await methodChannel.invokeMethod(
      'setClickEnabled',
      <String, dynamic>{'enable': enable},
    );
    return result;
  }
}
