package br.com.padtec.v3.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.modelparser.Transponder1UModelParser;


public final class TrpTables {
  public static double[] freqTable = { 5193400.0D, 5193000.0D, 5293000.0D, 5398900.0D, 
    5512400.0D, 5632500.0D, 5761200.0D, 5899500.0D, 6047300.0D, 6209700.0D, 6381900.0D, 
    6567500.0D, 6768800.0D, 6987400.0D, 7226200.0D, 7486300.0D, 7413900.0D, 7413500.0D, 
    7560600.0D, 7717300.0D, 7885200.0D, 
    8063300.0D, 8254800.0D, 8458600.0D, 8678400.0D, 8918000.0D, 9173600.0D, 9448100.0D, 
    9746400.0D, 10068000.0D, 10417000.0D, 
    10791000.0D, 10387000.0D, 10386000.0D, 10586000.0D, 10798000.0D, 11025000.0D, 11265000.0D, 
    11522000.0D, 11799000.0D, 12095000.0D, 
    12419000.0D, 12764000.0D, 13135000.0D, 13538000.0D, 13975000.0D, 14452000.0D, 14973000.0D, 
    14828000.0D, 14827000.0D, 15121000.0D, 
    15435000.0D, 15770000.0D, 16127000.0D, 16510000.0D, 16917000.0D, 17357000.0D, 17836000.0D, 
    18347000.0D, 18896000.0D, 19493000.0D, 
    20136000.0D, 20833000.0D, 21582000.0D, 20774000.0D, 20772000.0D, 21172000.0D, 21596000.0D, 
    22049000.0D, 22530000.0D, 23045000.0D, 
    23598000.0D, 24189000.0D, 24839000.0D, 25527000.0D, 26270000.0D, 27075000.0D, 27950000.0D, 
    28905000.0D, 29945000.0D, 29655000.0D, 
    29654000.0D, 30242000.0D, 30869000.0D, 31541000.0D, 32253000.0D, 33019000.0D, 33834000.0D, 
    34714000.0D, 35672000.0D, 36694000.0D, 
    37792000.0D, 38985000.0D, 40273000.0D, 41666000.0D, 43164000.0D, 41547000.0D, 41544000.0D, 
    42344000.0D, 43191000.0D, 44099000.0D, 
    45060000.0D, 46090000.0D, 47196000.0D, 48378000.0D, 49678000.0D, 51055000.0D, 52540000.0D, 
    54150000.0D, 55899000.0D, 57810000.0D, 
    59890000.0D, 59311000.0D, 59308000.0D, 60485000.0D, 61739000.0D, 33081000.0D, 64506000.0D, 
    66038000.0D, 66769000.0D, 69427000.0D, 
    71344000.0D, 73388000.0D, 75585000.0D, 77971000.0D, 80546000.0D, 83333000.0D, 86328000.0D, 
    83095000.0D, 83087000.0D, 84689000.0D, 
    86383000.0D, 88198000.0D, 90120000.0D, 92179000.0D, 94392000.0D, 96757000.0D, 99356000.0D, 
    102110000.0D, 105080000.0D, 108300000.0D, 
    111800000.0D, 115620000.0D, 119780000.0D, 118620000.0D, 118620000.0D, 120970000.0D, 123480000.0D, 
    126160000.0D, 129010000.0D, 132080000.0D, 
    135340000.0D, 138850000.0D, 142690000.0D, 146780000.0D, 151170000.0D, 155940000.0D, 161090000.0D, 
    166670000.0D, 172660000.0D, 166190000.0D, 
    166170000.0D, 169380000.0D, 172770000.0D, 176400000.0D, 180240000.0D, 184360000.0D, 188780000.0D, 
    193510000.0D, 198710000.0D, 204220000.0D, 
    210160000.0D, 216600000.0D, 223600000.0D, 231240000.0D, 239560000.0D, 237240000.0D, 237230000.0D, 
    241940000.0D, 246950000.0D, 252330000.0D, 
    258020000.0D, 264150000.0D, 270670000.0D, 277710000.0D, 285380000.0D, 293550000.0D, 302340000.0D, 
    311880000.0D, 322180000.0D, 333330000.0D, 
    345310000.0D, 332380000.0D, 332350000.0D, 338760000.0D, 345530000.0D, 352790000.0D, 360480000.0D, 
    368720000.0D, 377570000.0D, 387030000.0D, 
    397420000.0D, 408440000.0D, 420320000.0D, 433200000.0D, 447190000.0D, 462480000.0D, 479120000.0D, 
    474490000.0D, 474470000.0D, 483880000.0D, 
    493910000.0D, 504650000.0D, 516050000.0D, 528310000.0D, 541350000.0D, 555420000.0D, 570750000.0D, 
    587110000.0D, 604680000.0D, 623770000.0D, 
    644370000.0D, 666660000.0D, 690620000.0D, 664760000.0D, 664700000.0D, 677510000.0D, 691060000.0D, 
    705580000.0D, 720960000.0D, 737430000.0D, 
    755140000.0D, 774050000.0D, 794850000.0D, 816880000.0D, 840640000.0D, 866400000.0D, 894380000.0D, 
    924960000.0D, 958250000.0D, 948980000.0D, 
    948930000.0D, 967760000.0D, 987820000.0D, 1009300000.0D, 1032100000.0D, 1056600000.0D, 1082700000.0D, 
    1110800000.0D, 1141500000.0D, 1174200000.0D, 
    1209400000.0D, 1247500000.0D, 1288700000.0D, 1333300000.0D, 1381200000.0D, 1329500000.0D, 1329400000.0D, 
    1355000000.0D, 1382100000.0D, 1411200000.0D, 
    1441900000.0D, 1474900000.0D, 1510300000.0D, 1548100000.0D, 1589700000.0D, 1633800000.0D, 1681300000.0D, 
    1732800000.0D, 1788800000.0D, 1849900000.0D, 
    1916500000.0D, 1898000000.0D, 1897900000.0D, 1935500000.0D, 1975600000.0D, 2018600000.0D, 2064800000.0D, 
    2113200000.0D, 2165400000.0D, 2221700000.0D, 
    2283000000.0D, 2348400000.0D, 2418700000.0D, 2495100000.0D, 2577500000.0D, 2666600000.0D, 2762500000.0D };
  public static final int ERROR_ON_RESULT = -127;
  private static final double[] lambdaTable = { 1310.0D, 1330.0D, 1350.0D, 
    1370.0D, 1390.0D, 1410.0D, 1430.0D, 1450.0D, 1470.0D, 1490.0D, 1510.0D, 1530.0D, 1550.0D, 1570.0D, 1590.0D, 
    1610.0D, -127.0D, -127.0D, 1563.05D, 1562.23D, 1561.4200000000001D, 
    1560.6099999999999D, 1559.79D, 1558.98D, 1558.1700000000001D, 1557.3599999999999D, 1556.55D, 1555.75D, 1554.9400000000001D, 
    1554.1300000000001D, 1553.3299999999999D, 1552.52D, 1551.72D, 1550.9200000000001D, 1550.1199999999999D, 1549.3199999999999D, 1548.51D, 
    1547.72D, 1546.9200000000001D, 1546.1199999999999D, 1545.3199999999999D, 1544.53D, 1543.73D, 1542.9400000000001D, 1542.1400000000001D, 
    1541.3499999999999D, 1540.5599999999999D, 1539.77D, 1538.98D, 1538.1900000000001D, 1537.4000000000001D, 1536.6099999999999D, 1535.8199999999999D, 
    1535.04D, 1534.25D, 1533.47D, 1532.6800000000001D, 1531.9000000000001D, 1531.1199999999999D, 1530.3299999999999D, 1529.55D, 
    1528.77D, 1527.99D, 1609.1900000000001D, 1608.3299999999999D, 1607.47D, 1606.5999999999999D, 1605.74D, 1604.8800000000001D, 
    1604.03D, 1603.1700000000001D, 1602.3099999999999D, 1601.46D, 1600.5999999999999D, 1599.75D, 1598.8900000000001D, 1598.04D, 
    1597.1900000000001D, 1596.3399999999999D, 1595.49D, 1594.6400000000001D, 1593.79D, 1592.95D, 1592.0999999999999D, 1591.26D, 
    1590.4100000000001D, 1589.5699999999999D, 1588.73D, 1587.8800000000001D, 1587.04D, 1586.2D, 1585.3599999999999D, 1584.53D, 
    1583.6900000000001D, 1582.8499999999999D, 1582.02D, 1581.1800000000001D, 1580.3499999999999D, 1579.52D, 1578.6900000000001D, 1577.8599999999999D };

