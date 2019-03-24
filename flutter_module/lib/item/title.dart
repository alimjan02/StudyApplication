import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter_module/values/colors.dart';

final double _appBarHeight = 50;
final double _statusBarHeight = MediaQueryData.fromWindow(window).padding.top;
final TextStyle defaultTextStyle = const TextStyle(
    color: Colors.white, fontSize: 15, fontWeight: FontWeight.normal);

class TitleBar extends StatefulWidget implements PreferredSizeWidget {
  final String title;
  final Color backgroundColor;
  final Widget backWidget /*返回箭头*/, centerWidget; //中间Title
  final Widget rightWidget;

  final OnTitleClickListener onClickListener;

  const TitleBar(
      {Key key,
      @required this.title,
      @required this.backgroundColor,
      this.backWidget,
      this.centerWidget,
      this.rightWidget,
      this.onClickListener})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return new TitleBarState(
        title: this.title,
        backgroundColor: this.backgroundColor,
        backWidget: this.backWidget,
        centerWidget: this.centerWidget,
        rightWidget: this.rightWidget,
        onClickListener:
            onClickListener == null ? new _OnClickListener() : onClickListener);
  }

  //appBar默认从状态栏开始,所以这里添加上状态栏的高度
  @override
  Size get preferredSize => Size.fromHeight(_appBarHeight + _statusBarHeight);
}

class TitleBarState extends State<TitleBar> {
  final String title;
  final Color backgroundColor;
  final Widget backWidget /*返回箭头*/, centerWidget; //中间Title
  final Widget rightWidget;

  final OnTitleClickListener onClickListener;

  TitleBarState(
      {Key key,
      @required this.title,
      @required this.backgroundColor,
      this.backWidget,
      this.centerWidget,
      this.rightWidget,
      this.onClickListener});

  @override
  Widget build(BuildContext context) {
    return new SafeArea(
        top: false, //默认为true,表示延伸至状态栏下面
        child: new Container(
          padding: EdgeInsets.only(top: _statusBarHeight),
          height: _appBarHeight + _statusBarHeight,
          //appBar默认从状态栏开始,所以这里添加上状态栏的高度
          color: backgroundColor,
          child: new Stack(
            children: <Widget>[
              Align(
                alignment: AlignmentDirectional.centerStart,
                child: Material(
                  color: backgroundColor != null
                      ? backgroundColor
                      : Theme.of(context).primaryColor,
                  child: InkWell(
                    borderRadius: BorderRadius.circular(50),
                    onTap: _onBackClick,
                    child: new Container(
                      width: _appBarHeight,
                      height: _appBarHeight,
                      child: backWidget != null
                          ? backWidget
                          : Icon(
                              Icons.arrow_back,
                              color: ColorStyle.black,
                            ),
                    ),
                  ),
                ),
              ),
              Align(
                alignment: AlignmentDirectional.center,
                child: centerWidget != null
                    ? centerWidget
                    : new Text(
                        title,
                        style: defaultTextStyle,
                      ),
              ),
              Align(
                alignment: AlignmentDirectional.centerEnd,
                child: Material(
                  color: backgroundColor != null
                      ? backgroundColor
                      : Theme.of(context).primaryColor,
                  child: InkWell(
                    borderRadius: BorderRadius.circular(50),
                    onTap: _onRightClick,
                    child: new Container(
                      padding: EdgeInsets.only(right: 16),
                      alignment: AlignmentDirectional.centerEnd,
                      width: _appBarHeight + 16,
                      height: _appBarHeight,
                      child:
                          rightWidget != null ? rightWidget : new Container(),
                    ),
                  ),
                ),
              )
            ],
          ),
        ));
  }

  _onBackClick() {
    if (onClickListener != null) {
      onClickListener.onBackClick();
    }
  }

  _onRightClick() {
    if (onClickListener != null) {
      onClickListener.onRightClick();
    }
  }
}

///Title点击事件,可以在widget中实现
abstract class OnTitleClickListener {
  void onBackClick();

  void onRightClick();
}

///一个点击事件的实现类,仅在当前 onClickListener==null 时调用
class _OnClickListener implements OnTitleClickListener {
  @override
  void onBackClick() async {
    print("super -> onBackClick");
  }

  @override
  void onRightClick() {
    print("super -> onRightClick");
  }
}
