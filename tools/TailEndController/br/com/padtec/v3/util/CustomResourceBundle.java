package br.com.padtec.v3.util;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import br.com.padtec.v3.util.io.DynamicFile;
import br.com.padtec.v3.util.text.TextParser;




public final class CustomResourceBundle
{
	private static CustomResourceBundle instance;
	private final List<EquipmentClass> cache = new CopyOnWriteArrayList<EquipmentClass>();
	private DynamicFile df;

	public static CustomResourceBundle getInstance()
	{
		if (instance == null) {
			synchronized (CustomResourceBundle.class) {
				if (instance == null) {
					instance = new CustomResourceBundle();
					instance.start();
				}
			}
		}
		return instance;
	}

	private void start()
	{
		if (this.df != null)
		{
			return;
		}
		File file = new File("list.txt");
		try
		{
			if (!(file.exists())) {
				file.createNewFile();
				FileWriter fOut = new FileWriter(file);
				fOut.write("#\n");
				fOut.write("#  Arquivo list.txt\n");
				fOut.write("#  Tipos suportados:\n");
				fOut.write("#  TrpTrS25: Transponder Original (sem LaserOff)\n");
				fOut.write("#  TrpCWDM: Transponder CWDM\n");
				fOut.write("#  TrpDWDM: Transponder DWDM (com lambda)\n");
				fOut.write("#  TrpFECRX: Transponder FEC Rx (sem canal)\n");
				fOut.write("#  TrpGBEthC: Transponder Bidirecional CWDM\n");
				fOut.write("#  TrpGBEthD: Transponder Bidirecional DWDM\n");
				fOut.write("#  TrpDWDM10: Transponder 2R 10 Gbps\n");
				fOut.write("#  TrpDWDM25Otn: Transponder 2.5Gbps com OTN (G.709)\n");
				fOut.write("#  T100D_GC: Transponder DWDM 10 Giga OTN Regenerador\n");
				fOut
				.write("#  T100D_GT: Transponder DWDM 10 Giga OTN Terminal Transparente\n");
				fOut.write("#  T100D_GT_SDH: Transponder DWDM 10 Giga OTN Terminal \n");
				fOut
				.write("#  TrpBiDWDMRate: Transponder Bidirecional com medida de taxa\n");
				fOut.write("#  PBAmp: Amplificadores Pré, Booster e Linha\n");
				fOut.write("#  RAmp: Amplificadores RAMAN\n");
				fOut.write("#  Muxponder: Transponder 4 STM16 x 1 OTU2\n");
				fOut.write("#  Combiner: Combinador 8x1\n");
				fOut.write("#  OpticalProtection: Chave Óptica\n");
				fOut.write("#  OpticalSwitch8x1: proteção de transponder 8x1\n");
				fOut.write("#  SHK: Shelf House Keeping\n");
				fOut.write("#  SupSPV: Supervisor SPV (antigo)\n");
				fOut.write("#  SupSPVJ: Supervisor SPVJ\n");
				fOut.write("#  SupSPVJFilho: Supervisor SPVJ filho\n");
				fOut.write("#  MediaConverter: Conversores de Mídia\n");
				fOut
				.write("#  AmplifierPowerSupply: Fonte Gerenciável de amplificadores\n");
				fOut.write("#  PowerSupply: Fonte Gerenciável de transponders\n");
				fOut.write("#  Fan: Fan Gerenciável\n");
				fOut.write("#\n");
				fOut.write("#Este arquivo deve seguir o seguinte formato:\n");
				fOut
				.write("#<Número de modelo (part)>, <Nome do modelo>, <Nome da classe Java>\n");
				fOut.write("#\n");
				fOut.flush();
				fOut.close();
				fOut = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
			this.df = new DynamicFile(file, 10000L);
			this.df.addObserver(new Observer()   {
				public void update(Observable o, Object arg)    {
					//				  Log.getInstance().fine("CustomResourceBundle: Loading list.txt");
					try {
						ByteArrayInputStream bais = new ByteArrayInputStream((byte[])arg);
						InputStreamReader isr = new InputStreamReader(bais, "UTF-8");
						BufferedReader br = new BufferedReader( (Reader)isr );

						List<EquipmentClass> list = TextParser.parse(br, "\n", ",", new TextParser.Mapper<EquipmentClass>() {
							public EquipmentClass parse(List<String> line) {
								int campo1 = Integer.parseInt(((String)line.get(0)).trim());

								String campo2 = ((String)line.get(1)).trim();

								//								String campo3 = (line.size() > 2) ? "br.com.padtec.v3.data.impl." +
								String campo3 = (line.size() > 2) ? "br.com.padtec.v3.data.impl." +
										((String)line.get(2)).trim() + "_Impl" : null;
										return new EquipmentClass(campo1, campo2, campo3);
							}

						});
						for (EquipmentClass item : list) {
							while (cache.size() <= item.part) {
								cache.add(null);
							}
							cache.set(item.part, item);
						}



					}
					catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			});
			this.df.start();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void stop()
	{
		this.df.stop();
	}

	public EquipmentClass getEquipmentClass(int partNumber)
	throws ClassNotFoundException
	{
		if (this.cache.size() > partNumber) {
			EquipmentClass item = (EquipmentClass)this.cache.get(partNumber);
			if (item != null) {
				if ((item.className == null) || (item.className.length() == 0))
				{
					item.className = guessClassName(item);
				}
				return item;
			}
		}
		throw new ClassNotFoundException("No reference found for part number " + 
				partNumber);
	}

	private String guessClassName(EquipmentClass item)
	{
		float elementScore;
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		float currentScore = 0.0F;
		for (EquipmentClass element : this.cache)
			if ((element != null) && (element.className != null)) {
				if (element.className.length() == 0)
				{
					continue;
				}

				if (element == item) {
					continue;
				}
				elementScore = getScore(item.model, element.model);
				if (elementScore > currentScore) {
					currentScore = elementScore;
					result.clear();
					result.put(element.className, Integer.valueOf(1));
				} else if (elementScore == currentScore) {
					Integer classScore = (Integer)result.get(element.className);
					result.put(element.className, 
							Integer.valueOf((classScore != null) ? classScore.intValue() + 1 : 1));
				}
			}
		Map.Entry<String,Integer> bestEntry = null;
		for (Map.Entry<String, Integer> entry : result.entrySet()) {
			if ((bestEntry != null) && 
					((bestEntry.getValue()).intValue() >= (entry.getValue()).intValue())) 
				bestEntry = entry;
		}

		return ((bestEntry == null) ? null : (String)bestEntry.getKey());
	}

	private float getScore(String str1, String str2)
	{
		int min;
		float max;
		if (str1.length() < str2.length()) {
			min = str1.length();
			max = str2.length();
		} else {
			min = str2.length();
			max = str1.length();
		}
		int count = 0;
		for (int i = 0; i < min; ++i) {
			if (str1.charAt(i) == str2.charAt(i)) {
				++count;
			}
		}
		return (count / max);
	}

	public String getClassName(int partNumber)
	throws ClassNotFoundException
	{
		return getEquipmentClass(partNumber).className;
	}

	public Map<Integer, String> getModels()
	{
		Map<Integer, String> result = new TreeMap<Integer, String>();
		for (EquipmentClass item : this.cache) {
			if (item != null) {
				result.put(Integer.valueOf(item.part), item.model);
			}
		}
		return result;
	}

	public String getModel(int part)
	{
		try
		{
			return getEquipmentClass(part).model; 
		} catch (ClassNotFoundException e) {
		}
		return Msg.getString("Unknown") + "(" + part + ")";
	}

	public List<String> getModels(String className)
	{
		String name = "br.com.padtec.v3.data.impl." + className + "_Impl";
		List<String> result = new LinkedList<String>();
		for (EquipmentClass item : this.cache) {
			if ((item == null) || (item.className == null) || 
					(!(item.className.equals(name)))) 
				result.add(item.model);
		}

		return result;
	}

	public List<String> getAmplifiersModels()
	{
		List<String> result = new LinkedList<String>();
		for (EquipmentClass item : this.cache) {
			if ((item == null) || (
					(!("br.com.padtec.v3.data.impl.PBAmp_Impl".equals(item.className))) && 
					(!("br.com.padtec.v3.data.impl.RAmp_Impl".equals(item.className))) && 
					(!("br.com.padtec.v3.data.impl.RAmpNoPout_Impl"
							.equals(item.className))) && 
							(!("br.com.padtec.v3.data.impl.PBAmpALS_Impl"
									.equals(item.className))))) continue;
			result.add(item.part + " - " + item.model);
		}

		return result;
	}

	public String getList()
	{
		StringBuilder result = new StringBuilder();
		for (EquipmentClass ec : this.cache) {
			if (ec != null) {
				String className = ec.className;
				className = className.replace("br.com.padtec.v3.data.impl.", "");
				className = className.replace("_Impl", "");
				result.append(ec.part);
				result.append(',');
				result.append(ec.model);
				result.append(',');
				result.append(className);
				result.append('\n');
			}
		}
		String returnStr = result.toString();
		result = null;
		return returnStr;
	}

	public Integer getPart(String model)
	{
		int resultPart = 0;
		for (EquipmentClass item : this.cache) {
			if ((item != null) && 
					((item.model.equals(model)))) 
				resultPart = item.part;
		}

		return Integer.valueOf(resultPart);
	}

	public List<Integer> getVirtualElementPart()
	{
		List<Integer> resultPart = new ArrayList<Integer>();
		List<String> virtualElements = new ArrayList<String>();
		virtualElements.add("Mux");
		virtualElements.add("Demux");
		virtualElements.add("MuxDemux");
		virtualElements.add("Client");
		virtualElements.add("SOMSOD");
		virtualElements.add("OADM");
		virtualElements.add("FANNotMgmt");
		virtualElements.add("DCM");
		virtualElements.add("OADMWDM");

		for (EquipmentClass item : this.cache) {
			if (item != null) {
				for (int i = 0; i < virtualElements.size(); ++i) {
					String name = "br.com.padtec.v3.data.impl." + ((String)virtualElements.get(i)) + 
					"_Impl";
					if (item.className.equals(name)) {
						resultPart.add(Integer.valueOf(item.part));
						break;
					}
				}
			}
		}
		return resultPart;
	}

	public static void main(String[] args)
	{
		CustomResourceBundle rb = getInstance();
		for (EquipmentClass item : rb.cache) {
			if (item != null)
				if ((item.className != null) && (item.className.length() > 0)) {
					System.out.print(item.part);
					System.out.print(',');
					System.out.print(item.model);
					System.out.print(',');
					System.out.print(item.className.substring(27).replaceAll("_Impl", ""));
					System.out.println();
				}
				else
				{
					String guess = rb.guessClassName(item);
					System.out.print(item.part);
					System.out.print(',');
					System.out.print(item.model);
					System.out.print(',');
					System.out.print(guess.substring(27).replaceAll("_Impl", ""));
					System.out.print(" [check]");
					System.out.println();
				}
		}
	}

	public int getLastPartByClassName(String className)
	{
		int highestPart = -1;
		for (EquipmentClass item : this.cache) {
			if ((item == null) || 
					(item.part >= 2000) || 
					(!(item.className.equals(className)))) 
				highestPart = Math.max(highestPart, item.part);
		}

		return highestPart;
	}

	public Map<String, String> getLastPartByClassName() {
		Map<String,String> result = new TreeMap<String, String>();
		for (EquipmentClass item : this.cache) {
			if (item != null) {
				result.put(item.className, item.model);
			}
		}
		return result;
	}

	final class EquipmentClass
	{
		public int part;
		public String model;
		public String className;

		public EquipmentClass(int paramInt, String paramString1, String paramString2)
		{
			this.part = paramInt;
			this.model = paramString1;
			this.className = paramString2;
		}
	}
}