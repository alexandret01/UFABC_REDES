package br.com.padtec.v3.data;


import java.io.Serializable;
import java.util.Random;

import br.com.padtec.v3.util.text.TextUtils;

public final class Temperature implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final double[] ctt1 = { 
		47.5D, 
		51.200000000000003D, 54.899999999999999D, 58.399999999999999D, 61.899999999999999D, 32.399999999999999D, 36.399999999999999D, 40.399999999999999D, 44.399999999999999D, 48.299999999999997D, 52.100000000000001D, 56.0D, 59.600000000000001D, 
		63.200000000000003D, 32.100000000000001D, 36.100000000000001D, 40.100000000000001D, 44.100000000000001D, 48.0D, 52.0D, 55.799999999999997D, 59.5D, 63.200000000000003D, 32.5D, 36.399999999999999D, 
		40.5D, 44.5D, 48.399999999999999D, 52.299999999999997D, 56.100000000000001D, 59.799999999999997D, 63.399999999999999D, 32.799999999999997D, 36.899999999999999D, 40.799999999999997D, 44.799999999999997D, 48.700000000000003D, 
		52.600000000000001D, 56.399999999999999D, 60.200000000000003D, 28.800000000000001D, 32.700000000000003D, 36.700000000000003D, 40.700000000000003D, 44.700000000000003D, 48.600000000000001D, 52.5D, 56.399999999999999D, 60.100000000000001D, 
		29.300000000000001D, 33.200000000000003D, 37.200000000000003D, 41.100000000000001D, 45.0D, 48.899999999999999D, 52.799999999999997D, 56.600000000000001D, 60.5D, 29.699999999999999D, 33.600000000000001D, 37.399999999999999D, 
		41.399999999999999D, 45.200000000000003D, 49.200000000000003D, 53.0D, 56.799999999999997D, 60.5D, 30.100000000000001D, 34.0D, 37.799999999999997D, 41.700000000000003D, 45.5D, 49.299999999999997D, 
		53.200000000000003D, 56.899999999999999D, 60.799999999999997D, 30.399999999999999D, 34.299999999999997D, 38.0D, 41.899999999999999D, 45.600000000000001D, 49.5D, 53.299999999999997D, 57.100000000000001D, 60.700000000000003D, 
		30.699999999999999D, 34.399999999999999D, 38.100000000000001D, 41.899999999999999D, 45.600000000000001D, 
		49.5D, 53.299999999999997D, 57.100000000000001D, 60.700000000000003D, 30.699999999999999D, 34.399999999999999D, 38.100000000000001D, 41.899999999999999D, 45.600000000000001D, 49.5D, 53.299999999999997D };

	private static final transient Random r = new Random();

	private boolean setTemperatureCalled = false;
	private Double temperature;
	private boolean enabled = true;

	public void setData(String data)
	{
		if (this.setTemperatureCalled)
			return;
		try
		{
			double index;
			if (data == null)
				return;
			StringBuilder newData = new StringBuilder();
			char[] dataArray = data.toCharArray();
			for (int i = 0; i < dataArray.length; ++i) {
				if ("0123456789".indexOf(dataArray[i]) >= 0) {
					newData.append(dataArray[i]);
				}
			}
			String newDataStr = newData.toString();
			if (newDataStr.length() == 0) {
				return;
			}
			int channel = Integer.parseInt(newDataStr);
			double tableTemperature = ctt1[(channel % ctt1.length)];

			synchronized (r) {
				index = r.nextDouble();
			}
			this.temperature = tableTemperature + 0.1 - (index * 0.2);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTemperature(Double temperature) {
		this.setTemperatureCalled = true;
		this.temperature = temperature;
	}

	public Double getTemperature()
	{
		return this.temperature;
	}

	public String toString()
	{
		String DEGREE = "°C";
		return TextUtils.toString(getTemperature(), 1, false, false, DEGREE, "N/A");
	}

	public boolean isEnabled()
	{
		return this.enabled;
	}
}