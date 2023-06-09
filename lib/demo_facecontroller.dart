import 'package:flutter/services.dart';

class Demofacecontroller {
  late MethodChannel _channel;

  //late StringResultHandler onError;
  Demofacecontroller(int id) {
    _channel = MethodChannel("demo_filter$id");
    _channel.setMethodCallHandler((call) => handleMethodCalls(call));
    init();
  }

  Future<void>handleMethodCalls(MethodCall call) async {
    switch (call.method) {
      case 'success':
        print("success : ${call.arguments}");
        break;
      case 'onError':
        print('Unknown method ${call.arguments}');
        // onError(call.arguments);
        break;
      default:
        print('Unknown method ${call.method}, ${call.arguments}');
      /* if (debug) {
          print('Unknown method ${call.method}');
        }*/
    }
  }

  void init() async {
    try {
     var data = await _channel.invokeMethod("init", "");
     print("init responce "+data);
    } on Exception catch (e) {
      print(e.toString());
    }
  }


  void capture() async {
    try {
      var data =   await _channel.invokeMethod("record",{"onoff":"true"});
      print("capture responce "+data);
    }on Exception catch(e){
      print(e.toString());
    }
  }

  void loadmesh(var count,var data)async{

    // }
    // else{
    //   data = 'fox_face.sfb';
    // }
    //  final String textureBytes = "assets/fox_face_mesh_texture.png";
    // final ByteData texture = await rootBundle.load(count);
    try {
      await _channel.invokeMethod("loadmesh",{
        'skin3DModelFilename': data,
        'texturedata' : count
      });
    }on Exception catch(e){
      print(e.toString());
    }
  }
}