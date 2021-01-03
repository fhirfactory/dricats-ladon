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
package net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.twintypecentrc;

import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.BehaviourEgressConduit;
import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.BehaviourIngresConduit;
import net.fhirfactory.pegacorn.ladon.behaviours.framework.worker.common.StimulusBasedBehaviourRouteFrameworkTemplate;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourRouteNames;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessUnitBehaviourFrameworkTemplate extends StimulusBasedBehaviourRouteFrameworkTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUnitBehaviourFrameworkTemplate.class);

    public BusinessUnitBehaviourFrameworkTemplate(CamelContext context, String behaviourName) {
        super(context, behaviourName);
    }

    @Override
    public void configure() {
        LOG.debug("BusinessUnitBehaviourFrameworkTemplate::configure(): Entry!, for wupNodeElement --> {}", getBehaviourName());
        LOG.debug("BusinessUnitBehaviourFrameworkTemplate::configure(): BehaviourIngresConduitIngresPoint --> {}", getNameSet().getBehaviourIngresConduitIngresPoint());
        LOG.debug("BusinessUnitBehaviourFrameworkTemplate::configure(): BehaviourEgressConduitEgressPoint --> {}", getNameSet().getBehaviourEgressConduitEgressPoint());

        from(getNameSet().getBehaviourIngresConduitIngresPoint())
                .routeId(getNameSet().getRouteNameIngresConduitIngres2IngresConduitEgress())
                .bean(BehaviourIngresConduit.class, "injectStimulus(*, Exchange," + this.getBehaviourName() + ")")
                .to(getNameSet().getBehaviourIngresConduitEgressPoint());

        from(getNameSet().getBehaviourIngresConduitEgressPoint())
                .routeId(getNameSet().getRouteNameIngressConduitEgress2BehaviourIngres())
                .to(getNameSet().getBehaviourIngresPoint());

        from(getNameSet().getBehaviourEgressPoint())
                .routeId(getNameSet().getRouteNameBehaviourEgress2EgressConduitIngres())
                .to(getNameSet().getBehaviourEgressConduitIngresPoint());

        from(getNameSet().getBehaviourEgressConduitIngresPoint())
                .routeId(getNameSet().getRouteNameEgressConduitIngres2EgressConduitEgress())
                .bean(BusinessUnitCentricEgressConduit.class, "extractOutcomes(*)");
    }

}
