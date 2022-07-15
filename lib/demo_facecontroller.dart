import 'package:flutter/services.dart';

class Demofacecontroller {
  late MethodChannel _channel;

  //late StringResultHandler onError;
  Demofacecontroller(int id) {
    _channel = MethodChannel("demo_filter$id");
    _channel.setMethodCallHandler(_handleMethodCalls);
    init();
  }

  Future<dynamic> _handleMethodCalls(MethodCall call) async {
    switch (call.method) {
      case 'success':
        print("success : ${call.arguments}");
        break;
      case 'onError':
        print('Unknown method ${call.arguments}');
        // onError(call.arguments);
        break;
      default:
      /* if (debug) {
          print('Unknown method ${call.method}');
        }*/
        return Future.value();
    }
  }

  void init() async {
    try {
      await _channel.invokeMethod("init", "");
    } on Exception catch (e) {
      print(e.toString());
    }
  }


  void capture() async {
    try {
      await _channel.invokeMethod("capture",["true"]);
    }on Exception catch(e){
      print(e.toString());
    }
  }

  void loadmesh(int count)async{
    var data;
    if(count == 1){
      data = "toucan.sfb";
    }
    else{
      data = "fox_face.sfb";
    }
    try {
      await _channel.invokeMethod("loadmesh",{
        'skin3DModelFilename': data
      });
    }on Exception catch(e){
      print(e.toString());
    }
  }
}