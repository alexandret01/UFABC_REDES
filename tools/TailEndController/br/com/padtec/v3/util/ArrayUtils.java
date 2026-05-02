package br.com.padtec.v3.util;

import java.lang.reflect.Array;



public final class ArrayUtils
{
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	public static int[] ensureCapacity(int[] array, int size)
	{
		if (array.length >= size) {
			return array;
		}
		int[] result = new int[size];
		System.arraycopy(array, 0, result, 0, array.length);
		return result;
	}

	public static <T> String toString(T[] value)
	{
		if (value == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		result.append('[');
		int i = 0;
		if (value.length > 0) {
			while (true) {
				result.append(value[i]);
				++i;
				if (i >= value.length) break;
				result.append(',');
			}

		}

		result.append(']');
		return result.toString();
	}

	public static boolean startsWith(byte[] data, byte[] start)
	{
		if (data == start) {
			return true;
		}
		if ((data == null) || (start == null)) {
			return false;
		}
		if (data.length < start.length) {
			return false;
		}
		for (int i = 0; i < start.length; ++i) {
			if (data[i] != start[i]) {
				return false;
			}
		}
		return true;
	}

	public static <T extends Number> T[] asObject(Class<T> targetClass, double[] data)
	{
		int i;
		Number[] result = (Number[])Array.newInstance(targetClass, data.length);

		if (targetClass == Byte.class) {
			for (i = 0; i < data.length; ++i)
				result[i] = Byte.valueOf((byte)(int)data[i]);
		}
		else if (targetClass == Double.class) {
			for (i = 0; i < data.length; ++i)
				result[i] = Double.valueOf(data[i]);
		}
		else if (targetClass == Float.class) {
			for (i = 0; i < data.length; ++i)
				result[i] = Float.valueOf((float)data[i]);
		}
		else if (targetClass == Integer.class) {
			for (i = 0; i < data.length; ++i)
				result[i] = Integer.valueOf((int)data[i]);
		}
		else if (targetClass == Long.class) {
			for (i = 0; i < data.length; ++i)
				result[i] = Long.valueOf((long)data[i]);
		}
		else if (targetClass == Short.class) {
			for (i = 0; i < data.length; ++i) {
				result[i] = Short.valueOf((short)(int)data[i]);
			}
		}

		return (T[])result;
	}
}