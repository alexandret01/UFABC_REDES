package org.onosproject.drivers.padtec;

import org.onosproject.net.AbstractAnnotated;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.Annotations;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;

/**
 * Based on org.onosproject.net.device.DefaultPortStatistics
 */
public class PadtecPortStatistics extends AbstractAnnotated implements PortStatistics {
    DeviceId deviceId;
    PortNumber portNumber;
    double rxPwr;
    double txPwr;
    double lambda;
    String channel;
    boolean isLOS;
    boolean isLOF;
    boolean isBDI;
    double bpi8Rate;
    double beiRate;
    double fecErr;
    double fecRate;
    double rxPwrClient;
    double txPwrClient;
    double isLOSClient;
    //Ohter variabbles
    private final long packetsReceived=0;
    private final long packetsSent=0;
    private final long bytesReceived=0;
    private final long bytesSent=0;
    private final long packetsRxDropped=0;
    private final long packetsTxDropped=0;
    private final long packetsRxErrors=0;
    private final long packetsTxErrors=0;
    private final long durationSec=0;
    private final long durationNano=0;

    PadtecPortStatistics(DeviceId deviceId, PortNumber portNumber, double rxPwr, double txPwr, double lambda, String channel, boolean isLOS, boolean isLOF, boolean isBDI, double bpi8Rate, double beiRate, double fecErr, double fecRate, double rxPwrClient, double txPwrClient, double isLOSClient, Annotations annotations) {
        super(annotations);
        this.rxPwr = rxPwr;
        this.txPwr = txPwr;
        this.lambda = lambda;
        this.channel = channel;
        this.isLOS = isLOS;
        this.isLOF = isLOF;
        this.isBDI = isBDI;
        this.bpi8Rate = bpi8Rate;
        this.beiRate = beiRate;
        this.fecErr = fecErr;
        this.fecRate = fecRate;
        this.rxPwrClient = rxPwrClient;
        this.txPwrClient = txPwrClient;
        this.isLOSClient = isLOSClient;
    }

    @Override
    public PortNumber portNumber() {
        return this.portNumber;
    }

    @Override
    public long packetsReceived() {
        return this.packetsReceived;
    }

    @Override
    public long packetsSent() {
        return this.packetsSent;
    }

    @Override
    public long bytesReceived() {
        return this.bytesReceived;
    }

    @Override
    public long bytesSent() {
        return this.bytesSent;
    }

    @Override
    public long packetsRxDropped() {
        return this.packetsRxDropped;
    }

    @Override
    public long packetsTxDropped() {
        return this.packetsTxDropped;
    }

    @Override
    public long packetsRxErrors() {
        return this.packetsRxErrors;
    }

    @Override
    public long packetsTxErrors() {
        return this.packetsTxErrors;
    }

    @Override
    public long durationSec() {
        return this.durationSec;
    }

    @Override
    public long durationNano() {
        return this.durationNano;
    }

    @Override
    public boolean isZero() {
        return  bytesReceived() == 0 &&
                bytesSent() == 0 &&
                packetsReceived() == 0 &&
                packetsRxDropped() == 0 &&
                packetsSent() == 0 &&
                packetsTxDropped() == 0;
    }


    @Override
    public String toString() {
        return "device: " + deviceId + ", " +
                "port: " + this.portNumber + ", " +
                "" +
                "annotations: " + annotations();
    }

}