  public static final String getChannelDWDM(int canal)
  {
    if (canal == 100)
      return "L00";
    if (canal < 100) {
      if (canal >= 63)
        return "L" + canal;
      if ((canal >= 18) && (canal <= 62))
        return "C" + canal;
    }
    return "Unknown";
  }

  public static final String getChannel(int canal) {
    if (canal == 255) {
      return "1.5u";
    }
    if (canal == 254) {
      return "1.3u";
    }
    if (canal < 16)
      return getChannelCWDM(canal);
    return getChannelDWDM(canal);
  }

  public static final String getChannelCWDM(int canal)
  {
    String ret;
    switch (canal)
    {
    case 0:
      ret = "O3";
      break;
    case 1:
      ret = "O4";
      break;
    case 2:
      ret = "O5";
      break;
    case 3:
      ret = "E1";
      break;
    case 4:
      ret = "E2";
      break;
    case 5:
      ret = "E3";
      break;
    case 6:
      ret = "E4";
      break;
    case 7:
      ret = "E5";
      break;
    case 8:
      ret = "S1";
      break;
    case 9:
      ret = "S2";
      break;
    case 10:
      ret = "S3";
      break;
    case 11:
      ret = "C1";
      break;
    case 12:
      ret = "C2";
      break;
    case 13:
      ret = "L1";
      break;
    case 14:
      ret = "L2";
      break;
    case 15:
      ret = "L3";
      break;
    default:
      ret = "Unknown";
    }
    return ret;
  }

