import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_xiaomi_iap/flutter_xiaomi_iap.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _isInstalled = false;
  bool _isLogin = false;
  String _logMsg = '';
  String appId = '2882303761520305359';
  String appKey = '5732030596359';

  final normalTextStyle = TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.w500,
    color: Colors.blue[900],
  );
  final disableTextStyle = TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.w500,
    color: Colors.grey[500],
  );

  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  void _initXiaomiIap() async {
    if (!_isInstalled) {
      _isInstalled = await FlutterXiaomiIap.init(appId: appId, appKey: appKey);
      _logMsg = _isInstalled ? '初始化成功' : '初始化失败';
      setState(() {});
    }
  }

  void _getLoginInfo() async {
    try {
      final result = await FlutterXiaomiIap.getLoginInfo();
      setState(() { _logMsg = result != null ? result.toJson() : '未登录';  });
      debugPrint('获取小米账号/自有账号登录信息 === $_logMsg');
    } on PlatformException catch (e) {
      setState(() { _logMsg = e?.message ?? '获取小米账号/自有账号登录失败'; });
    }
  }

  void _miLogin({
    required MiLoginType loginType,
    required MiAccountType accountType,
    String? anonId,
  }) async {
    try {
      final result = await FlutterXiaomiIap.miLogin(
        loginType: loginType,
        accountType: accountType,
        anonId: anonId,
      );
      setState(() { _logMsg = result.toJson(); _isLogin = true; });
      debugPrint('小米账号/自有账号登录 === $_logMsg');
    } on PlatformException catch (e) {
      setState(() { _logMsg = e?.message ?? '小米账号/自有账号登录失败'; _isLogin = false; });
    }
  }

  void _createPurchaseByProductCode() async {
    try {
      final result = await FlutterXiaomiIap.createPurchaseByProductCode(
        productType: MiProductType.CONSUMABLE,
        orderNo: '4563567468687nfhkusfgdghfdskjghtiugh',
        productCode: 'consumetest',
        paymentType: null,
      );
      setState(() { _logMsg = result.toJson(); });
    } on PlatformException catch (e) {
      setState(() { _logMsg = e?.message ?? '创建按计费代码计费订单失败'; });
    }
  }

  void _createPurchaseByFeeValue() async {
    try {
      final result = await FlutterXiaomiIap.createPurchaseByFeeValue(
        productType: MiProductType.CONSUMABLE,
        orderNo: '4563567468687nfhkusfgdghfdskjghtiugh',
        feeValue: 1,
        paymentType: null,
      );
      setState(() { _logMsg = result.toJson(); });
    } on PlatformException catch (e) {
      setState(() { _logMsg = e?.message ?? '创建按金额计费订单失败'; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Xiaomi IAP Plugin Demo'),
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Visibility(
                  visible: _logMsg.isNotEmpty,
                  child: Container(
                    padding: const EdgeInsets.all(15),
                    color: Colors.grey[100],
                    child: Text(
                      _logMsg,
                      style: const TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w400,
                        color: Colors.black,
                      ),
                    ),
                  ),
                ),
                Visibility(
                  visible: !_isInstalled,
                  child: TextButton(
                    onPressed: _initXiaomiIap,
                    child: Text(
                      '初始化',
                      style: _isInstalled ? disableTextStyle : normalTextStyle,
                    ),
                  ),
                ),
                _buildAction('获取登录信息(状态)', () => _getLoginInfo(), true),
                _buildAction('小米账号登录(自动登录优先)', () => _miLogin(loginType: MiLoginType.AUTO_FIRST, accountType: MiAccountType.MI_SDK), !_isLogin),
                _buildAction('小米账号登录(仅自动登录)', () => _miLogin(loginType: MiLoginType.AUTO_ONLY, accountType: MiAccountType.MI_SDK), !_isLogin),
                _buildAction('小米账号登录(仅手动登录)', () => _miLogin(loginType: MiLoginType.MANUAL_ONLY, accountType: MiAccountType.MI_SDK), !_isLogin),
                _buildAction('创建按计费代码计费订单', () => _createPurchaseByProductCode(), _isLogin),
                _buildAction('创建按金额计费订单', () => _createPurchaseByFeeValue(), _isLogin),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildAction(String title, void Function()? onPressed, bool visible) {
    return Visibility(
      visible: visible,
      child: Padding(
        padding: const EdgeInsets.only(top: 20),
        child: TextButton(
          onPressed: () {
            if (!_isInstalled) {
              setState(() => _logMsg = '未初始化，请先初始化');
              return;
            }
            if (onPressed != null) onPressed!();
          },
          child: Text(
            title,
            style: _isInstalled ? normalTextStyle : disableTextStyle,
            textAlign: TextAlign.center,
          ),
        ),
      ),
    );
  }
}
