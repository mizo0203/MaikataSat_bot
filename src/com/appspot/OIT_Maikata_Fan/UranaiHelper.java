package com.appspot.OIT_Maikata_Fan;

import java.util.*;
import java.util.TimeZone;
import java.util.logging.*;

import javax.jdo.*;

//IT技術による暦占い
public final class UranaiHelper {
  static final Logger log = Logger.getLogger(UranaiHelper.class.getName());

  static private Uranai getUranai(PersistenceManager pm) {
    try {
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)
      String date = (calendar.get(Calendar.MONTH) + 1) + "月"
          + calendar.get(Calendar.DAY_OF_MONTH) + "日";
      return pm.getObjectById(Uranai.class, date);
    } catch (Exception e) {
      return null;
    }
  }

  static void checkUranai(PersistenceManager pm) {
    Stack<Entry> stack = new Stack<Entry>();
    Uranai uranai = getUranai(pm);
    if (uranai != null) {
      Entry entry = new Entry(uranai.getAllText());
      stack.push(entry);
      TwitterHelper.tweetEntry(pm, stack);
    }
  }

}
