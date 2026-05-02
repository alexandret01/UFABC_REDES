
package br.com.padtec.v3.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public final class CollectionUtils
{
  public static <T> Collection<T> getOrderedList(List<T> original)
  {
    TreeMap<String, T> ordered = new TreeMap<String, T>();
    for (T element : original) {
      ordered.put(element.toString(), element);
    }
    return ordered.values();
  }

  public static <T> void toLowerCase(Map<String, T> data)
  {
    Collection<String> dataToRemove = new ArrayList<String>();
    for (Map.Entry<String, T> item : data.entrySet()) {
      String key = (String)item.getKey();
      if (key != null) {
        String lower = key.toLowerCase();
        if (!(lower.equals(key))) {
          dataToRemove.add(key);
        }
      }
    }
    for (String key : dataToRemove) {
      T value = data.remove(key);
      data.put(key.toLowerCase(), value);
    }
  }

  public static <T> boolean in(T o, T[] list)
  {
    for (Object i : list) {
      if (o.equals(i)) {
        return true;
      }
    }
    return false;
  }

  public static boolean in(int value, int[] set)
  {
    for (int i = 0; i < set.length; ++i) {
      if (set[i] == value) {
        return true;
      }
    }
    return false;
  }

  public static <T> List<T> select(Collection<T> from, String field, Object value)
    throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    ArrayList<T> result = new ArrayList<T>();
    if (!(from.isEmpty())) {
      Field fieldType = from.iterator().next().getClass().getDeclaredField(
        field);
      for (T item : from) {
        Object itemValue = fieldType.get(item);
        if (Functions.equals(itemValue, value)) {
          result.add(item);
        }
      }
    }
    return result;
  }

  public static <T> List<T> select(T[] from, String field, Object value)
    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException
  {
    return select((Collection<T>)Arrays.asList(from), field, value);
  }

  public static List<Object> getListField(List<Object> data, String method)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
  {
    List<Object> resultColection = new ArrayList<Object>();
    for (Iterator<Object> localIterator = data.iterator(); localIterator.hasNext(); ) { 
    	Object item = localIterator.next();
      Method m = item.getClass().getMethod(method, null);
      Object result = m.invoke(item, new Object[0]);
      resultColection.add(result);
    }
    return resultColection;
  }

  public static <T extends Comparable<T>> T getMin(List<T> list)
  {
    T smallest = null;
    Iterator<T> iter = list.iterator();
    while (iter.hasNext())
    {
      if (smallest == null) {
        smallest = iter.next(); } else {
        T next;
        if (smallest.compareTo(next = iter.next()) > 0)
          smallest = next;
      }
    }
    return smallest;
  }

  public static <K, V> K getKey(Map<K, V> map, V value)
  {
    for (Map.Entry<K,V> entry : map.entrySet()) {
      if (Functions.equals(value, entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  public static <T> TreeSet<T> newTreeSet(T[] data)
  {
    TreeSet<T> result = new TreeSet<T>();
    for (T item : data) {
      result.add(item);
    }
    return result;
  }

  public static <T> LinkedList<T> newLinkedList(T[] data)
  {
    LinkedList<T> result = new LinkedList<T>();
    addAll(result, data);
    return result;
  }

  public static <T> List<T> newArrayList(T[] data)
  {
    ArrayList<T> result = new ArrayList<T>(data.length);
    addAll(result, data);
    return result;
  }

  public static <T> boolean addAll(Collection<T> collection, T[] data)
  {
    boolean result = false;
    for (T item : data) {
      collection.add(item);
      result = true;
    }
    return result;
  }

  public static <P, C> void filterCollection(Collection<P> source, Class<C> childClass)
  {
    Iterator iter = source.iterator();
    while (iter.hasNext()) {
      Object board = iter.next();
      if (childClass.isInstance(board)) {
        continue;
      }
      iter.remove();
    }
  }
}