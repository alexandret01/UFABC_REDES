package br.com.padtec.v3.server.protocols.codegenerator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import br.com.padtec.v3.data.ne.MultiRate;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.ChannelTable;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.TrpTables;
import br.com.padtec.v3.util.io.DynamicFile;
import br.com.padtec.v3.util.math.OverflowChk;
import br.com.padtec.v3.util.text.TextParser;



public class Generator {
	

	public static int add(byte[] data, int pos, int value) {
		return (Functions.b2i(data[pos]) + value);
	}

	public static void _add(byte[] data, int pos, int value, int bean) {
		data[pos] = Functions.i2b(bean - value);
	}

	public static double getAgc(byte[] data, int pos, int mul, double div) {
		return ((Functions.b2i(data[(pos + 1)]) * mul + Functions.b2i(data[pos])) / 
				div);
	}

	public static void _getAgc(byte[] data, int pos, int mul, double div, double bean)
	{
		data[(pos + 1)] = Functions.i2b((int)(bean * div / mul));
		data[pos] = Functions.i2b((int)(bean * div % mul));
	}

	public static String getVersion(byte[] data, int pos, int size) {
		if (data != null) {
			return getString(data, pos, size).trim();
		}
		return "-1";
	}

	public static void _getVersion(byte[] data, int pos, String bean)
	{
		if ((bean != null) && (!("-1".equals(bean))))
			_getString(data, pos, pos + bean.length() - 1, bean);
	}

	public static boolean getBit(byte[] data, int pos, int bit)	{
		return Functions.testBit(data[pos], bit);
	}

	public static void _getBit(byte[] data, int pos, int bit, boolean bean) {
		if (bean)
			data[pos] = Functions.setBit(data[pos], bit);
	}

	public static BigInteger getBigInteger(byte[] data, BigInteger framesOTN, int pos, int len, NE ne)
	{
		return OverflowChk.getCorrectValue(data, framesOTN, pos, pos + len - 1, ne);
	}

	public static void _getBigInteger(byte[] data, BigInteger framesOTN, int pos, int size, BigInteger bean)
	{
		int dataIdx = pos + size - 1;

		byte[] value = framesOTN.toByteArray();
		int valueIdx = value.length - 1;

		while ((dataIdx >= pos) && (valueIdx >= 0)) {
			data[dataIdx] = value[valueIdx];
			--dataIdx;
			--valueIdx;
		}
	}

	public static double getDbm(byte[] data, int pos, int len, double div) {
		return Functions.getDbm(Functions.b2l(data, pos, len) / div);
	}

	public static double getMuxDbm(byte[] data, int pos, int len, int mul, double div)
	{
		return Functions.getDbm((Functions.b2i(data[pos]) * mul + 
				Functions.b2i(data[(pos + 1)])) / 
				div);
	}

	public static void _getMuxDbm(byte[] data, int pos, int len, int mul, double div, double bean)
	{
		data[pos] = Functions.i2b((int)(Functions.getMiliwatts(bean) * div) / mul);
		data[(pos + 1)] = 
			Functions.i2b((int)(Functions.getMiliwatts(bean) * div) % 
					mul);
	}

	public static void _getDbm(byte[] data, int pos, int len, double div, double bean)
	{
		long mw = Math.round(Functions.getMiliwatts(bean) * div);
		byte[] dataArray = Functions.l2b(mw, len);
		System.arraycopy(dataArray, 0, data, pos, len);
	}

	public static double getDouble(byte[] data, int pos, int len, double div) {
		return (Functions.b2l(data, pos, len) / div);
	}

	public static int getInt(byte[] data, int pos) {
		return Functions.b2i(data[pos]);
	}

	public static void _getInt(byte[] data, int pos, int bean) {
		data[pos] = Functions.i2b(bean);
	}

	public static void _getDouble(byte[] data, int pos, int len, double div, double bean)
	{
		byte[] dataArray = Functions.l2b(Math.round(bean * div), len);
		System.arraycopy(dataArray, 0, data, pos, len);
	}

	public static double getDoubleSigned(byte[] data, int pos, int len, double div) {
		double valor = Functions.b2sl(data, pos, len);
		return valor / div;
	}

	public static void _getDoubleSigned(byte[] data, int pos, int len, double div, double bean)
	{
		byte[] dataArray = Functions.l2b(Math.round(bean * div), len);
		System.arraycopy(dataArray, 0, data, pos, len);
	}

