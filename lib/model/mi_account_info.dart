
import 'dart:convert';

class MiAccountInfo {
  final String? headImg;
  final String? nickName;
  final String? sessionId;
  final int? uid;
  final String? unionId;

  MiAccountInfo({
    this.headImg,
    this.nickName,
    this.sessionId,
    this.uid,
    this.unionId,
  });

  factory MiAccountInfo.fromJson(String str) =>
      MiAccountInfo.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory MiAccountInfo.fromMap(Map<String, dynamic> json) => MiAccountInfo(
    headImg: json["headImg"],
    nickName: json["nickName"],
    sessionId: json["sessionId"],
    uid: json["uid"],
    unionId: json["unionId"],
  );

  Map<String, dynamic> toMap() => {
    "headImg": headImg,
    "nickName": nickName,
    "sessionId": sessionId,
    "uid": uid,
    "unionId": unionId,
  };
}
