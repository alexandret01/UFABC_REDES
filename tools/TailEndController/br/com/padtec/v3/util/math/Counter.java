package br.com.padtec.v3.util.math;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {
  private final AtomicLong counter;
  private long max = 9223372036854775807L;

  private long min = 0L;

  public Counter(long min, long max, boolean randomStart)
  {
    this.min = min;
    this.max = max;
    if (randomStart)
      this.counter = 
        new AtomicLong(min + 
        Math.round(Math.random() * (max - min)));
    else
      this.counter = new AtomicLong(min);
  }

  public Counter(long min, long max, long initialValue)
  {
    this.min = min;
    this.max = max;
    this.counter = new AtomicLong(initialValue);
  }

  public long next()
  {
    long result = this.counter.getAndIncrement();
    if (result > this.max) {
      reset();
    }
    return result;
  }

  public void reset()
  {
    this.counter.set(this.min);
  }
}