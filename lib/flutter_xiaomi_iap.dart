// ignore_for_file: constant_identifier_names

import 'package:flutter_xiaomi_iap/model/mi_account_info.dart';
import 'package:flutter_xiaomi_iap/model/mi_buy_info.dart';
import 'package:flutter_xiaomi_iap/model/mi_buy_result.dart';

import 'flutter_xiaomi_iap_platform_interface.dart';

enum MiLoginType {
  /// 自动登录优先
  AUTO_FIRST(0),

  /// 仅自动登录
  AUTO_ONLY(1),

  /// 仅手动登录
  MANUAL_ONLY(2);

  final int value;

  const MiLoginType(this.value);
}

enum MiAccountType {
  /// 自有账号
  APP('app'),

  /// 小米账号
  MI_SDK('mi_sdk');

  final String value;

  const MiAccountType(this.value);
}

enum MiPaymentType {
  /// 微信
  WXWAP('WXWAP'),

  /// 支付宝
  ALIPAY('ALIPAY');

  final String value;

  const MiPaymentType(this.value);
}

enum MiProductType {
  /// 消耗型
  CONSUMABLE(0),

  /// 非消耗型（永久型）
  PERMANENT(1),

  /// 订阅型
  SUBSCRIPTION(2);

  final int value;

  const MiProductType(this.value);
}

class FlutterXiaomiIap {
  /// 初始化小米 IAP SDK
  static Future<bool> init({
    required String appId,
    required String appKey,
  }) {
    return FlutterXiaomiIapPlatform.instance.init(appId: appId, appKey: appKey);
  }

  /// 小米账号/自有账号登录
  ///
  /// @param {MiLoginType} loginType 登录类型
  /// @param {MiAccountType} accountType 账户类型
  /// @param {String} anonId 自有账号ID，账户类型为MiAccountType.APP时，anonId必传
  /// @returns Object
  static Future<MiAccountInfo> miLogin({
    required MiLoginType loginType,
    required MiAccountType accountType,
    String? anonId,
  }) {
    return FlutterXiaomiIapPlatform.instance.miLogin(
      loginType: loginType.value,
      accountType: accountType.value,
      anonId: anonId,
    );
  }

  /// 获取登录信息，返回值为null：表示未登录
  static Future<MiAccountInfo?> getLoginInfo() async {
    return FlutterXiaomiIapPlatform.instance.getLoginInfo();
  }

  /// 创建按金额计费的订单。
  ///
  /// @param {MiProductType} productType 产品类型
  /// @param {String} orderNo 订单号
  /// @param {Number} feeValue 支付金额(单位：分)
  /// @param {MiPaymentType} paymentType 优先支付方式
  /// @returns Object
  static Future<MiBuyResult> createPurchaseByFeeValue({
    required MiProductType productType,
    required String orderNo,
    required int feeValue,
    MiPaymentType? paymentType = MiPaymentType.ALIPAY,
  }) {
    return FlutterXiaomiIapPlatform.instance.createPurchase(
      miBuyInfo: MiBuyInfo.fromMap(
        {
          'productType': productType.value,
          'cpOrderId': orderNo,
          'feeValue': feeValue,
          'paymentType': paymentType?.value ?? '',
          'createType': 0,
        },
      ),
    );
  }

  /// 创建按计费代码计费的订单(订阅产品也调用此方法)。
  ///
  /// @param {MiProductType} productType 产品类型
  /// @param {String} orderNo 订单号
  /// @param {String} productCode 产品code
  /// @param {MiPaymentType} paymentType 优先支付方式
  /// @returns Object
  static Future<MiBuyResult> createPurchaseByProductCode({
    required MiProductType productType,
    required String orderNo,
    required String productCode,
    int? quantity = 1,
    MiPaymentType? paymentType = MiPaymentType.ALIPAY,
  }) {
    return FlutterXiaomiIapPlatform.instance.createPurchase(
      miBuyInfo: MiBuyInfo.fromMap(
        {
          'productType': productType.value,
          'cpOrderId': orderNo,
          'productCode': productCode,
          'quantity': quantity,
          'paymentType': paymentType?.value ?? '',
          'createType': 1,
        },
      ),
    );
  }
}
