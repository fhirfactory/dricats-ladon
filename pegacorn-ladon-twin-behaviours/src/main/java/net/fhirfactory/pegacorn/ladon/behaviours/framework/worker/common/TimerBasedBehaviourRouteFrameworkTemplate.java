/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common;

import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourRouteNames;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerBasedBehaviourRouteFrameworkTemplate extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TimerBasedBehaviourRouteFrameworkTemplate.class);

    private String behaviourName;
    private BehaviourRouteNames nameSet;

    public TimerBasedBehaviourRouteFrameworkTemplate(CamelContext context, String behaviourName) {
        super(context);
        LOG.debug(".InterchangeExtractAndRouteTemplate(): Entry, context --> ###, behaviourName (String) --> {}", behaviourName);
        this.behaviourName = behaviourName;
        nameSet = new BehaviourRouteNames(behaviourName);
    }

    @Override
    public void configure() {
        LOG.debug("TimerBasedBehaviourRouteFrameworkTemplate::configure(): Entry!, for wupNodeElement --> {}", behaviourName);
        LOG.debug("TimerBasedBehaviourRouteFrameworkTemplate::configure(): BehaviourEgressConduitEgressPoint --> {}", nameSet.getBehaviourEgressConduitEgressPoint());

        from(nameSet.getBehaviourEgressPoint())
                .routeId(nameSet.getRouteNameBehaviourEgress2EgressConduitIngres())
                .to(nameSet.getBehaviourEgressConduitIngresPoint());

        from(nameSet.getBehaviourEgressConduitIngresPoint())
                .routeId(nameSet.getRouteNameEgressConduitIngres2EgressConduitEgress())
                .bean(BehaviourEgressConduit.class, "extractStimulus(*, Exchange," +  this.getBehaviourName() + ")");
    }

    public String getBehaviourName() {
        return behaviourName;
    }

    public BehaviourRouteNames getNameSet() {
        return nameSet;
    }
}
