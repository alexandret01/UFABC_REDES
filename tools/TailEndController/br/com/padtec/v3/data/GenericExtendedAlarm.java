
package br.com.padtec.v3.data;

import java.util.StringTokenizer;

public class GenericExtendedAlarm extends Alarm implements ExtendedAlarm {
  private static final long serialVersionUID = 1L;
  private String slot;
  private String detail;

  public GenericExtendedAlarm(SerialNumber serial, int type, Integer contact)
  {
    super(serial, type, contact);
    if (getContact() == null)
      setContact(-1);
  }

  public String getSlot()
  {
    return this.slot;
  }

  public void setSlot(String slot) {
    this.slot = slot;
  }

  public String getDetail() {
    return this.detail;
  }

  public void setDetail(String detail)
  {
    this.detail = detail;
    if (detail == null) {
      return;
    }
    StringTokenizer st = new StringTokenizer(detail, ";");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.startsWith("NC"))
      {
        setAlarmName(getAlarmName() + " (" + token.substring(2) + ")"); } else {
        if (!(token.startsWith("DC")))
          continue;
        setDescription(getDescription() + " (" + token.substring(2) + ")");
      }
    }

    st = null;
  }
}