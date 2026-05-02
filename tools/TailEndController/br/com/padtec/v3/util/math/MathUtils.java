package br.com.padtec.v3.util.math;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import br.com.padtec.v3.util.text.TextUtils;

public final class MathUtils {
	public static Number add(Number v1, Number v2)
	throws NumberFormatException
	{
		if (v1 == null) {
			v1 = new Integer(0);
		}
		if (v2 == null) {
			v2 = new Integer(0);
		}
		if ((v1 instanceof BigDecimal) || (v2 instanceof BigDecimal)) {
			return asBigDecimal(v1).add(asBigDecimal(v2));
		}
		if ((v1 instanceof Double) || (v2 instanceof Double)) {
			return Double.valueOf(v1.doubleValue() + v2.doubleValue());
		}
		if ((v1 instanceof Float) || (v2 instanceof Float)) {
			return Float.valueOf(v1.floatValue() + v2.floatValue());
		}
		if ((v1 instanceof BigInteger) || (v2 instanceof BigInteger)) {
			return asBigInteger(v1).add(asBigInteger(v2));
		}
		if ((v1 instanceof Long) || (v2 instanceof Long)) {
			return Long.valueOf(v1.longValue() + v2.longValue());
		}
		if ((v1 instanceof Integer) || (v2 instanceof Integer)) {
			return Integer.valueOf(v1.intValue() + v2.intValue());
		}
		if ((v1 instanceof Short) || (v2 instanceof Short))
		{
			return Short.valueOf((short)(v1.shortValue() + v2.shortValue()));
		}
		return Double.valueOf(v1.doubleValue() + v2.doubleValue());
	}

	public static Number subtract(Number v1, Number v2)
	throws NumberFormatException
	{
		if (v1 == null) {
			v1 = new Integer(0);
		}
		if (v2 == null) {
			v2 = new Integer(0);
		}
		if ((v1 instanceof BigDecimal) || (v2 instanceof BigDecimal)) {
			return asBigDecimal(v1).subtract(asBigDecimal(v2));
		}
		if ((v1 instanceof Double) || (v2 instanceof Double)) {
			return Double.valueOf(v1.doubleValue() - v2.doubleValue());
		}
		if ((v1 instanceof Float) || (v2 instanceof Float)) {
			return Float.valueOf(v1.floatValue() - v2.floatValue());
		}
		if ((v1 instanceof BigInteger) || (v2 instanceof BigInteger)) {
			return asBigInteger(v1).subtract(asBigInteger(v2));
		}
		if ((v1 instanceof Long) || (v2 instanceof Long)) {
			return Long.valueOf(v1.longValue() - v2.longValue());
		}
		if ((v1 instanceof Integer) || (v2 instanceof Integer)) {
			return Integer.valueOf(v1.intValue() - v2.intValue());
		}
		if ((v1 instanceof Short) || (v2 instanceof Short))
		{
			return Short.valueOf((short)(v1.shortValue() - v2.shortValue()));
		}
		return Double.valueOf(v1.doubleValue() - v2.doubleValue());
	}

	public static Number divide(Number v1, Number v2)
	throws NumberFormatException
	{
		if (v1 == null) {
			v1 = new Integer(0);
		}
		if (v2 == null) {
			v2 = new Integer(0);
		}
		if ((v1 instanceof BigDecimal) || (v2 instanceof BigDecimal)) {
			return asBigDecimal(v1).divide(asBigDecimal(v2), MathContext.DECIMAL128);
		}
		if ((v1 instanceof Double) || (v2 instanceof Double)) {
			return Double.valueOf(v1.doubleValue() / v2.doubleValue());
		}
		if ((v1 instanceof Float) || (v2 instanceof Float)) {
			return Float.valueOf(v1.floatValue() / v2.floatValue());
		}
		if ((v1 instanceof BigInteger) || (v2 instanceof BigInteger)) {
			return asBigInteger(v1).divide(asBigInteger(v2));
		}
		if ((v1 instanceof Long) || (v2 instanceof Long)) {
			return Long.valueOf(v1.longValue() / v2.longValue());
		}
		if ((v1 instanceof Integer) || (v2 instanceof Integer)) {
			return Integer.valueOf(v1.intValue() / v2.intValue());
		}
		if ((v1 instanceof Short) || (v2 instanceof Short))
		{
			return Short.valueOf((short)(v1.shortValue() / v2.shortValue()));
		}
		return Double.valueOf(v1.doubleValue() / v2.doubleValue());
	}

