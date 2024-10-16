import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
// import 'dart:async';
import 'package:flutter/services.dart';

// import 'package:permission_handler/permission_handler.dart';
// import 'package:permission_handler/permission_handler.dart';

Future<void> main() async {
  await requestNotificationPermission();
  runApp(MaterialApp(
    debugShowCheckedModeBanner: false,
    home: HomePage(),
  ));
}

Future<void> requestNotificationPermission() async {
  // if (await Permission.notification.isDenied) {
  //   await Permission.notification.request();
  // }
}

class HomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Platform Specific Codding',
          style: TextStyle(color: Colors.black),
        ),
        centerTitle: true,
        elevation: 0,
        backgroundColor: Colors.white,
      ),
      body: const MyHomePage(
        title: 'Method Channel',
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // Create a methodChannel named "flutter.native/helper"
  // and assigned in platform variable.
  // By this variable we can call Native Method.
  static const platform = MethodChannel('flutter.native/helper');

  @override
  Widget build(BuildContext context) {
    return Material(
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(
                height: 24,
              ),
              CupertinoButton(
                  color: Colors.blueAccent,
                  child: const Text("Show Notification From Native"),
                  onPressed: () async {
                    try {
                      //Invoke a method named "startNativeActivity"
                      //startNativeActivity is the name of a function located in
                      //MainActivity that can be call from here.
                      await platform
                          .invokeMethod('showNotificationFromNative', {
                        "title": "Test Title",
                        "message":
                            "This is a Simple Notification Thrown from Native"
                      });
                    } on PlatformException catch (e) {
                      print("Failed to Invoke: '${e.message}'.");
                    }
                  }),
              const SizedBox(
                height: 8,
              ),
              CupertinoButton(
                color: Colors.lightGreen,
                child: const Text("Show Type2 Notification From Native"),
                onPressed: () async {
                  try {
                    int seconds = 0;
                    bool isFirstUpdate = true;
                    int estimateProgress = 20;

                    Timer.periodic(const Duration(seconds: 1), (timer) async {
                      seconds++;
                      if (seconds > estimateProgress) {
                        timer.cancel();
                        return;
                      }

                      await platform
                          .invokeMethod('showChargingNotificationMethod', {
                        'bike_name': 'Raptrix',
                        'notification_id': 123,
                        'estimate_percentage': estimateProgress,
                        'estimate_time': '9:30 AM',
                        'current_percentage': seconds,
                        'is_first_update': isFirstUpdate,
                        'isType2': true,
                        'isStillCharging': seconds != estimateProgress,
                      });

                      if (isFirstUpdate) {
                        isFirstUpdate = false;
                      }
                    });
                  } on PlatformException catch (e) {
                    print("Failed to Invoke: '${e.message}'.");
                  }
                },
              ),
              const SizedBox(
                height: 8,
              ),
              CupertinoButton(
                color: Colors.purple,
                child: const Text("Show Type3 Notification From Native"),
                onPressed: () async {
                  try {
                    int seconds = 0;
                    bool isFirstUpdate = true;
                    int estimateProgress = 20;

                    Timer.periodic(const Duration(seconds: 1), (timer) async {
                      seconds++;
                      if (seconds > estimateProgress) {
                        timer.cancel();
                        return;
                      }

                      await platform
                          .invokeMethod('showChargingNotificationMethod', {
                        'bike_name': 'Raptrix',
                        'notification_id': 123123,
                        'estimate_percentage': estimateProgress,
                        'estimate_time': '9:30 AM',
                        'current_percentage': seconds,
                        'is_first_update': isFirstUpdate,
                        'isType2': false,
                        'isStillCharging': seconds != estimateProgress,
                      });

                      if (isFirstUpdate) {
                        isFirstUpdate = false;
                      }
                    });
                  } on PlatformException catch (e) {
                    print("Failed to Invoke: '${e.message}'.");
                  }
                },
              ),

              const SizedBox(
                height: 10,
              ),
              // show bike movement detection notification.
              CupertinoButton(
                color: Colors.redAccent,
                child: const Text("Show Bike Movement Notification"),
                onPressed: () async {
                  try {
                    bool isFirstUpdate = true;

                    await platform
                        .invokeMethod('showBikeMovementNotificationMethod', {
                      'bike_name': 'Raptrix',
                      'notification_id': 123123,
                      'is_moving': true,
                      'is_first_update': isFirstUpdate,
                    });
                  } on PlatformException catch (e) {
                    print("Failed to Invoke: '${e.message}'.");
                  }
                },
              ),
              const SizedBox(
                height: 8,
              ),
              CupertinoButton(
                color: Colors.teal,
                child: const Text("Show Bike Stats Notification"),
                onPressed: () async {
                  try {
                    await platform
                        .invokeMethod('showBikeStatsNotificationMethod', {
                      'bike_name': 'Raptrix',
                      'notification_id': 444444,
                    });
                  } on PlatformException catch (e) {
                    print("Failed to Invoke: '${e.message}'.");
                  }
                },
              ),
              const SizedBox(
                height: 8,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
