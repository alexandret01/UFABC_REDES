package br.com.padtec.v3.util.text;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;



public class TextParser
{
  public static <T> List<T> parse(BufferedReader br, String lineSeparator, String fieldSeparator, Mapper<T> name)
  {
	  List<T> result = new LinkedList<T>();
    while (true)
      try {
        String line = br.readLine();
        if (line == null) {
          break;
        }
        List<String> tableLine = new LinkedList<String>();
        StringTokenizer filedSt = new StringTokenizer(line, fieldSeparator);
        while (filedSt.hasMoreTokens()) {
          String field = filedSt.nextToken();
          tableLine.add(field);
        }
        filedSt = null;
        try {
          result.add(name.parse(tableLine));
        } catch (Exception localException) {
        }
        tableLine = null;
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    return result;
  }

  public static abstract interface Mapper<T>
  {
    public abstract T parse(List<String> paramList);
  }
}