package org.onosproject.opticallab;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Buffer circular em memória para DataPoints coletados.
 *
 * Capacidade padrão: 1440 pontos = 24h com coleta a cada 60s.
 * Thread-safe (synchronized).
 */
public class OpticalLabStore {

    private static final int DEFAULT_CAPACITY = 1440;

    private final int capacity;
    private final Deque<DataPoint> ring = new ArrayDeque<>();
    private volatile DataPoint latest = null;

    public OpticalLabStore() {
        this(DEFAULT_CAPACITY);
    }

    public OpticalLabStore(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(DataPoint dp) {
        if (ring.size() >= capacity) {
            ring.pollFirst();
        }
        ring.addLast(dp);
        latest = dp;
    }

    public DataPoint getLatest() {
        return latest;
    }

    public synchronized List<DataPoint> getHistory() {
        return new ArrayList<>(ring);
    }

    public synchronized int size() {
        return ring.size();
    }

    /** Exporta todos os pontos históricos como string CSV. */
    public synchronized String toCsvString() {
        StringBuilder sb = new StringBuilder(DataPoint.csvHeader());
        for (DataPoint dp : ring) {
            sb.append(dp.toCsvRows());
        }
        return sb.toString();
    }

    public synchronized void clear() {
        ring.clear();
        latest = null;
    }
}
