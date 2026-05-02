package br.com.padtec.v3.util.math;

public class Average
{
  private double average;
  private double count;

  public synchronized void add(double value)
  {
    this.count += 1.0D;
    this.average *= (this.count - 1.0D) / this.count;
    this.average += value / this.count;
  }

  public double getAverage()
  {
    if (this.count == 0.0D) {
      return (0.0D / 0.0D);
    }
    return this.average;
  }

  public long getCount()
  {
    return Math.round(this.count);
  }

  public String toString()
  {
    return Double.toString(getAverage());
  }

  public synchronized void reset()
  {
    this.average = 0.0D;
    this.count = 0.0D;
  }
}