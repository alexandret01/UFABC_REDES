package br.com.padtec.v3.server.protocols.ppm3;

import br.com.padtec.v3.data.GenericExtendedAlarm;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.AlarmFactory;

public class AlarmFactoryPPM3 extends AlarmFactory  {
	public static GenericExtendedAlarm createAlarm(NE_Impl ne, String location, int idAlarm, Integer contact, boolean isNew, String detail)
	{
		SerialNumber serial;
		if (ne == null)
		{
			serial = new SerialNumber(23, 1);
		}
		else serial = ne.getSerial();

		GenericExtendedAlarm alarm = new GenericExtendedAlarm(serial, idAlarm, 
				contact);
		if (!(isNew)) {
			alarm.setEndDate(System.currentTimeMillis());
		}
		if (ne != null) {
			alarm.setNeName(ne.getName());
		}
		alarm.setNeOrigin(serial);
		if (!(applyAlarmConfig(alarm))) {
			return null;
		}
		if (location != null) {
			alarm.setMapName(location);
		}
		if (detail != null) {
			alarm.setDetail(detail);
		}
		return alarm;
	}

	public static GenericExtendedAlarm createCommandAlarm(NE_Impl ne, String commandName, int contact)
	{
		GenericExtendedAlarm a = createAlarm(ne, null, 56, 
				null, true, null);
		String detail = commandName;
		detail = "NC" + detail + ";DC" + detail;
		a.setDetail(detail);
		return a;
	}
}