package br.com.padtec.v3.util.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;




public final class TextUtils
{
	public static String toString(Object value, String ext, String def)
	{
		if (value == null) {
			return def;
		}
		if (ext == null)
			ext = "";
		else if ((ext.length() > 0) && (ext.charAt(0) != ' ')) {
			ext = " " + ext;
		}
		if (value instanceof BigInteger) {
			BigInteger bi = (BigInteger)value;
			DecimalFormat f = new DecimalFormat("#,##0");
			return f.format(bi); }
		if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal)value;
			return bd.toPlainString(); }
		if (value instanceof Number) {
			double num = ((Number)value).doubleValue();
			if (num % 1.0D != 0.0D)
			{
				if ((Math.abs(num) < 0.01D) || (Math.abs(num) >= 1000000.0D))
				{
					return toString(new Double(num), 3, true, false, ext, def);
				}

				return toString(new Double(num), 2, false, false, ext, def);
			}

			return toString(new Double(num), 0, false, false, ext, def);
		}

		return value + ext;
	}

	public static String toString(Number value, int prec, boolean exp, boolean allowKMG, String unit, String def)
	{
		DecimalFormat f;
		if (value == null) {
			return def;
		}
		double num = value.doubleValue();
		//    if (num != num)
		//    {
		//      return def;
		//    }
		if (unit == null) {
			unit = "";
		}
		if (Double.isInfinite(num)) {
			exp = false;
			allowKMG = false;
		}

		if (exp) {
			f = new DecimalFormat("0.0E0");
		} else {
			f = new DecimalFormat("0.0");
			if (allowKMG) {
				boolean negative = num < 0.0D;
				num = Math.abs(num);
				int i = -1;
				if (num > 1.0D) {
					while ((num / 1000.0D >= 1.0D) && (i < 4)) {
						num /= 1000.0D;
						++i;
					}
					if (i >= 0)
						unit = "kMGTP".charAt(i) + unit;
				}
				else if (num > 0.0D) {
					while ((num < 1.0D) && (i < 3)) {
						num *= 1000.0D;
						++i;
					}
					if (i >= 0) {
						unit = "mµnp".charAt(i) + unit;
					}
				}
				if (negative) {
					num = -num;
				}
			}
		}

		if ((unit.length() > 0) && (unit.charAt(0) != ' ')) {
			unit = " " + unit;
		}

		f.setMaximumFractionDigits(prec);
		f.setMinimumFractionDigits(prec);

		def = f.format(num);
		return def + unit;
	}

	public static String breakText(String text, int maxChars)
	{
		StringBuilder result = new StringBuilder();
		BufferedReader reader = new BufferedReader(new StringReader(text));
		try
		{
			String line = reader.readLine();


			int pos = line.lastIndexOf(" ", maxChars);
			if (pos == -1) {
				result.append(line.substring(0, maxChars));
				result.append("\n");
				line = line.substring(maxChars);
			} else {
				result.append(line.substring(0, pos));
				result.append("\n");
				line = line.substring(pos + 1);
			}
			do
			{
				if (line.length() <= maxChars);
				result.append(line);
				result.append("\n");
			}
			while ((line = reader.readLine()) != null);
		}
		catch (IOException localIOException)
		{
		}

		return result.substring(0, result.length() - 1);
	}

	public static String newString(String[] param) {
		StringBuilder builder = new StringBuilder();
		for (String item : param) {
			builder.append(item);
		}
		String result = builder.toString();
		builder = null;
		return result;
	}

	public static String newStringIgnoreNull(String[] param) {
		StringBuilder builder = new StringBuilder();
		for (String item : param) {
			if (item != null) {
				builder.append(item);
			}
		}
		String result = builder.toString();
		builder = null;
		return result;
	}

	public static Character toCharacter(String string)
	{
		if ((string == null) || (string.length() == 0)) {
			return null;
		}
		return Character.valueOf(string.charAt(0));
	}

	public static String filter(String data, String filter)
	{
		if ((data == null) || (filter == null)) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < data.length(); ++i) {
			char c = data.charAt(i);
			if (filter.indexOf(c) >= 0) {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String toString(Object data)
	{
		if (data == null) {
			return null;
		}
		return data.toString();
	}

	public static String getTextDifferenceToAppend(String currentText, String newData)
	{
		if (newData.length() == 0) {
			return newData;
		}
		int currentTextindex = currentText.length() - newData.length();
		while ((currentTextindex = currentText.indexOf(newData.charAt(0), 
				currentTextindex)) != -1) {
			if (currentText.substring(currentTextindex).startsWith(
					newData.substring(0, currentText.length() - currentTextindex))) {
				return newData.substring(currentText.length() - currentTextindex);
			}
			++currentTextindex;
		}
		return newData;
	}
}