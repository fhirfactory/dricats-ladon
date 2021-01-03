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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import net.fhirfactory.pegacorn.datasets.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.deployment.topology.manager.DeploymentTopologyIM;
import net.fhirfactory.pegacorn.ladon.model.outcomes.Outcome;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.orchestrator.common.caches.*;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common.TwinTypeBaseBehaviourEncapsulatorRouteWUP;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinforwardermap.TwinInstance2EdgeForwarderMap;
import net.fhirfactory.pegacorn.petasos.model.pathway.WorkUnitTransportPacket;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElementFunctionToken;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;

import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourCentricExclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourCentricInclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourIdentifier;
import net.fhirfactory.pegacorn.ladon.model.outcomes.OutcomeSet;
import net.fhirfactory.pegacorn.ladon.model.stimuli.Stimulus;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusIdentifier;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusPackage;
import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import net.fhirfactory.pegacorn.ladon.model.twin.TwinTypeEnum;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.datasets.manager.TopicIM;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.topology.NodeElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;

public abstract class TwinOrchestratorBase {

    private ConcurrentHashMap<BehaviourIdentifier, BehaviourCentricInclusionFilterRulesInterface> inclusionFilterMap;
    private ConcurrentHashMap<BehaviourIdentifier, BehaviourCentricExclusionFilterRulesInterface> exclusionFilterMap;
    private ConcurrentHashMap<DigitalTwinIdentifier, BehaviourIdentifier> twinInstanceBusyStatus;
    private List<TopicToken> subscribedTopicList;
    private ConcurrentHashMap<BehaviourIdentifier, NodeElement> behaviourSet;
    private UoWCache uowCache;
    private StimulusCache stimulusCache;
    private TwinInstanceWorkQueues twinWorkQueues;
    private CausalityMapCache causalityMap;
	private OutcomeCache outcomeCache;
	private NodeElement associatedBehaviourEncapsulatorNode;
	private boolean initialised;
    
    static final long INITIAL_DELAY = 1000; // Delay (in Milliseconds) before scanning of the Per-Instance Activity Queue occurs
    static final long DELAY = 500; // Delay (in Milliseconds) between scans of the Per-Instance Activity Queue

    @Inject
    private LadonProcessingPlant ladonPlant;
    
    @Inject
    private TopicIM topicServer;
    
    @Resource
    ManagedScheduledExecutorService scheduler;

	@Inject
	FHIRElementTopicIDBuilder fhirTopicBuilder;

	@Inject
	DeploymentTopologyIM topologyProxy;
    
    @Inject 
    CamelContext camelCTX;

    @Inject
	private FHIRContextUtility fhirContextUtility;

    @Inject
	TwinInstance2EdgeForwarderMap twinInstance2EdgeForwarderMap;

    public TwinOrchestratorBase(){
        this.inclusionFilterMap = new ConcurrentHashMap<>();
        this.exclusionFilterMap = new ConcurrentHashMap<>();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.subscribedTopicList = new ArrayList<>();
        this.twinWorkQueues = new TwinInstanceWorkQueues();
        this.twinInstanceBusyStatus = new ConcurrentHashMap<>();
        this.uowCache = new UoWCache();
        this.stimulusCache = new StimulusCache();
        this.behaviourSet = new ConcurrentHashMap<>();
		this.causalityMap = new CausalityMapCache();
		this.outcomeCache = new OutcomeCache();
		this.associatedBehaviourEncapsulatorNode = null;
		this.initialised = false;
    }

