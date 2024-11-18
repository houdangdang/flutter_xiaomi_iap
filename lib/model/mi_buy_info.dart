import 'dart:convert';

class MiBuyInfo {
  final int? productType;
  final String? cpOrderId;
  final String? productCode;
  final String? paymentType;
  final int? quantity;
  final int? feeValue;
  final int? createType;

  MiBuyInfo({
    this.productType,
    this.cpOrderId,
    this.productCode,
    this.paymentType,
    this.quantity,
    this.feeValue,
    this.createType,
  });

  factory MiBuyInfo.fromJson(String str) => MiBuyInfo.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory MiBuyInfo.fromMap(Map<String, dynamic> map) {
    return MiBuyInfo(
      productType: map['productType'],
      cpOrderId: map['cpOrderId'],
      productCode: map['productCode'],
      paymentType: map['paymentType'],
      quantity: map['quantity'],
      feeValue: map['feeValue'],
      createType: map['createType'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'productType': productType,
      'cpOrderId': cpOrderId,
      'productCode': productCode,
      'paymentType': paymentType,
      'quantity': quantity,
      'feeValue': feeValue,
      'createType': createType,
    };
  }
}
