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
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourCentricExclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourCentricInclusionFilterRulesInterface;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourIdentifier;
import net.fhirfactory.pegacorn.ladon.model.behaviours.BehaviourTypeEnum;
import net.fhirfactory.pegacorn.ladon.statespace.twinpathway.stimulusbased.encapsulatorroutes.common.TwinTypeBaseBehaviourEncapsulatorRouteWUP;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GenericStimuliBasedBehaviour extends GenericBehaviour {

    @Inject
    private BehaviourRouteManager behaviourRouteMgr;

    abstract protected List<BehaviourCentricExclusionFilterRulesInterface> exclusionFilterSet();
    abstract protected List<BehaviourCentricInclusionFilterRulesInterface> inclusionFilterSet();

    abstract protected TwinTypeBaseBehaviourEncapsulatorRouteWUP getEncapsulatingWUP();

    @Override
    protected BehaviourTypeEnum specifyBehaviourType(){return(BehaviourTypeEnum.STIMULI_BASED_BEHAVIOUR);}

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
    protected Set<TopicToken> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        //
        // 1st, lets do the (macro) Topic (Stimulus) registration process
        //
        getLogger().trace(".specifySubscriptionTopics(): First, get the DigitalTwinStimulusSubscriptionCriteriaInterface list");
        ArrayList<TopicToken> subscribedTopics = new ArrayList<>();
        for(BehaviourCentricInclusionFilterRulesInterface positiveFilterRulesInterface: inclusionFilterSet()){
            for(TopicToken currentToken: positiveFilterRulesInterface.positiveStaticFilterStimulus()){
                getLogger().trace(".specifySubscriptionTopics(): Topic of interest --> {}", currentToken);
                getLogger().trace(".specifySubscriptionTopics(): Now, append the right discriminator so as to get the Topics ONLY from the PubSub service");
                currentToken.addDescriminator("Source", "Ladon.StateSpace.PubSub");
                getLogger().trace(".specifySubscriptionTopics(): Call the addTopicToSubscription method on the corresponding CollectorService");
                subscribedTopics.add(currentToken);
                getLogger().trace(".specifySubscriptionTopics(): Topic added... continue");
            }
        }
        getMyTwinOrchestrationService().requestSubscrption(subscribedTopics);
        //
        // Next: lets inject the BehaviourStimulusSubscription into the appropriate DigitalTwinOrchestrator instance
        //
        for(BehaviourCentricInclusionFilterRulesInterface criteriaInterface: inclusionFilterSet()){
            getLogger().trace(".specifySubscriptionTopics(): Get the BehaviourStimulusRequirementSet for each interface instance");
            BehaviourIdentifier behaviourId = new BehaviourIdentifier(specifyBehaviourName(), specifyBehaviourVersion());
            getMyTwinOrchestrationService().registerBehaviourCentricInclusiveFilterRules(behaviourId, criteriaInterface);
        }
        //
        // Lastly: return an empty set.
        //
        HashSet<TopicToken> myTopics = new HashSet<TopicToken>();
        getLogger().debug(".specifySubscriptionTopics(): Exit");
        return(myTopics);
    }


}
