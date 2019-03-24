import 'package:flutter/material.dart';

class OrderTitleWidget extends StatelessWidget {
  final time; //工单日期
  final num; //签出单数

  const OrderTitleWidget({Key key, @required this.time, @required this.num})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Card(
      elevation: 1,
      margin: EdgeInsets.only(bottom: 8),
      child: new Container(
        alignment: AlignmentDirectional.center,
        color: Colors.white,
        padding: EdgeInsets.only(left: 16, top: 10, right: 16, bottom: 10),
        child: new Stack(
          alignment: AlignmentDirectional.center,
          children: <Widget>[
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: Text(
                time,
                style: TextStyle(color: Colors.black, fontSize: 15),
              ),
            ),
            Align(
              alignment: AlignmentDirectional.centerEnd,
              child: Text(
                num,
                style: TextStyle(color: Colors.black, fontSize: 15),
              ),
            )
          ],
        ),
      ),
    );
  }
}
