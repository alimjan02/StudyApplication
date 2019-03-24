import 'package:flutter/material.dart';
import 'package:flutter_module/values/colors.dart';

class OrderTimeSelect<T> extends StatelessWidget {
  final List<String> taskTimeRecent;
  final OnItemSelectListener onItemSelectListener;

  const OrderTimeSelect(this.taskTimeRecent,
      {Key key, this.onItemSelectListener})
      : assert(taskTimeRecent != null),
        super(key: key);

  @override
  Widget build(BuildContext context) {
    return new Container(
      alignment: AlignmentDirectional.bottomCenter,
      height: 200,
      child: new ListView.builder(
          physics: BouncingScrollPhysics(),
          itemCount: taskTimeRecent.length,
          itemBuilder: (context, index) {
            return InkWell(
              onTap: () {
                if (onItemSelectListener != null) {
                  onItemSelectListener.onItemSelect(
                      taskTimeRecent[index], index);
                }
                Navigator.of(context).pop();
              },
              child: Column(
                children: <Widget>[
                  new Container(
                    alignment: AlignmentDirectional.center,
                    width: MediaQuery.of(context).size.width,
                    color: ColorStyle.white,
                    padding: EdgeInsets.all(15),
                    child: new Text(
                      taskTimeRecent[index],
                      style: TextStyle(color: ColorStyle.black, fontSize: 15),
                    ),
                  ),
                  new Divider(
                    height: 1,
                    color: ColorStyle.gray_e5e5e5,
                  ),
                ],
              ),
            );
          }),
    );
  }
}

abstract class OnItemSelectListener<T> {
  void onItemSelect(T t, int index);
}