	public static String getChannel(byte[] data, int pos) {
		return TrpTables.getChannel(Functions.b2i(data[pos]));
	}

	public static String getChannelDWDM(byte[] data, int pos) {
		return TrpTables.getChannelDWDM(Functions.b2i(data[pos]));
	}

	public static void _getChannelDWDM(byte[] data, int pos, String channelName) {
		try {
			int canal = Integer.parseInt(channelName.substring(1));
			if (canal == 0) {
				data[pos] = 100; return;
			}
			data[pos] = Functions.i2b(canal);
		}
		catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
			data[pos] = 0;
		}
	}

	public static String getChannelDWDMSintonizavel(byte[] data, int pos)
	{
		return backITUChannel((int)Functions.b2l(data, pos, 2));
	}

	static String backITUChannel(int channel) {
		String channelS = "";
		channel >>= 4;
		if (channel % 2 != 0) {
			channelS = "C";
		} else {
			channelS = "H";
			++channel;
		}
		channel = 61 - (channel >> 1);

		if (channel < 10) {
			channelS = channelS + "0";
		}
		channelS = channelS + Integer.valueOf(channel).toString();
		return channelS;
	}

	public static void _getChannel(byte[] data, int i, String channel)	{
	}

	public static double getLambda(byte[] data, int pos) {
		return TrpTables.getLambda(Functions.b2i(data[pos]));
	}

	public static void _getLambda(byte[] data, int pos, double nominalLambda) {
		int code = TrpTables.getLambdaCode(nominalLambda);
		data[pos] = Functions.i2b(code);
	}

	public static double getLambdaSintonizavel(byte[] data, int pos) {
		String channel = backITUChannel((int)Functions.b2l(data, pos, 2));
		double wavelength = (ChannelTable.channel2Wavelength(channel) * 1000000000);
		return wavelength; 
	}

	public static double getLambda(byte[] data, int pos, int pos2, double value) {
		return (TrpTables.getLambda(Functions.b2i(data[pos])) + data[pos2] / value);
	}

	public static double getRealLambda(byte[] data, int pos, double lambda, double div)
	{
		int[] dataLambda = new int[15];
		dataLambda[5] = Functions.b2i(data[(pos++)]);
		dataLambda[6] = Functions.b2i(data[(pos++)]);
		dataLambda[7] = Functions.b2i(data[(pos++)]);
		dataLambda[8] = Functions.b2i(data[(pos++)]);
		dataLambda[9] = Functions.b2i(data[(pos++)]);
		return (TrpTables.getLambdaReal((int)lambda, dataLambda) / div);
	}

	public static double getMaxTemperature(byte[] data, int posStart, double mul)
	{
		return (((Functions.b2i(data[posStart]) << 4) + (
				Functions.b2i(data[(posStart + 1)]) >> 4)) * 
				mul);
	}

	public static void _getMaxTemperature(byte[] data, int posStart, double mul, double maxTemp)
	{
		data[posStart] = Functions.i2b((int)(maxTemp / mul) >> 4);
		data[(posStart + 1)] = Functions.i2b(((int)(maxTemp / mul) & 0xF) << 4);
	}

	public static double getTemperature(byte[] data, int posStart, double mul)
	{
		if ((data[posStart] & 0x8) == 8) {
			return (((Functions.b2si((byte)(data[posStart] & 0xF | 0xF0)) << 8) + 
					Functions.b2i(data[(posStart + 1)])) * 
					mul);
		}
		return ((((Functions.b2i(data[posStart]) & 0xF) << 8) + 
				Functions.b2i(data[(posStart + 1)])) * 
				mul);
	}

	public static void _getTemperature(byte[] data, int posStart, double mul, double temp)
	{
		data[posStart] = Functions.i2b((int)(temp / mul) >> 8);
		data[(posStart + 1)] = Functions.i2b((int)(temp / mul) & 0xFF);
	}

	public static double getPot10G(byte[] data, int pos1src, int pos1dst, int len1, int pos2src, int pos2dst, int len2, boolean isPin)
	{
		byte[] tabela = new byte[10];
		System.arraycopy(data, pos1src, tabela, pos1dst, len1);
		System.arraycopy(data, pos2src, tabela, pos2dst, len2);
		return TrpTables.getPot10G(isPin, tabela);
	}

	public static double getPot10G(byte[] data, boolean isPin) {
		return TrpTables.getPot10G(isPin, data);
	}

	public static void _getPot10G(byte[] data, boolean isPin, double bean) {
		TrpTables.getPot10GTabela(isPin, data, bean);
	}

	public static void _getPot10G(byte[] data, int pos1src, int pos1dst, int len1, int pos2src, int pos2dst, int len2, boolean isPin, double bean)
	{
		byte[] tabela = new byte[10];
		System.arraycopy(data, pos1src, tabela, pos1dst, len1);
		System.arraycopy(data, pos2src, tabela, pos2dst, len2);
		TrpTables.getPot10GTabela(isPin, tabela, bean);
		System.arraycopy(tabela, pos1dst, data, pos1src, len1);
		System.arraycopy(tabela, pos2dst, data, pos2src, len2);
	}

	public static String getString(byte[] data, int offset, int length) {
		return new String(data, offset, length);
	}

	public static void _getString(byte[] data, int i, int j, String bean) {
		byte[] aux = new byte[j - i + 1];
		System.arraycopy(bean.getBytes(), 0, aux, 0, Math.min(
				bean.getBytes().length, j - i + i));
		System.arraycopy(aux, 0, data, i, Math.min(aux.length, data.length - i));
	}

	public static byte getByte(byte[] data, int i) {
		return data[i];
	}

	public static void _getByte(byte[] data, int i, byte bean) {
		data[i] = bean;
	}

	public static int getSlot(byte[] data, int pos) {
		if (data != null) {
			return (int)Functions.b2l(data, pos, 2);
		}
		return -1;
	}

	public static void _getSlot(byte[] data, int pos, int slot)
	{
		byte[] aux = Functions.l2b(slot, 2);
		System.arraycopy(aux, 0, data, pos, 2);
	}

	public static MultiRate.Rate getRate(byte[] data, int pos) {
		int rate = data[pos] & 0x3;
		return MultiRate.Rate.getRate((byte)rate);
	}

	public static void _getRate(byte[] data, int pos, MultiRate.Rate r) {
		int rate = r.getCode();
		data[pos] = Functions.i2b(rate);
	}

	public static double getCombRealLambda(byte[] data, int startIndex, int len, double lambda)
	{
		double pot_10_9 = Math.pow(10.0D, 9.0D);

		lambda /= pot_10_9;

		long lambdaFreq = ChannelTable.wavelength2Frequency(lambda);

		long desvFreq = Functions.b2sl(data, startIndex, len) * 1000000L;

		long freqReal = lambdaFreq + desvFreq;

		double lambdaReal = ChannelTable.frequency2Wavelength(freqReal);

		return (lambdaReal * pot_10_9);
	}

	public static void _getCombRealLambda(byte[] data, int startIndex, int len, double lambda, double bean)
	{
	}

	public static double getAmpPout(byte[] data, int index, int mul, int div)
	{
		return (((data[index] & 0x80) == 128) ? (0xFFFF0000 | data[(index - 1)] & 0xFF | data[index] << 8 & 0xFF00) / 
				div : 
					(Functions.b2i(data[(index - 1)]) + 
							Functions.b2i(data[index]) * mul) / 
							div);
	}

	public static double getAmpPin(byte[] data, int index, int mul, int div)
	{
		return (((data[index] & 0x80) == 128) ? (0xFFFF0000 | data[(index - 1)] & 0xFF | data[index] << 8 & 0xFF00) / 
				div : 
					(Functions.b2i(data[(index - 1)]) + 
							Functions.b2i(data[index]) * mul) / 
							div);
	}

	public static boolean testByte(byte[] data, int pos)
	{
		return (Functions.b2i(data[pos]) != 1);
	}

	public static void _testByte(byte[] data, int pos, boolean bean)
	{
		if (bean)
			data[pos] = 1;
		else
			data[pos] = 0;
	}

	public static double getTrpPoutPin(byte[] data, boolean applyLog)
	{
		return TrpTables.getPower(Functions.b2i(data[0]), 1, data, applyLog);
	}

	public static double getDesvioLambda(int lbd, byte[] table)
	{
		double ret = 0.0D;

		double[] auxLambda = new double[5];

		auxLambda[0] = -1.0D;
		auxLambda[1] = -0.5D;
		auxLambda[2] = 0.0D;
		auxLambda[3] = 0.5D;
		auxLambda[4] = 1.0D;

		for (int i = 0; i < 5; ++i) {
			if (lbd == getInt(table, i)) {
				return auxLambda[i];
			}
		}

		for (int i = 0; i < 4; ++i) {
			if ((lbd >= getInt(table, i)) || 
					(lbd <= getInt(table, i + 1))) continue;
			double x2 = getInt(table, i);
			double y2 = auxLambda[i];
			double x1 = getInt(table, i + 1);
			double y1 = auxLambda[(i + 1)];

			ret = ((y1 - y2) * lbd + x1 * y2 - (x2 * y1)) / (x1 - x2);
			ret *= 100.0D;
			ret = (int)ret;
			ret /= 100.0D;
		}

		return ret;
	}

	public static long getLong(byte[] data, int pos, int size) {
		return Functions.b2l(data, pos, size);
	}

	public static void _getLong(byte[] data, int pos, int size, long bean) {
		byte[] dataArray = Functions.l2b(bean, size);
		System.arraycopy(dataArray, 0, data, pos, size);
	}

	public static int getInt(byte[] data, int pos, int size) {
		return (int)getLong(data, pos, size);
	}

	public static void _getInt(byte[] data, int pos, int size, int bean) {
		_getLong(data, pos, size, bean);
	}

	public static int getPercentage(byte[] data, int pos)
	{
		return Math.round(Functions.b2i(data[pos]) * 100 / 255.0F);
	}

	public static void _getPercentage(byte[] data, int pos, int percentage)
	{
		data[pos] = Functions.i2b((int)Math.round(percentage * 255 / 100.0D));
	}

	public static String getChannelCwdm(byte[] data, int pos)
	{
		return TrpTables.getChannelCWDM(Functions.b2i(data[pos]));
	}

	public static class Description
	{
		public String bean;
		public String function;
		public String array;

		public Description(String campo1, String campo2, String campo3)
		{
			this.bean = campo1;
			this.function = campo2;
			this.array = campo3;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException	{
		DynamicFile df = new DynamicFile( new File("src/br/com/padtec/v3/server/protocols/codegenerator/" + args[0]), 10000L );
		df.addObserver(new Observer(){
			public void update(Observable o, Object arg) {
				try {

					BufferedReader br = new BufferedReader(
							new InputStreamReader(new ByteArrayInputStream((byte[])arg), "UTF-8"));

					List<Description> list = TextParser.parse(br, "\n", "|", new TextParser.Mapper<Description>() {
						public Description parse(List<String> line) {
							if (line.isEmpty()) {
								return null;
							}
							if (line.size() == 1) {
								return new Description(line.get(0).trim(), null, null);
							}
							return new Description(line.get(0).trim(), line.get(1).trim(), line.get(2).trim());
						}

					});
					System.out.println();
					System.out.println("// ***********");
					System.out.println("// Parse array");
					System.out.println("// ***********");

					for (Iterator<Description> localIterator = list.iterator(); localIterator.hasNext(); ) { 
						Description item = localIterator.next();
						if (item == null) {
							System.out.println();
						}
						else if (item.function == null) {
							System.out.println(item.bean);
						}
						else {
							System.out.print(item.bean.replace("??", "set").replace('?', 's'));
							System.out.print("(");
							System.out.print(item.function);
							System.out.print("(");

							StringTokenizer st = new StringTokenizer(item.array);
							while (true) {
								System.out.print(st.nextToken());
								if (!(st.hasMoreElements())) {
									break;
								}
								System.out.print(", ");
							}

							System.out.print("));");
							System.out.println();
						}
					}
					System.out.println();
					System.out.println("// ********");
					System.out.println("// To array");
					System.out.println("// ********");

					for (Iterator<Description> localIterator = list.iterator(); localIterator.hasNext(); ) { 
						Description item = localIterator.next();
						if (item == null) {
							System.out.println();
						}
						else if (item.function == null) {
							System.out.println(item.bean);
						}
						else {
							int idx = item.function.lastIndexOf(".") + 1;
							System.out.print(item.function.substring(0, idx));
							System.out.print("_");
							System.out.print(item.function.substring(idx));
							System.out.print("(");
							StringTokenizer st = new StringTokenizer(item.array);
							while (st.hasMoreElements()) {
								System.out.print(st.nextElement());
								System.out.print(", ");
							}
							System.out.print(item.bean.replace("??", "is").replace('?', 'g'));
							System.out.print("()");
							System.out.print(");");
							System.out.println(); }
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		df.start();
	}
}