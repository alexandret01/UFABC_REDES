package br.ufabc.controlplane.util;

import java.util.ArrayList;
import java.util.Collections;

public class Statistic {
    
 
      public static double getAverage(ArrayList<Double> array) {
            double total = 0;
            for (int counter = 0; counter < array.size(); counter++)
                  total += array.get(counter);
            return total / array.size();
      }
 
      public static double getSumElements(ArrayList<Double> array) {
            double total = 0;
            for (int counter = 0; counter < array.size(); counter++)
            total += array.get(counter);
            return total;
      }
 
      public static double getSumSquaredElement(ArrayList<Double> array) {
            double total = 0;
            for (int counter = 0; counter < array.size(); counter++)
                  total += Math.pow(array.get(counter), 2);
            return total;
      }
 
      public static void sort(ArrayList<Double> array) {
            Collections.sort(array);
      }
 
 
      // Variância Amostral
      public static double getVariance(ArrayList<Double> array) {
            double p1 = 1 / Double.valueOf(array.size() - 1);
            double p2 = getSumElements(array) - (Math.pow(getSumElements(array), 2) / Double.valueOf(array.size()));
            return p1 * p2;
      }
 
      // Desvio Padrão Amostral
      public static double getStandardDeviation(ArrayList<Double> array) {
            return Math.sqrt(getVariance(array));
      }
 
      public static double getMediana(ArrayList<Double> array) {
            sort(array);
            int tipo = array.size() % 2;
            if (tipo == 1) {
                  return array.get(((array.size() + 1) / 2) - 1);
            } else {
                  int m = array.size() / 2;
                  return (array.get(m - 1) + array.get(m)) / 2;
            }
      }
 
//      public double getModa() {
// 
//            HashMap map = new HashMap();
//            Integer i;
//            Double moda = 0.0;
//            Integer numAtual, numMaior = 0;
//            for (int count = 0; count < array.length; count++) {
//                  i = (Integer) map.get(new Double(array[count]));
// 
//                  if (i == null) {
//                        map.put(new Double(array[count]), new Integer(1));
//                  } else {
//                        map.put(new Double(array[count]), new Integer(i.intValue() + 1));
//                        numAtual = i.intValue() + 1;
//                        if (numAtual > numMaior) {
//                             numMaior = numAtual;
//                             moda = new Double(array[count]);
// 
//                        }
// 
//                  }
//            }
//            // System.out.print("\n Eis o mapa: "+map.toString());
//            return moda;
//      }
// 
//      public double getCoefAssimetria() {
//            return (getMediaAritmetica() - getModa()) / getDesvioPadrao();
//      }
// 
//      public double[] getArray() {
//            return array;
//      }
// 
//      public void setArray(double[] array) {
//            this.array = array;
//      }
}
