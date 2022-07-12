
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'demo_androidview.dart';
import 'demo_facecontroller.dart';
import 'face_view.dart';
typedef void DemofacecontrollerCreatedCallback(Demofacecontroller controller);
class DemoFilter extends StatefulWidget {
 /* static const MethodChannel _channel = MethodChannel('demo_filter');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;*/
  final DemofacecontrollerCreatedCallback? ondemo_facecontroller;
 /* final bool enableAugmentedFaces;
  final bool debug;*/

  const DemoFilter(
      {Key? key,
        this.ondemo_facecontroller,})
      : super(key: key);

  demo_faceviwstate  createState()=>demo_faceviwstate();
}

class demo_faceviwstate extends State<DemoFilter> {
  @override
  Widget build(BuildContext context) {
    return Container(
      child: Demoandroidview( onPlatformViewCreated: _onPlatformViewCreated,
        /*arCoreViewType: ArCoreViewType.AUGMENTEDFACE,*/),
    );
  }
  void _onPlatformViewCreated(int id) {
    widget.ondemo_facecontroller!(
      Demofacecontroller(
        id,
        /* enableAugmentedFaces: widget.enableAugmentedFaces,*/
      ),
    );
  }
}
