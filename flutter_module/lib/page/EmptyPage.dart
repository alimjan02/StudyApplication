import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_module/item/LoadingDialog.dart';
import 'package:flutter_module/values/Strings.dart';

class EmptyPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: AnnotatedRegion(
          value: SystemUiOverlayStyle.dark.copyWith(
              statusBarColor: Colors.black.withOpacity(0),
              systemNavigationBarColor: Colors.white,
              systemNavigationBarIconBrightness: Brightness.dark),
          child: Scaffold(
            backgroundColor: Colors.white,
            body: _Empty(),
          )),
    );
  }
}

class _Empty extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(child: Container(
      color: Colors.black.withOpacity(0.5),
      alignment: Alignment.center,
      padding: EdgeInsets.only(top: MediaQuery.of(context).padding.top),
      child: Column(
        children: <Widget>[
          Icon(
            Icons.style,
            size: 80,
            color: Colors.green,
          ),
          SizedBox(height: 10),
          Builder(builder: (context) {
            return InkWell(
              onTap: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) {
                  return LoadingDialog();
                }));
              },
              child: Text(
                Strings.nothing,
                style: TextStyle(color: Colors.black, fontSize: 18),
              ),
            );
          }),
        ],
      ),
    ),);
  }
}
