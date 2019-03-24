import 'package:intl/intl.dart';

class DateFormatUtil {
  static const String DATE_HH_MM = 'HH:mm';
  static const String DATE_YY = 'yyyy';
  static const String DATE_MM = 'MM';
  static const String DATE_DD = 'dd';
  static const String DATE_MM_DD = 'MM-dd';
  static const String DATE_YY_MM = 'yyyy-MM';
  static const String DATE_YY_MM_DD = 'yyyy-MM-dd';
  static const String DATE_ALL = 'yyyy-MM-dd HH:mm:ss';

  ///毫秒值转换成日期
  static String getDateFromSeconds(num seconds, String flag) {
    var format = new DateFormat(flag);
    return format.format(DateTime.fromMillisecondsSinceEpoch(seconds));
  }

  ///日期转换为毫秒值
  static String getSecondsFromDate(String date) {
    var format = new DateFormat(DATE_ALL);
    return format.parse(date).toString();
  }
}
