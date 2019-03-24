import 'package:flutter/material.dart';

///工单无数据界面
class WidgetNoData extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new Container(
      alignment: AlignmentDirectional.topCenter,
      margin: EdgeInsets.only(top: 50, left: 30, right: 30),
      child: new Image.asset('images/gongdan_empty.png'),
    );
  }
}
