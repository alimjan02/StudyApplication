import 'package:flutter/material.dart';
import 'package:flutter_module/page/EmptyPage.dart';

class LoadingDialog extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: InkWell(
        child: Text('温馨提示'),
        onTap: () {
          Navigator.of(context).pop();
        },
      ),
      content: EmptyPage(),
    );
  }
}

class _Loading extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _LoadingState();
}

class _LoadingState extends State<_Loading> {
  @override
  Widget build(BuildContext context) {
    return null;
  }
}
