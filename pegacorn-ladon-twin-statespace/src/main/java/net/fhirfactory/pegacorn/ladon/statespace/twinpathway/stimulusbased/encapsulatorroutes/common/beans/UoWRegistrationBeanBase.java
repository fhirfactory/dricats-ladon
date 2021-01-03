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

package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common.beans;

import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import org.apache.camel.Exchange;
import org.slf4j.Logger;

import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;

import javax.inject.Inject;

public abstract class UoWRegistrationBeanBase {

	@Inject
	private DeploymentTopologyIM topologyProxy;

	@Inject
	private PetasosPathwayExchangePropertyNames exchangePropertyNames;
	
	protected abstract TwinOrchestratorBase specifyTwinOrchestrator(); 
	protected abstract Logger getLogger();
	
	protected TwinOrchestratorBase getTwinOrchestrator() {
		return(specifyTwinOrchestrator());
	}
	
	public UoW registerUoW(WorkUnitTransportPacket workPacket, Exchange camelExchange, String wupKey) {
		if( topologyProxy == null ) {
			getLogger().error(".registerUoW(): Guru Software Meditation Error: topologyProxy is null");
		}
		WUPJobCard jobCard = workPacket.getCurrentJobCard();
		ParcelStatusElement statusElement = workPacket.getCurrentParcelStatus();
		UoW currentUoW = workPacket.getPayload();
		getTwinOrchestrator().registerNewUoW(currentUoW, jobCard, statusElement, wupKey);
		return(currentUoW);
	}

}
