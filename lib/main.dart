import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:demo_filter/demo_facecontroller.dart';
import 'package:flutter/services.dart';
import 'package:demo_filter/demo_filter.dart';
late List<CameraDescription> _camera;
void main() async{
WidgetsFlutterBinding.ensureInitialized();
//_camera = await availableCameras();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  late Demofacecontroller demofacecontroller;
 // late CameraController controller;
  @override
  void initState() {
    super.initState();
    /*controller = CameraController(_camera[1],ResolutionPreset.max);
    controller.initialize().then((_) {
      if (!mounted) {
        return;
      }
      setState(() {});
    }).catchError((Object e) {
      if (e is CameraException) {
        switch (e.code) {
          case 'CameraAccessDenied':
            print('User denied camera access.');
            break;
          default:
            print('Handle other errors.');
            break;
        }
      }
    });*/
   // initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  /*Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await DemoFilter.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }*/

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.

int count = 0;
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        // appBar: AppBar(
        //   title: const Text('Plugin example app'),
        // ),
        body:  Stack(
          children: [

            DemoFilter(
          ondemo_facecontroller: (Demofacecontroller conroler){
            demofacecontroller = conroler;
            demofacecontroller.init();
          },
            ),
           /* Positioned(
              top: 120,
              bottom: 1.5,
              child: MaterialButton(
                height: 120,
                onPressed: (){
                  if(demofacecontroller!=null){
                    demofacecontroller.loadmesh();
                  }
                },*/
                //color: Colors.amber,),
      //      ),
    Positioned(
              left: 150,
              right: 150,
              bottom: 20.5,
              child: Container(
                //height: 12,
                  width: 5,
                child: MaterialButton(
                  shape:RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20),
                  ),
                  height: 40,
                  child: Icon(Icons.camera_alt_outlined,color: Colors.white,),
                  onPressed: (){
                    if(demofacecontroller!=null){
                      count ++;
                      demofacecontroller.loadmesh(count);
                    }
                  },
                  color: Colors.blueGrey,
                   ),
              ),
            ),
          ],
        )/*Text('Running on: $_platformVersion\n')*/,
      ),
    );
  }
}
