import 'dart:convert';

class MiBuyResult {
  final String? info;
  final String? msg;
  final String? code;

  MiBuyResult({
    this.info,
    this.msg,
    this.code,
  });

  factory MiBuyResult.fromJson(String str) =>
      MiBuyResult.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory MiBuyResult.fromMap(Map<String, dynamic> map) {
    return MiBuyResult(
      info: map['info'],
      msg: map['msg'],
      code: map['code'],
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'info': info,
      'msg': msg,
      'code': code,
    };
  }

  @override
  String toString() {
    return 'MiBuyResult{info: $info, msg: $msg, code: $code}';
  }
}