  public static double getLambda(int canal)
  {
    if ((canal < 0) || (canal >= lambdaTable.length)) {
      return -127.0D;
    }
    if (canal == 252)
      return 1490.0D;
    if (canal == 253) {
      return 850.0D;
    }

    return lambdaTable[canal];
  }

  public static int getLambdaCode(double d)
  {
    for (int i = 0; i < lambdaTable.length; ++i) {
      if (lambdaTable[i] == d)
        return i;
    }
    return -1;
  }

  public static boolean isPinLog(NE ne)  {
    String model = ne.getModel();
    boolean extendedPinMeasure = Transponder1UModelParser.isExtendedInputPowerMeasure(model);
    return (!(extendedPinMeasure));
  }

  public static double getPower(int pot, int pos, byte[] tabela, boolean applyLog)
  {
    try
    {
      double ret;
      double x1 = Functions.b2i(tabela[(pos + 0)]);
      double y1 = -Functions.b2i(tabela[(pos + 1)]);
      double x2 = Functions.b2i(tabela[(pos + 2)]);
      double y2 = -Functions.b2i(tabela[(pos + 3)]);

      if (applyLog) {
        y1 = Math.pow(10.0D, y1 / 10.0D);
        y2 = Math.pow(10.0D, y2 / 10.0D);
      }
      if (x1 == x2)
        ret = (0.0D / 0.0D);
      else {
        ret = ((y1 - y2) * pot + x1 * y2 - (x2 * y1)) / (x1 - x2);
      }
      if (applyLog) {
        if (ret > 0.0D)
          ret = 10.0D * Math.log(ret) / Math.log(10.0D);
        else {
          ret = (0.0D / 0.0D);
        }

      }

      if (!(Double.isNaN(ret))) {
        ret *= 100.0D;
        ret = Math.round(ret);
        ret /= 100.0D;
      }

      return ret; } catch (Exception e) {
    }
    return (0.0D / 0.0D);
  }

  public static double getPot10G(boolean isPin, byte[] tabela)
  {
    if (isPin)
    {
      int p = Functions.b2i(tabela[1]) * 16777216 + Functions.b2i(tabela[2]) * 65536 + 
        Functions.b2i(tabela[3]) * 256 + Functions.b2i(tabela[4]);
      if (p == 0)
        return (0.0D / 0.0D);
      double pin = p * 1.0E-006D;
      double ret = 10.0D * Math.log10(pin);
      ret *= 100.0D;
      ret = (int)ret;
      return (ret / 100.0D);
    }

    int p = Functions.b2i(tabela[6]) * 16777216 + Functions.b2i(tabela[7]) * 65536 + 
      Functions.b2i(tabela[8]) * 256 + 
      Functions.b2i(tabela[9]);
    if (p == 0)
      return (0.0D / 0.0D);
    double pout = p * 0.001D;
    double ret = 10.0D * Math.log10(pout);
    ret *= 100.0D;
    ret = (int)ret;
    return (ret / 100.0D);
  }

