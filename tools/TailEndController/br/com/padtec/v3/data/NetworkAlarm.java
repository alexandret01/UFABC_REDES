
package br.com.padtec.v3.data;

import java.io.Serializable;
import java.util.Date;

public final class NetworkAlarm implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Long networkAlarmId;
  private ElementType elementType;
  private long elementId;
  private String elementName;
  private String networkName;
  private String neName;
  private String alarmName;
  private int alarmPriority;
  private boolean cleared;
  private Layer layer;
  private Alarm.Type type;
  private Date startDate;
  private boolean ack;

  public NetworkAlarm()
  {
  }

  public NetworkAlarm(long networkAlarmId, ElementType elementType, long elementId, String elementname, String networkName, String neName, Alarm alarm)
  {
    setNetworkAlarmId(Long.valueOf(networkAlarmId));
    setElementType(elementType);
    setElementId(elementId);
    setElementName(elementname);
    setNetworkName(networkName);
    setNeName(neName);
    setAlarmName(alarm.getAlarmName());
    setAlarmPriority(alarm.getPriority());
    setCleared(alarm.isCleared());
    setLayer(alarm.getNetworkLayer());
    setType(alarm.getType());
    setStartDate(alarm.getStartDate());
    setAck(alarm.isAck());
  }

  public boolean isAck()
  {
    return this.ack;
  }

  public void setAck(boolean ack)
  {
    this.ack = ack;
  }

  public String getAlarmName()
  {
    return this.alarmName;
  }

  public void setAlarmName(String alarmName)
  {
    this.alarmName = alarmName;
  }

  public int getAlarmPriority()
  {
    return this.alarmPriority;
  }

  public void setAlarmPriority(int alarmPriority)
  {
    this.alarmPriority = alarmPriority;
  }

  public long getElementId()
  {
    return this.elementId;
  }

  public void setElementId(long elementId)
  {
    this.elementId = elementId;
  }

  public String getElementName()
  {
    return this.elementName;
  }

  public void setElementName(String elementName)
  {
    this.elementName = elementName;
  }

  public ElementType getElementType()
  {
    return this.elementType;
  }

  public void setElementType(ElementType elementType)
  {
    this.elementType = elementType;
  }

  public Layer getLayer()
  {
    return this.layer;
  }

  public void setLayer(Layer layer)
  {
    this.layer = layer;
  }

  public String getNeName()
  {
    return this.neName;
  }

  public void setNeName(String neName)
  {
    this.neName = neName;
  }

  public Long getNetworkAlarmId()
  {
    return this.networkAlarmId;
  }

  public void setNetworkAlarmId(Long networkAlarmId)
  {
    this.networkAlarmId = networkAlarmId;
  }

  public String getNetworkName()
  {
    return this.networkName;
  }

  public void setNetworkName(String networkName)
  {
    this.networkName = networkName;
  }

  public Date getStartDate()
  {
    return this.startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Alarm.Type getType()
  {
    return this.type;
  }

  public void setType(Alarm.Type type)
  {
    this.type = type;
  }

  public boolean isCleared()
  {
    return this.cleared;
  }

  public void setCleared(boolean cleared)
  {
    this.cleared = cleared;
  }

  public static enum ElementType
  {
//    RELATION, NETWORK_LINK, CIRCUIT;
	  RELATION(10),
	  NETWORK_LINK(20),
	  CIRCUIT(30);

    private int id;

    private ElementType(int id){
    	this.id = id;
    }
    public int getId()
    {
      return this.id;
    }

    public static ElementType getType(int id)
    {
      for (ElementType type : values()) {
        if (type.getId() == id) {
          return type;
        }
      }
      return null;
    }
  }

  public static enum Layer
  {
//    OTS, OTS_CLIENTE, OCH, CIRCUITO, OAM, OMS, OTU_1, ODU_1, SDH, OTU_2, ODU_2, OPU_2, EQUIPAMENTO, GERENCIA;

    OTS(100),
    OTS_CLIENTE(101),
    OCH(102),
    CIRCUITO(103),
    OAM(104),
    OMS(105),
    OTU_1(200),
    ODU_1(201),
    SDH(202),
    OTU_2(300),
    ODU_2(301),
    OPU_2(302),
    EQUIPAMENTO(400),
    GERENCIA(401);
    
    private int id;

    private Layer(int id){
    	this.id = id;
    }
    public int getId()
    {
      return this.id;
    }

    public static NetworkAlarm.ElementType getType(int id)
    {
      for (NetworkAlarm.ElementType type : NetworkAlarm.ElementType.values()) {
        if (type.getId() == id) {
          return type;
        }
      }
      return null;
    }
  }
}