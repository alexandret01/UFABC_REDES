package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.util.ArrayUtils;

public class GenericHandlerFactory
{
	private static List<GenericHandler> handlerList = new ArrayList<GenericHandler>();
	private static GenericHandler nullHandler;

	static	{
		handlerList.add(new SPVL4Handler());
		handlerList.add(new FanG8Handler());
		nullHandler = new GenericHandler()
		{
			public boolean canHandle(Object ne) {
				return true;
			}

			public byte[] analyzeGet(Object board, int code, byte[] data) {
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}

			public void analyzeResponse(Object board, Map<Integer, byte[]> tlvs, Map<Integer, byte[]> tlvHistory, Collection<Notification> notifications, Map<Integer, Double> performanceData)
			{
			}

			public void analyzeSet(Object board, int code, byte[] data)
			{
			}

			public boolean analyzeTrap(Object board, int type, byte[] value, Collection<Notification> alarms)
			{
				return false;
			}

			public int getExpectedResponseSize(Object board, int code) {
				return 0;
			}

			public List<Integer> getRequestCode(Object board, GenericHandler.CodeType type)
			{
				return Collections.emptyList();
			}

			public Map<Integer, Boolean> getTrapsFromBean(Object board)
			{
				return Collections.emptyMap();
			}
		};
	}

	public static GenericHandler getHandler(NE_Impl ne)
	{
		if (ne != null) {
			for (GenericHandler handler : handlerList) {
				if (handler.canHandle(ne)) {
					return handler;
				}
			}
		}
		return nullHandler;
	}

	public static List<GenericHandler> getHandlerList() {
		return handlerList;
	}
}