  public static void getPot10GTabela(boolean isPin, byte[] tabela, double pot)
  {
    byte[] dataArray;
    if (isPin) {
      pot = Math.pow(10.0D, pot / 10.0D);
      pot /= 1.0E-006D;
      dataArray = Functions.l2b((long)pot, 4);
      System.arraycopy(dataArray, 0, tabela, 1, dataArray.length);
    } else {
      pot = Math.pow(10.0D, pot / 10.0D);
      pot /= 0.001D;
      dataArray = Functions.l2b((long)pot, 4);
      System.arraycopy(dataArray, 0, tabela, 6, dataArray.length);
    }
  }

  public static String getTaxaString(double d) {
    String number;
    if ((d == -127.0D) || (Double.isNaN(d))) {
      return Msg.getString("nullNumber");
    }
    NumberFormat f = NumberFormat.getNumberInstance();
    f.setMaximumFractionDigits(3);
    f.setGroupingUsed(false);

    if (d > 1000000000.0D)
      number = f.format(d / 1000000000.0D) + " Gbps";
    else if (d > 1000000.0D)
      number = f.format(d / 1000000.0D) + " Mbps";
    else
      number = f.format(d / 1000000000.0D) + " bps";
    return number;
  }
  public static int getCode(double freq)
  {
      if(freq < freqTable[0])
          return 0;
      if(freq > freqTable[287])
          return 287;
      ArrayList<PairCodeValue> v = new ArrayList<PairCodeValue>(freqTable.length);
      

      for(int i = 0; i < freqTable.length; i++){
    	  double value = freqTable[i];
    	  PairCodeValue p = new PairCodeValue(i, value);
          v.add(i, p);
      }
      Collections.sort(v);
      int index = Math.abs(Collections.binarySearch(v, new PairCodeValue(-1, freq)) + 1);
      if(index > 287)
          index = 287;
      if(index < 0)
          index = 0;
      PairCodeValue p = v.get(index);
      return p.code;
  }



  public static double getFrequency(int code) {
    if ((code < 0) || (code >= freqTable.length)) {
      return (0.0D / 0.0D);
    }
    return freqTable[code];
  }

  public static double getLambdaReal(int lbd, int[] tabela)
  {
    double ret = 0.0D;

    if (tabela == null) {
      return (0.0D / 0.0D);
    }

    double lambdaNominal = getLambda(tabela[5]);

    if (lambdaNominal == -127.0D) {
      return (0.0D / 0.0D);
    }

    int[] vetorInterpola = new int[5];
    double[] vetorRet = new double[5];

    for (int i = 0; i < 5; ++i) {
      vetorInterpola[i] = tabela[(6 + i)];
    }

    vetorRet[0] = (lambdaNominal - 1.0D);
    vetorRet[1] = (lambdaNominal - 0.5D);
    vetorRet[2] = lambdaNominal;
    vetorRet[3] = (lambdaNominal + 0.5D);
    vetorRet[4] = (lambdaNominal + 1.0D);

    for (int i = 0; i < 5; ++i) {
      if (lbd == vetorInterpola[i]) {
        ret = vetorRet[i];
      }
    }
    if (ret == 0.0D) {
      for (int i = 0; i < 4; ++i) {
        if ((lbd < vetorInterpola[i]) && (lbd > vetorInterpola[(i + 1)])) {
          double x2 = vetorInterpola[i];
          double y2 = vetorRet[i];
          double x1 = vetorInterpola[(i + 1)];
          double y1 = vetorRet[(i + 1)];
          ret = ((y1 - y2) * lbd + x1 * y2 - (x2 * y1)) / (x1 - x2);
          ret *= 100.0D;
          ret = (int)ret;
          ret /= 100.0D;
        }
      }
    }
    return ret;
  }
  
 
}