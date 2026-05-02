package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.com.padtec.v3.data.Notification;


public abstract interface GenericHandler
{
  public abstract boolean canHandle(Object paramObject);

  public abstract List<Integer> getRequestCode(Object paramObject, CodeType paramCodeType);

  public abstract int getExpectedResponseSize(Object paramObject, int paramInt);

  public abstract void analyzeResponse(Object paramObject, Map<Integer, byte[]> paramMap1, Map<Integer, byte[]> paramMap2, Collection<Notification> paramCollection, Map<Integer, Double> paramMap);

  public abstract boolean analyzeTrap(Object paramObject, int paramInt, byte[] paramArrayOfByte, Collection<Notification> paramCollection);

  public abstract byte[] analyzeGet(Object paramObject, int paramInt, byte[] paramArrayOfByte);

  public abstract void analyzeSet(Object paramObject, int paramInt, byte[] paramArrayOfByte);

  public abstract Map<Integer, Boolean> getTrapsFromBean(Object paramObject);

  public static enum CodeType
  {
    STATIC, DYNAMIC, OCCASIONAL, CONFIGURATOR;
  }
}