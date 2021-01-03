/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.BusinessUnitTwinOrchestrator;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.OrganizationTwinOrchestrator;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerEgressGatekeeper;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerEgressProcessor;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerIngresGatekeeper;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.WUPContainerIngresProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.ladon.model.twin.TwinTypeEnum;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common.TwinTypeBaseBehaviourEncapsulatorRouteWUP;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common.beans.UoW2StimulusListBean;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.beans.OrganizationStimulusRegistrationBean;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.beans.OrganizationUoWRegistrationBean;

@ApplicationScoped
public class OrganizationTwinTypeWUPBehaviourEncapsulator extends TwinTypeBaseBehaviourEncapsulatorRouteWUP {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationTwinTypeWUPBehaviourEncapsulator.class );
    private static final String ORGANIZATION_TWIN_TYPE_STIMULI_COLLECTOR_WUP_VERSION = "1.0.0";
    
    @Override
    protected Logger getLogger(){return(LOG);}

    @Override
    protected String specifyTwinTypeName() {
        return (TwinTypeEnum.ORGANIZATION_TWIN.getTwinTypeName());
    }

    @Override
    protected String specifyTwinTypeVersion() {
        return (ORGANIZATION_TWIN_TYPE_STIMULI_COLLECTOR_WUP_VERSION);
    }

    @Inject
    OrganizationTwinOrchestrator orchestrator;

    @Override
    protected TwinOrchestratorBase getOrchestrator(){return(orchestrator);}

    @Override
    public void configure() throws Exception {        
        getLogger().debug(".configure(): Entry!, for wupNode --> {}", getWUPFunctionToken());
        getLogger().debug("OrganizationTwinTypeStimuliCollectorWUP :: EndPointWUPContainerIngresProcessorIngres --> {}", getRouteElementNameSet().getEndPointWUPContainerIngresProcessorIngres());
        getLogger().debug("OrganizationTwinTypeStimuliCollectorWUP :: EndPointWUPContainerIngresProcessorEgress --> {}", getRouteElementNameSet().getEndPointWUPContainerIngresProcessorEgress());
        getLogger().debug("OrganizationTwinTypeStimuliCollectorWUP :: EndPointWUPContainerIngresGatekeeperIngres --> {}", getRouteElementNameSet().getEndPointWUPContainerIngresGatekeeperIngres());
        getLogger().debug("OrganizationTwinTypeStimuliCollectorWUP :: EndPointWUPIngresConduitIngres --> {}", getRouteElementNameSet().getEndPointWUPIngresConduitIngres());
        getLogger().debug("OrganizationTwinTypeStimuliCollectorWUP :: EndPointWUPIngres --> {}", getRouteElementNameSet().getEndPointWUPIngres());

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerIngresProcessorIngres())
                .routeId(getRouteElementNameSet().getRouteWUPContainerIngressProcessor())
                .bean(WUPContainerIngresProcessor.class, "ingresContentProcessor(*, Exchange," + this.getWUPTopologyNodeElement().extractNodeKey() + ")")
                .to(getRouteElementNameSet().getEndPointWUPContainerIngresProcessorEgress());

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerIngresProcessorEgress())
                .routeId(getRouteElementNameSet().getRouteIngresProcessorEgress2IngresGatekeeperIngres())
                .to(getRouteElementNameSet().getEndPointWUPContainerIngresGatekeeperIngres());

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerIngresGatekeeperIngres())
                .routeId(getRouteElementNameSet().getRouteWUPContainerIngresGateway())
                .bean(WUPContainerIngresGatekeeper.class, "ingresGatekeeper(*, Exchange," + getWUPTopologyNodeElement().extractNodeKey() + ")");

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPIngresConduitIngres())
                .routeId(getRouteElementNameSet().getRouteCoreWUP())
                .bean(OrganizationUoWRegistrationBean.class, "registerUoW")
                .split().method(UoW2StimulusListBean.class, "convertUoWContent2StimulusList")
                .bean(OrganizationStimulusRegistrationBean.class, "registerStimulus");

        // --> Goes into Behaviour Sets

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerEgressProcessorIngres())
                .routeId(getRouteElementNameSet().getRouteWUPContainerEgressProcessor())
                .bean(WUPContainerEgressProcessor.class, "egressContentProcessor(*, Exchange," + getWUPTopologyNodeElement().extractNodeKey() + ")")
                .to(getRouteElementNameSet().getEndPointWUPContainerEgressProcessorEgress());

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerEgressProcessorEgress())
                .routeId(getRouteElementNameSet().getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres())
                .to(getRouteElementNameSet().getEndPointWUPContainerEgressGatekeeperIngres());

        fromWithStandardExceptionHandling(getRouteElementNameSet().getEndPointWUPContainerEgressGatekeeperIngres())
                .routeId(getRouteElementNameSet().getRouteWUPContainerEgressGateway())
                .bean(WUPContainerEgressGatekeeper.class, "egressGatekeeper(*, Exchange," + getWUPTopologyNodeElement().extractNodeKey() + ")");
    } 
}
