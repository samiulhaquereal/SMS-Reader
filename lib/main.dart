import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:convert';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('com.example.smsreader/sms');
  List<Map<String, dynamic>> _smsMessages = [];

  @override
  void initState() {
    super.initState();
    _getSmsMessages();
  }

  Future<void> _getSmsMessages() async {
    try {
      final String result = await platform.invokeMethod('getSms');
      List<Map<String, dynamic>> smsMessages = List<Map<String, dynamic>>.from(json.decode(result));
      setState(() {
        _smsMessages = smsMessages;
      });
    } on PlatformException catch (e) {
      setState(() {
        _smsMessages = [{"error": "Failed to get SMS messages: '${e.message}'."}];
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('SMS Reader'),
        ),
        body: ListView.builder(
          padding: EdgeInsets.all(16.0),
          itemCount: _smsMessages.length,
          itemBuilder: (context, index) {
            final sms = _smsMessages[index];
            final date = DateTime.fromMillisecondsSinceEpoch(sms['date']);
            log(_smsMessages.toString());
            return Card(
              child: Padding(
                padding: EdgeInsets.all(8.0),
                child: ListTile(
                  title: Text('From: ${sms['address']}' ?? 'Unknown'),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(sms['body'] ?? 'No content'),
                      Text('Date: ${date.toString()}'),
                    ],
                  ),
                )
              ),
            );
          },
        ),
      ),
    );
  }
}
