import 'package:demo_filter/demo_facecontroller.dart';
import 'package:flutter/cupertino.dart';

import 'demo_androidview.dart';
typedef void DemofacecontrollerCreatedCallback(Demofacecontroller controller);
class Demofaceviw extends StatefulWidget{
  final DemofacecontrollerCreatedCallback? ondemo_facecontroller;
  final bool enableAugmentedFaces;
  final bool debug;

  const Demofaceviw(
      {Key? key,
        this.ondemo_facecontroller,
        this.enableAugmentedFaces = false,
        this.debug = false})
      : super(key: key);

  demo_faceviwstate  createState()=>demo_faceviwstate();
}

class demo_faceviwstate extends State<Demofaceviw> {
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