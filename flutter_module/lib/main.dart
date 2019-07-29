import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_module/page/ChartPage.dart';
import 'package:flutter_module/page/EmptyPage.dart';
import 'package:flutter_module/values/Routes.dart';

void main() => runApp(_switchRoutes(window.defaultRouteName));

Widget _switchRoutes(String defaultRouteName) {
  print("接收到路由 Route ： $defaultRouteName");
  switch (defaultRouteName) {
    case Routes.Route_Chart:
      return ChartPage();
    default:
      return EmptyPage();
  }
}