	public static Number multiply(Number v1, Number v2)
	throws NumberFormatException
	{
		if (v1 == null) {
			v1 = new Integer(0);
		}
		if (v2 == null) {
			v2 = new Integer(0);
		}
		if ((v1 instanceof BigDecimal) || (v2 instanceof BigDecimal)) {
			return asBigDecimal(v1).multiply(asBigDecimal(v2));
		}
		if ((v1 instanceof Double) || (v2 instanceof Double)) {
			return Double.valueOf(v1.doubleValue() * v2.doubleValue());
		}
		if ((v1 instanceof Float) || (v2 instanceof Float)) {
			return Float.valueOf(v1.floatValue() * v2.floatValue());
		}
		if ((v1 instanceof BigInteger) || (v2 instanceof BigInteger)) {
			return asBigInteger(v1).multiply(asBigInteger(v2));
		}
		if ((v1 instanceof Long) || (v2 instanceof Long)) {
			return Long.valueOf(v1.longValue() * v2.longValue());
		}
		if ((v1 instanceof Integer) || (v2 instanceof Integer)) {
			return Integer.valueOf(v1.intValue() * v2.intValue());
		}
		if ((v1 instanceof Short) || (v2 instanceof Short))
		{
			return Short.valueOf((short)(v1.shortValue() * v2.shortValue()));
		}
		return Double.valueOf(v1.doubleValue() * v2.doubleValue());
	}

	public static int compare(Number v1, Number v2)
	throws NumberFormatException
	{
		if ((v1 instanceof BigDecimal) || (v2 instanceof BigDecimal)) {
			return asBigDecimal(v1).compareTo(asBigDecimal(v2));
		}
		if ((v1 instanceof BigInteger) || (v2 instanceof BigInteger)) {
			return asBigInteger(v1).compareTo(asBigInteger(v2));
		}
		if ((v1 instanceof Long) || (v2 instanceof Long) || (v1 instanceof Integer) || 
				(v2 instanceof Integer) || (v1 instanceof Short) || (v2 instanceof Short)) {
			return asLong(v1).compareTo(asLong(v2));
		}
		return asDouble(v1).compareTo(asDouble(v2));
	}

	public static BigDecimal asBigDecimal(Number value)
	throws NumberFormatException
	{
		BigDecimal result;
		if (value instanceof BigDecimal) {
			return ((BigDecimal)value);
		}

		if (value instanceof BigInteger)
			result = new BigDecimal((BigInteger)value);
		else {
			result = new BigDecimal(value.doubleValue());
		}
		return result;
	}

	public static BigInteger asBigInteger(Number value)
	{
		if (value instanceof BigInteger) {
			return ((BigInteger)value);
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal)value).toBigInteger();
		}
		return BigInteger.valueOf(value.longValue());
	}

	public static Double asDouble(Number value)
	{
		if (value instanceof Double) {
			return ((Double)value);
		}
		if (value == null) {
			return null;
		}
		return Double.valueOf(value.doubleValue());
	}

	public static Long asLong(Number value)
	{
		if (value instanceof Long) {
			return ((Long)value);
		}
		if (value == null) {
			return null;
		}
		return Long.valueOf(value.longValue());
	}

	public static boolean isInfinite(Number value)
	{
		if (value instanceof Double) {
			return ((Double)value).isInfinite();
		}
		if (value instanceof Float) {
			return ((Float)value).isInfinite();
		}
		return false;
	}

	public static boolean isNaN(Number value)
	{
		if (value instanceof Double) {
			return ((Double)value).isNaN();
		}
		if (value instanceof Float) {
			return ((Float)value).isNaN();
		}
		return false;
	}

	public static Integer toInteger(String string)
	{
		if (string == null) {
			return null;
		}
		return Integer.valueOf(Integer.parseInt(string));
	}

	public static Integer toIntegerIgnoreStrings(String data)
	{
		data = TextUtils.filter(data, "0123456789.");
		if ((data == null) || (data.length() == 0)) {
			return null;
		}
		return toInteger(data);
	}

	public static Integer toInteger(String val, Integer def) {
		try {
			return Integer.valueOf(val); } catch (Exception e) {
			}
			return def;
	}
}