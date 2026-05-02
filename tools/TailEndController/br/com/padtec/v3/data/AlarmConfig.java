package br.com.padtec.v3.data;

import java.io.Serializable;

import br.com.padtec.v3.util.Msg;

public class AlarmConfig implements Serializable, Comparable<AlarmConfig> {
	private static final long serialVersionUID = 3L;
	private static String[] priorityNames = { Msg.getString("AlarmConfig.0"), 
		Msg.getString("AlarmConfig.1"), Msg.getString("AlarmConfig.2"), 
		Msg.getString("AlarmConfig.3"), Msg.getString("AlarmConfig.4") };

	private static int[] priorityValues = { 60, 50, 40, 30, 20 };
	private Integer id;
	private String nome;
	private Boolean mail;
	private int priority;
	private String desc;
	private long intermitenceTime;

	public AlarmConfig(int id, String nome, boolean mail, int priority, String desc, long intermitenceTime)
	{
		this.id = new Integer(id);
		this.nome = nome;
		this.mail = new Boolean(mail);
		this.priority = priority;
		this.desc = desc;
		this.intermitenceTime = intermitenceTime;
	}

	public String getDesc()
	{
		return this.desc;
	}

	public Integer getId()
	{
		return this.id;
	}

	public Boolean getMail()
	{
		return this.mail;
	}

	public String getNome()
	{
		return this.nome;
	}

	public int getPriority()
	{
		return this.priority;
	}

	public static String getPriorityName(int pr) {
		for (int i = 0; i < priorityValues.length; ++i) {
			if (priorityValues[i] == pr) {
				return priorityNames[i];
			}
		}
		return priorityNames[4];
	}

	public static int getPriority(String name) {
		for (int i = 0; i < priorityNames.length; ++i) {
			if (priorityNames[i].equals(name)) {
				return priorityValues[i];
			}
		}
		return 20;
	}

	public static String[] getPrioritiesName()
	{
		return priorityNames;
	}

	public String getPriorityName()
	{
		return getPriorityName(this.priority);
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public void setMail(Boolean mail)
	{
		this.mail = mail;
	}

	public void setNome(String nome)
	{
		this.nome = nome;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public void setPriority(String name)
	{
		setPriority(getPriority(name));
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof AlarmConfig) {
			AlarmConfig c = (AlarmConfig)obj;
			return c.id.equals(this.id);
		}
		return false;
	}

	public int hashCode()
	{
		return this.id.intValue();
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.id);
		buffer.append("-");
		buffer.append(this.nome);
		buffer.append("(");
		buffer.append(getPriorityName());
		buffer.append(")");
		return buffer.toString();
	}

	public void setId(Integer id)
	{
		if (this.id.intValue() == 0)
			this.id = id;
	}

	public int compareTo(AlarmConfig c)
	{
		if (!(equals(c))) {
			return (getId().intValue() - c.getId().intValue());
		}
		return 0;
	}

	public long getIntermitenceTime() {
		return this.intermitenceTime;
	}

	public void setIntermitenceTime(long intermitenceTime) {
		this.intermitenceTime = intermitenceTime;
	}
}