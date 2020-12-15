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

package net.fhirfactory.pegacorn.ladon.behaviours.framework.worker;

import net.fhirfactory.pegacorn.ladon.behaviours.framework.model.BehaviourRouteNames;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.ladon.statespace.stimuli.model.StimulusPackage;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Mark A. Hunter
 * @since 2020-09-20
 */
@Dependent
public class BehaviourEgressConduit {
    private static final Logger LOG = LoggerFactory.getLogger(BehaviourEgressConduit.class);
    
    @Inject
    LadonProcessingPlant ladonServices;

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;
    
    /**
     * This function does the post-Behaviour-Stimulus-Processing processing.
     *
     * @param incomingStimulusPackage   The incoming Stimulus (Stimumus) received as output from the actual Work Unit Processor (Business Logic)
     * @param camelExchange The Apache Camel Exchange object, for extracting the WUPJobCard & ParcelStatusElement from
     * @param behaviourName The Behaviour name - an absolutely unique name for the instance of the Behaviour within the entiry deployment.
     * @return A Stimulus object for injecting into other Behaviours
     */
    public StimulusPackage extractStimulus(StimulusPackage incomingStimulusPackage, Exchange camelExchange, String behaviourName) {
        LOG.debug(".extractStimulus(): Entry, incomingStimulus (Stimulus) --> {}, behaviourName (String) --> {}", incomingStimulusPackage, behaviourName);
        // Get my Petasos Context
        if( ladonServices == null ) {
        	LOG.error(".extractStimulus(): Guru Software Meditation Error: ladonServices is null");
        }
        BehaviourRouteNames frameworkRouteEndpointNames = new BehaviourRouteNames(behaviourName);
        //
        // Place any Behaviour Egress processing here... including AuditTrail Entry if required.
        // For initial release, do nothing.
        //
        return (incomingStimulusPackage);
    }
}
