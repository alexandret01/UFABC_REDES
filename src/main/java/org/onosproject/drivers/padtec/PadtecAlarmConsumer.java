/*
 * Copyright 2018 Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.drivers.padtec;

import com.google.common.collect.ImmutableList;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmConsumer;
import org.onosproject.alarm.AlarmId;
import org.onosproject.alarm.DefaultAlarm;
import org.onosproject.alarm.XmlEventParser;
import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.DriverData;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.HandlerBehaviour;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfException;
import org.onosproject.mastership.MastershipService;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.onosproject.alarm.Alarm.SeverityLevel;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Polatis specific implementation to provide a list of current alarms.
 */
public class PadtecAlarmConsumer implements AlarmConsumer, HandlerBehaviour {
    private final Logger log = getLogger(getClass());

    private static final String ALARM_TIME = "alarm-time";
    private static final String ALARM_TYPE = "alarm-type";
    private static final String ALARM_TYPE_LOS = "NOTIF_PORT_POWER";
    private static final String ALARM_MESSAGE = "alarm-message";

    private DeviceId deviceId;
    private DriverHandler handler;

    @Override
    public List<Alarm> consumeAlarms() {
        DriverHandler handler = handler();
        deviceId = handler.data().deviceId();

        List<Alarm> alarms = new ArrayList<>();

        return ImmutableList.copyOf(alarms);
    }

    @Override
    public DriverHandler handler() {
        return handler;
    }

    @Override
    public void setHandler(DriverHandler handler) {
        this.handler = handler;
    }

    @Override
    public DriverData data() {
        return null;
    }

    @Override
    public void setData(DriverData data) {

    }
}
