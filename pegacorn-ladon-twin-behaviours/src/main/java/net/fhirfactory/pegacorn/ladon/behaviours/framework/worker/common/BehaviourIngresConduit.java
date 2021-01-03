/*
 * Copyright (c) 2020 MAHun
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
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusPackage;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * @author Mark A. Hunter
 * @since 2020-07-05
 */
@Dependent
public class BehaviourIngresConduit {

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;

    private static final Logger LOG = LoggerFactory.getLogger(BehaviourIngresConduit.class);
    /**
     * This function does the pre-Behaviour-Stimulus-Processing processing.
     *
     * @param incomingStimulusPackage   The incoming Stimulus (Stimumus) received as output from the actual Work Unit Processor (Business Logic)
     * @param camelExchange The Apache Camel Exchange object, for extracting the WUPJobCard & ParcelStatusElement from
     * @param behaviourName The Behaviour name - an absolutely unique name for the instance of the Behaviour within the entiry deployment.
     * @return A Stimulus object for injecting into other Behaviours
     */
    public StimulusPackage injectStimulus(StimulusPackage incomingStimulusPackage, Exchange camelExchange, String behaviourName){
        LOG.debug(".injectStimulus(): Entry, incomingStimulus (Stimulus) --> {}, behaviourName (String) --> {}", incomingStimulusPackage, behaviourName);
        BehaviourRouteNames frameworkRouteEndpointNames = new BehaviourRouteNames(behaviourName);
        //
        // Place any Behaviour Ingres processing here... including AuditTrail Entry if required.
        // For initial release, do nothing.
        //
        LOG.debug(".injectStimulus(): Exit, returning (Stimulus) --> {}", incomingStimulusPackage);
        return(incomingStimulusPackage);
    }
}
