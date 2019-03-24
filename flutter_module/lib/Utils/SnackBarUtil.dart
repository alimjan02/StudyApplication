import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class SnackBarUtil {
  static GlobalKey<ScaffoldState> scaffoldKey;

  static void showSnackBar(GlobalKey<ScaffoldState> scaffoldKey, String msg) {
    if (scaffoldKey != null && msg != null) {
      SnackBarUtil.scaffoldKey = scaffoldKey;
      scaffoldKey.currentState.showSnackBar(new SnackBar(
        content: new Text(msg),
        duration: Duration(milliseconds: 800),
      ));
    }
  }
}
