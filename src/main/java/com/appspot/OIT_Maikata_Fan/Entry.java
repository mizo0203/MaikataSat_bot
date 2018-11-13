package com.appspot.OIT_Maikata_Fan;

// url は最大23文字
public class Entry {
    private final String text;
    private final String url;

    Entry(String text, String url) {
        if (text.length() > 116) {
            text = text.substring(0, 115);
            text += "…";
        }
        this.text = text;
        this.url = url;
    }

    Entry(String text) {
        if (text.length() > 140) {
            text = text.substring(0, 139);
            text += "…";
        }
        this.text = text;
        this.url = null;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public String getAllText() {
        if (url != null) {
            return text + "\n" + url;
        } else {
            return text;
        }
    }

  /*
  public boolean checkOld(Site site) {
    if (text.equalsIgnoreCase(site.getTitle()))
      return true;
    else
      return false;
  }
  */
}
