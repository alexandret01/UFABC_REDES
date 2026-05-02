package br.com.padtec.v3.util;

class PairCodeValue  implements Comparable<PairCodeValue>  {

    public int compareTo(PairCodeValue p) {
        return Double.compare(value, p.value);
    }

    int code;
    double value;

   public  PairCodeValue(int c, double v) {
        code = c;
        value = v;
    }
    
}