    @PostConstruct
    protected void initialise(){
    	if(!initialised) {
			ladonPlant.initialisePlant();
			this.scheduler.scheduleAtFixedRate(this::manifestor, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
			initialised = true;
		}
    }

    public void initialiseService(){
    	initialise();
	}

    /** ------------------------- Overall Flow --------------------

	 1. UoW Reception
	 2. Stimulus Instantiation (from UoW Ingres Content)
	 3. Map Stimulus --> TwinInstance via Behaivour Input Filters
	 4. Wait for TwinInstance to be Idle
	 5. Insert into Behaviour Instance Queue / Lock TwinInstance
	 6. Execute Behaviour / Update Stimulus Processing Status
	 7. Collect Outcomes / Unlock TwinInstance / Update Stimulus Processing Status
	 8. Aggregate Outcomes / Finalise Stimulus Processing
	 9. Publish Completed UoW / Flush Status / Clear Pools

	 ------------------------------------------------------------- */

	// Stage 1

	public void registerNewUoW(UoW newUoW, WUPJobCard jobCard, ParcelStatusElement statusElement, String wupKey){
		uowCache.addUoW(newUoW, jobCard, statusElement, wupKey );
	}

	// Stage 2 & 3

	public void registerNewStimulus(Stimulus newStimulus){
		stimulusCache.addStimulus(newStimulus);
		stimulusFeeder2DigitalTwin(newStimulus);
	}

	// Stage 3

	public void stimulusFeeder2DigitalTwin(Stimulus newStimulus){
		Enumeration<BehaviourIdentifier> behaviourList = inclusionFilterMap.keys();
		while(behaviourList.hasMoreElements()) {
			BehaviourIdentifier currentBehaviour = behaviourList.nextElement();
			BehaviourCentricInclusionFilterRulesInterface inclusionFilter = inclusionFilterMap.get(currentBehaviour);
			List<DigitalTwinIdentifier> digitalTwinIdentifiers = inclusionFilter.positiveDynamicFilterTwinInstancesForStimulus(newStimulus);
			for(DigitalTwinIdentifier currentTwin: digitalTwinIdentifiers){
				if(exclusionFilterMap.containsKey(currentBehaviour)){
					BehaviourCentricExclusionFilterRulesInterface exclusionFilter = exclusionFilterMap.get(currentBehaviour);
					if(!exclusionFilter.blockStimulusForDigitalTwinInstance(newStimulus, currentTwin)){
						StimulusPackage newStimulusPackage = new StimulusPackage(newStimulus.getOriginalUoWIdentifier(), currentTwin, currentBehaviour, newStimulus);
						twinWorkQueues.addStimulus2Queue(currentTwin, newStimulusPackage);
					}
				} else {
					StimulusPackage newStimulusPackage = new StimulusPackage(newStimulus.getOriginalUoWIdentifier(), currentTwin, currentBehaviour, newStimulus);
					twinWorkQueues.addStimulus2Queue(currentTwin, newStimulusPackage);
				}
			}
		}
	}

	// Stage 4 & 5

	public void manifestor(){
		Set<DigitalTwinIdentifier> twinsWithQueuedTraffic = twinWorkQueues.getTwinsWithQueuedWork();
		for(DigitalTwinIdentifier currentTwinInstance : twinsWithQueuedTraffic){
			// Check to see if the DigitalTwin is BUSY by checking the instaanceBusyStatus map.
			if(!twinInstanceBusyStatus.containsKey(currentTwinInstance)) {
				// DigitalTwin isn't busy - so let's give it something to do...
				StimulusPackage nextStimulusForTwinInstance = twinWorkQueues.getNextStimulusPackage(currentTwinInstance);
				twinInstanceBusyStatus.put(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
				injectStimulusPackageIntoBehaviourQueue(nextStimulusForTwinInstance.getTargetBehaviour(), nextStimulusForTwinInstance );
				lockTwinInstance(currentTwinInstance, nextStimulusForTwinInstance.getTargetBehaviour());
			}
		}
	}

	// Stage 5

	private void injectStimulusPackageIntoBehaviourQueue(BehaviourIdentifier behaviourId, StimulusPackage stimulusPkg) {
		getLogger().debug(".injectStimulusPackageIntoBehaviourQueue(): Entry, BehaviourIdentifier --> {}, StimulusPackage --> {}", behaviourId, stimulusPkg);
		if(!behaviourSet.containsKey(behaviourId)) {
			return;
		}
		NodeElement behaviourNode = behaviourSet.get(behaviourId);
		RouteElementNames nameSet = new RouteElementNames(behaviourNode.getNodeFunctionToken());
		ProducerTemplate prodTemplate = camelCTX.createProducerTemplate();
		prodTemplate.sendBody(nameSet.getEndPointWUPContainerIngresProcessorIngres(), stimulusPkg);
	}


	// Stage 7

	public void registerBehaviourCompletion(OutcomeSet outcomes){
		if(outcomes == null){
			return;
		}
		unlockTwinInstance(outcomes.getSourceTwin());
		outcomeCache.addOutcomeSet(outcomes);
		ArrayList<UoWIdentifier> completedUoWProcessing = new ArrayList<>();
		for(Outcome outcome: outcomes.getOutcomes()) {
			Stimulus currentStimulus = stimulusCache.getStimulus(outcome.getSourceStimulus());
			causalityMap.setProcessingStatus(BehavioursProcessingOfStimulusStatusEnum.PROCESSING_STATUS_FINISHED,outcome.getSourceBehaviour(), outcome.getAffectingTwin(), outcome.getSourceStimulus(), currentStimulus.getOriginalUoWIdentifier() );
			if(causalityMap.checkForCompletionOfProcessingByAllBehavioursForAllTwins(currentStimulus.getOriginalUoWIdentifier())){
				if(!completedUoWProcessing.contains(currentStimulus.getOriginalUoWIdentifier()))
					completedUoWProcessing.add(currentStimulus.getOriginalUoWIdentifier());
			}
		}
		for(UoWIdentifier uowId: completedUoWProcessing){
			aggregateAndPublishOutcomes(uowId);
		}
	}

	// Stage 8

	public void aggregateAndPublishOutcomes(UoWIdentifier uowId){
		if(uowId == null){
			return;
		}
		Set<StimulusIdentifier> stimulusSet = stimulusCache.getStimulusAssociatedWithUoW(uowId);
		ArrayList<Outcome> outcomeList = new ArrayList<>();
		for(StimulusIdentifier stimulusId: stimulusSet){
			outcomeList.addAll(outcomeCache.getStimulusDerivedOutcomes(stimulusId));
		}
		ArrayList<UoWPayload> payloadList = new ArrayList<>();
		UoW theUoW = uowCache.getUoW(uowId);
		for(Outcome outcome: outcomeList) {
			UoWPayload payload = new UoWPayload();
			String resourceAsString = fhirContextUtility.getJsonParser().encodeResourceToString(outcome.getOutputResource());
			payload.setPayload(resourceAsString);
			if(outcome.isForwardToRealMe()){
				Set<String> forwarderSet = twinInstance2EdgeForwarderMap.getForwarderAssociation2DigitalTwin(outcome.getAffectingTwin());
				for(String forwarderInstance: forwarderSet){
					TopicToken payloadTopic = fhirTopicBuilder.createTopicToken(outcome.getOutputResource().getResourceType().name(), "4.0.1");
					payloadTopic.addDescriminator("Destination", forwarderInstance);
					payload.setPayloadTopicID(payloadTopic);
					theUoW.getEgressContent().addPayloadElement(payload);
				}
			} else {
				TopicToken payloadTopic = fhirTopicBuilder.createTopicToken(outcome.getOutputResource().getResourceType().name(), "4.0.1");
				payload.setPayloadTopicID(payloadTopic);
				theUoW.getEgressContent().addPayloadElement(payload);
			}
		}
		theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
		publishUoW(theUoW);
		// Now Clean Up
		for(StimulusIdentifier stimulusId: stimulusSet){
			outcomeCache.removeOutcomesDerivedFromStimulus(stimulusId);
			stimulusCache.removeStimulus(stimulusId);
		}
		causalityMap.purgeUoWFromMap(uowId);
	}

	// Stage 9

	public void publishUoW(UoW outputUoW){
		getLogger().debug(".publishUoW(): Entry, outputUoW --> {}", outputUoW);
		String wupInstanceKey = uowCache.getAssociatedWUPKey(outputUoW.getInstanceID());
		NodeElement node = topologyProxy.getNodeByKey(wupInstanceKey);
		getLogger().trace(".publishUoW(): Node Element retrieved --> {}", node);
		NodeElementFunctionToken wupFunctionToken = node.getNodeFunctionToken();
		getLogger().trace(".publishUoW(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);
		RouteElementNames elementNames = new RouteElementNames(wupFunctionToken);
		WUPJobCard jobCard = uowCache.getAssociatedJobCard(outputUoW.getInstanceID());
		ParcelStatusElement statusElement = uowCache.getAssociatedStatusElement(outputUoW.getInstanceID());
		WorkUnitTransportPacket transportPacket = new WorkUnitTransportPacket(jobCard.getActivityID(), Date.from(Instant.now()), outputUoW);
		switch (outputUoW.getProcessingOutcome()) {
			case UOW_OUTCOME_SUCCESS:
				getLogger().trace(".receiveFromWUP(): UoW was processed successfully - updating JobCard/StatusElement to FINISHED!");
				jobCard.setCurrentStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
				jobCard.setRequestedStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
				statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FINISHED);
				statusElement.setEntryDate(Date.from(Instant.now()));
				break;
			case UOW_OUTCOME_NOTSTARTED:
			case UOW_OUTCOME_INCOMPLETE:
			case UOW_OUTCOME_FAILED:
			default:
				getLogger().trace(".receiveFromWUP(): UoW was not processed or processing failed - updating JobCard/StatusElement to FAILED!");
				jobCard.setCurrentStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
				jobCard.setRequestedStatus(WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
				statusElement.setParcelStatus(ResilienceParcelProcessingStatusEnum.PARCEL_STATUS_FAILED);
				statusElement.setEntryDate(Date.from(Instant.now()));
				break;
		}
		transportPacket.setCurrentJobCard(jobCard);
		transportPacket.setCurrentParcelStatus(statusElement);
		ProducerTemplate prodTemplate = camelCTX.createProducerTemplate();
		prodTemplate.sendBody(elementNames.getEndPointWUPContainerEgressProcessorIngres(), transportPacket);
	}

    //
    // 
    // General Getters/Setters
    //
    //
    
    public TwinTypeEnum getTwinType() {
    	return(specifyTwinType());
    }
    
	protected List<TopicToken> getSubscribedTopicList(){
		return(this.subscribedTopicList);
	}
	
	protected NodeElement getAssociatedBehaviourEncapsulatorNode() {
		return(this.associatedBehaviourEncapsulatorNode);
	}

    //
    //
    // Abstracted Methods to be Implemented by sub-types
    //
    //

	abstract protected Logger getLogger();
    abstract protected TwinTypeEnum specifyTwinType();

    //
    //
    // Configuration Methods for Behaviour Encapsulation Route WUP
    //
    //

	public void requestSubscrption(List<TopicToken> topicList) {
		getSubscribedTopicList().addAll(topicList);
		if(getAssociatedBehaviourEncapsulatorNode() != null) {
			for (TopicToken topicToken : getSubscribedTopicList()) {
				topicServer.addTopicSubscriber(topicToken, getAssociatedBehaviourEncapsulatorNode().getContainingElementID());
			}
		}
	}

	public void registerEncapsulatorWUPNode(NodeElement encapsulatorNode){
		this.associatedBehaviourEncapsulatorNode = encapsulatorNode;
	}

	//
    //
    // Business / Configuration Methods for Behaviours
    //
    //
    
	public void registerBehaviourCentricInclusiveFilterRules(BehaviourIdentifier behaviourId, BehaviourCentricInclusionFilterRulesInterface inclusionRules) {
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		inclusionFilterMap.put(behaviourId, inclusionRules);
		getLogger().debug(".registerBehaviourCentricInclusiveFilterRules(): Exit");
	}

	public void registerBehaviourCentricExclusiveFilterRules(BehaviourIdentifier behaviourId, BehaviourCentricExclusionFilterRulesInterface exclusionRules) {
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Entry, BehaviourIdentifier --> {}", behaviourId);
		exclusionFilterMap.put(behaviourId, exclusionRules);
		getLogger().debug(".registerBehaviourCentricExclusiveFilterRules(): Exit");
	}
    
	public void registerBehaviourNode(BehaviourIdentifier behaviourId, NodeElement behaviourNode) {
		getLogger().debug(".registerBehaviourNode() Entry, BehaviourIdentifier --> {}, NodeElement --> {}", behaviourId, behaviourNode);
		if(!behaviourSet.containsKey(behaviourId)) {
			behaviourSet.put(behaviourId,  behaviourNode);
		}
		getLogger().debug(".registerBehaviourNode() Exit");
	}
    
	//
	//
    // Twin Instance Active/Busy Status Logic
	//
	//

    public void lockTwinInstance(DigitalTwinIdentifier twinIdentifier, BehaviourIdentifier behaviourIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return;
        }
        twinInstanceBusyStatus.put(twinIdentifier, behaviourIdentifier);
    }

    public void unlockTwinInstance(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            twinInstanceBusyStatus.remove(twinIdentifier);
        }
    }

    public boolean isTwinLocked(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(true);
        } else {
            return(false);
        }
    }

    public boolean isBusy(DigitalTwinIdentifier twinIdentifier){
        return(isTwinLocked(twinIdentifier));
    }

    public BehaviourIdentifier getTwinActiveBehaviour(DigitalTwinIdentifier twinIdentifier){
        if(twinInstanceBusyStatus.containsKey(twinIdentifier)){
            return(twinInstanceBusyStatus.get(twinIdentifier));
        } else {
            return(null);
        }
    }
}
