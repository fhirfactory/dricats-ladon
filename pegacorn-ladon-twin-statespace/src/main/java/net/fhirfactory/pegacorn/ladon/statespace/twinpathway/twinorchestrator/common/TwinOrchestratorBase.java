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
package net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinorchestrator.common;

import net.fhirfactory.pegacorn.ladon.model.behaviours.ExplicitStimulus2TwinInstanceMap;
import net.fhirfactory.pegacorn.ladon.model.stimuli.Stimulus;
import net.fhirfactory.pegacorn.ladon.model.twin.DigitalTwinIdentifier;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.BehaviourCentricExclusiveFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.BehaviourCentricInclusiveFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.StimulusPackage;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathwaycontroller.common.TwinPathwayControllerBase;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathwaycontroller.common.TwinTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import org.apache.camel.builder.RouteBuilder;
import org.hl7.fhir.r4.model.Identifier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TwinOrchestratorBase extends RouteBuilder {

    private ConcurrentHashMap<Stimulus, DigitalTwinIdentifier> stimulusInterestSet;
    private List<BehaviourCentricInclusiveFilterRulesInterface> inclusiveFilterList;
    private List<BehaviourCentricExclusiveFilterRulesInterface> exclusiveFilterList;
    private TwinTypeEnum twinType;

    @Inject
    private LadonProcessingPlant ladonPlant;

    public TwinOrchestratorBase(){
        this.twinType = specifyTwinType();
    }

    @PostConstruct
    protected void initialise(){
        ladonPlant.initialisePlant();

    }

    abstract protected TwinPathwayControllerBase specifyPathwayController();
    abstract protected TwinTypeEnum specifyTwinType();

    public void processNextStimulusPackageForInstance(Identifier instanceId){

    }

    public void orchestrateInstance(Identifier instanceId){

    }

    @Override
    public void configure() throws Exception {

    }

    public void registerBehaviourStimulusSubscription(BehaviourCentricInclusiveFilterRulesInterface behaviourSubscriptionSet){
        List<ExplicitStimulus2TwinInstanceMap> explicitStimulus2TwinInstanceMaps = behaviourSubscriptionSet.positiveStaticFilterTwinInstance2StimulusMap();
        // 1st, extract the set of topic tokens that we should ge the StimuliCollector to listen for
        List<TopicToken> resourceSet = new ArrayList<TopicToken>();
        for(ExplicitStimulus2TwinInstanceMap behaviorRequirement: explicitStimulus2TwinInstanceMaps){
            resourceSet.addAll(behaviorRequirement.getStimulusRequirementMap().keySet());
        }


    }
}
