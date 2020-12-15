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
package net.fhirfactory.pegacorn.ladon.behaviours.archetypes.common;

import net.fhirfactory.pegacorn.ladon.behaviours.framework.manager.BehaviourRouteManager;
import net.fhirfactory.pegacorn.ladon.model.behaviours.ExplicitStimulus2TwinInstanceMap;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusType;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.BehaviourCentricExclusiveFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.BehaviourCentricInclusiveFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulicollector.common.TwinStimuliCollectorBase;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.twinorchestrator.common.TwinOrchestratorBase;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.wup.archetypes.LadonStimuliTriggeredBehaviourWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GenericStimuliBasedBehaviour extends LadonStimuliTriggeredBehaviourWUP {
    private static final Logger LOG = LoggerFactory.getLogger(GenericStimuliBasedBehaviour.class);

    private static final String BEHAVIOUR_WORKSHOP = "Behaviours";

    private String name;
    private String ingresFeed;
    private String egressFeed;

    @Inject
    private BehaviourRouteManager behaviourRouteMgr;

    abstract protected String specifyBehaviourName();
    abstract protected String specifyBehaviourVersion();
    abstract protected List<BehaviourCentricExclusiveFilterRulesInterface> negativeFilterSet();
    abstract protected List<BehaviourCentricInclusiveFilterRulesInterface> positiveFilterSet();

    abstract protected TwinStimuliCollectorBase getMyCollectorService();
    abstract protected TwinOrchestratorBase getMyTwinOrchestrationService();

    /**
     * Within the context of a Behaviour, it extracts the Type of Resource (set) that this Behaviour is expecting for
     * Stimulus and registers these with the (corresponding) StimulusCollector entity.
     *
     * It then extracts the relationship map BehaviourStimulusRequirementSet and injects this into the "upstream"
     * (corresponding) DigitalTwinOrchestrator instance. This function orders and limits the DigitalTwin instances being
     * fed into this behaviour.
     *
     * Because the input (StimulusPackages) are injected by the TwinManifestor directly, we don't actually want to
     * have this WUP (which is the super-class of this one) subscribing to any topics. So we return an empty set.
     *
     * @return An empty TopicToken set.
     */
    @Override
    protected Set<TopicToken> specifySubscriptionTopics() {
        LOG.debug(".specifySubscriptionTopics(): Entry");
        //
        // 1st, lets do the (macro) Topic (Stimulus) registration process
        //
        LOG.trace(".specifySubscriptionTopics(): First, get the DigitalTwinStimulusSubscriptionCriteriaInterface list");
        for(BehaviourCentricInclusiveFilterRulesInterface positiveFilterRulesInterface: positiveFilterSet()){
            for(StimulusType requestedStimulus: positiveFilterRulesInterface.positiveStaticFilterStimulus()){
                TopicToken currentToken =  requestedStimulus.getAsTopicToken();
                LOG.trace(".specifySubscriptionTopics(): Topic of interest --> {}", currentToken);
                LOG.trace(".specifySubscriptionTopics(): Now, append the right discriminator so as to get the Topics ONLY from the PubSub service");
                currentToken.addDescriminator("Source", "Ladon.StateSpace.PubSub");
                LOG.trace(".specifySubscriptionTopics(): Call the addTopicToSubscription method on the corresponding CollectorService");
                getMyCollectorService().addTopicToSubscription(currentToken);
                LOG.trace(".specifySubscriptionTopics(): Topic added... continue");
            }
        }
        LOG.trace(".specifySubscriptionTopics(): Now iterate through each Interface Instance");
        for(BehaviourCentricInclusiveFilterRulesInterface positiveFilterRulesInterface: positiveFilterSet()){
            LOG.trace(".specifySubscriptionTopics(): Get the BehaviourStimulusRequirementSet for each interface instance");
            List<ExplicitStimulus2TwinInstanceMap> explicitStimulus2TwinInstanceMaps = positiveFilterRulesInterface.positiveStaticFilterTwinInstance2StimulusMap();
            LOG.trace(".specifySubscriptionTopics(): Iterate through each requirements set");
            for(ExplicitStimulus2TwinInstanceMap stimulusRequirements: explicitStimulus2TwinInstanceMaps){
                LOG.trace(".specifySubscriptionTopics(): Now, iterate through every actual topic identified within the requirements set");
                for(StimulusType currentStimulusType: stimulusRequirements.getStimulusRequirementMap().keySet() ){
                    TopicToken currentToken = currentStimulusType.getAsTopicToken();
                    LOG.trace(".specifySubscriptionTopics(): Topic of interest --> {}", currentToken);
                    LOG.trace(".specifySubscriptionTopics(): Now, append the right discriminator so as to get the Topics ONLY from the PubSub service");
                    currentToken.addDescriminator("Source", "Ladon.StateSpace.PubSub");
                    LOG.trace(".specifySubscriptionTopics(): Call the addTopicToSubscription method on the corresponding CollectorService");
                    getMyCollectorService().addTopicToSubscription(currentToken);
                    LOG.trace(".specifySubscriptionTopics(): Topic added... continue");
                }
            }
        }
        //
        // Now, lets inject the BehaviourStimulusSubscription into the appropriate DigitalTwinOrchestrator instance
        //
        for(BehaviourCentricInclusiveFilterRulesInterface criteriaInterface: positiveFilterSet()){
            LOG.trace(".specifySubscriptionTopics(): Get the BehaviourStimulusRequirementSet for each interface instance");
            getMyTwinOrchestrationService().registerBehaviourStimulusSubscription(criteriaInterface);
        }
        //
        // Lastly, return an empty set.
        //
        HashSet<TopicToken> myTopics = new HashSet<TopicToken>();
        LOG.debug(".specifySubscriptionTopics(): Exit");
        return(myTopics);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (specifyBehaviourName());
    }

    @Override
    protected String specifyWUPVersion() {
        return (specifyBehaviourVersion());
    }

    @Override
    protected String specifyWUPWorkshop() {
        return(BEHAVIOUR_WORKSHOP);
    }

}
