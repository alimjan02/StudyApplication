import 'dart:ui';

import 'package:flutter/material.dart';

class TitleBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final bool isShowBack;
  final Color backgroundColor;
  final Widget backWidget /*返回箭头*/, centerWidget; //中间Title
  final Widget rightWidget; //右侧的布局
  final Widget rootContainer; //替换整个Title
  final TextStyle titleStyle;
  final bool top; //是否延伸至状态栏
  final double appBarHeight;

  final void Function() onRightClick;
  final void Function() onBackClick;

  const TitleBar({
    Key key,
    @required this.title,
    this.isShowBack = true,
    this.backgroundColor,
    this.appBarHeight = 50,
    this.backWidget,
    this.centerWidget,
    this.rightWidget,
    this.titleStyle,
    this.rootContainer,
    this.onRightClick,
    this.onBackClick,
    this.top = false,
  }) : super(key: key);

  @override
  Size get preferredSize {
    double _statusBarHeight = MediaQueryData.fromWindow(window).padding.top;
    return Size.fromHeight(
        top ? appBarHeight + _statusBarHeight : appBarHeight);
  }

  @override
  Widget build(BuildContext context) {
    double _statusBarHeight = MediaQueryData.fromWindow(window).padding.top;
    print(
        'appBarHeight : $appBarHeight , _statusBarHeight : $_statusBarHeight');
    return new SafeArea(
        top: top,
        child: new Container(
          padding: EdgeInsets.only(top: top ? 0 : _statusBarHeight),
          height: appBarHeight + _statusBarHeight,
          //appBar默认从状态栏开始,所以这里添加上状态栏的高度
          color: backgroundColor != null
              ? backgroundColor
              : Theme.of(context).primaryColor,
          child: rootContainer != null
              ? rootContainer
              : new Stack(
                  children: <Widget>[
                    !isShowBack
                        ? Container()
                        : Align(
                            alignment: AlignmentDirectional.centerStart,
                            child: Material(
                              color: backgroundColor != null
                                  ? backgroundColor
                                  : Theme.of(context).primaryColor,
                              child: InkWell(
                                borderRadius: BorderRadius.circular(50),
                                onTap: () {
                                  _onBackClick(context);
                                },
                                child: new Container(
                                  width: appBarHeight,
                                  height: appBarHeight,
                                  child: backWidget != null
                                      ? backWidget
                                      : Image.asset(
                                          'assets/images/ic_back_white.png'),
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
                              style: titleStyle != null ? titleStyle : null,
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
                          onTap: () {
                            _onRightClick();
                          },
                          child: new Container(
                            padding: EdgeInsets.only(right: 16),
                            alignment: AlignmentDirectional.centerEnd,
                            width: appBarHeight + 16,
                            height: appBarHeight,
                            child: rightWidget != null
                                ? rightWidget
                                : new Container(),
                          ),
                        ),
                      ),
                    ),
                    Align(
                      alignment: AlignmentDirectional.bottomCenter,
                      child: //分割线
                          new Container(
                        child: new Divider(
                            height: 1,
                            color: Theme.of(context).backgroundColor),
                      ),
                    ),
                  ],
                ),
        ));
  }

  _onBackClick(BuildContext context) {
    if (onBackClick != null) {
      onBackClick();
    } else {
      Navigator.pop(context);
    }
  }

  _onRightClick() {
    if (onRightClick != null) {
      onRightClick();
    }
  }
}
