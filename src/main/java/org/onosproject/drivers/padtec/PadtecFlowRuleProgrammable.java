/*
 * Copyright 2017 Open Networking Foundation
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



import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleProgrammable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Flow rule programmable behaviour for Polatis optical netconf devices.
 */
public class PadtecFlowRuleProgrammable extends AbstractHandlerBehaviour implements FlowRuleProgrammable {

    private static final Logger log = getLogger(PadtecFlowRuleProgrammable.class);

    @Override
    public Collection<FlowEntry> getFlowEntries() {
    	Collection<FlowEntry> flows = new ArrayList<FlowEntry>();
        return flows;
    }

    @Override
    public Collection<FlowRule> applyFlowRules(Collection<FlowRule> rules) {
    	Collection<FlowRule> flows = new ArrayList<FlowRule>();
        return flows;
    }

    @Override
    public Collection<FlowRule> removeFlowRules(Collection<FlowRule> rules) {
    	Collection<FlowRule> flows = new ArrayList<FlowRule>();
        return flows;
    }


}
