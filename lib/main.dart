import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Screenshot Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  Uint8List? _screenshotBytes;

  Future<void> _takeScreenshot() async {
    try {
      final Uint8List? bytes = await const MethodChannel('screenshot_channel')
          .invokeMethod('takeScreenShot');
      setState(() {
        _screenshotBytes = bytes;
      });
    } on PlatformException catch (e) {
      print("Failed to take screenshot: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Screenshot Example'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: _takeScreenshot,
              child: const Text('Take Screenshot'),
            ),
            const SizedBox(height: 20),
            if (_screenshotBytes != null)
              Image.memory(
                _screenshotBytes!,
                width: 200,
                height: 200,
              ),
          ],
        ),
      ),
    );
  }
}
