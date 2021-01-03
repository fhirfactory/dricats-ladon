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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.fhirfactory.pegacorn.fhir.helpers.BundleDecoder;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusReason;
import net.fhirfactory.pegacorn.ladon.model.stimuli.StimulusReasonTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWIdentifier;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.datasets.fhir.r4.internal.topics.FHIRElementTypeExtractor;
import net.fhirfactory.pegacorn.ladon.model.stimuli.Stimulus;
import net.fhirfactory.pegacorn.petasos.model.topics.TopicToken;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;

/**
 *
 */
@ApplicationScoped
public class UoW2StimulusListBean {
    private static final Logger LOG = LoggerFactory.getLogger(UoW2StimulusListBean.class );

    @Inject
    private FHIRElementTypeExtractor fhirElementTypeFromTopic;

    @Inject
	private FHIRContextUtility fhirContextUtility;

    @Inject
	private BundleDecoder bundleDecoder;

    public List<Stimulus> convertUoWContent2StimulusList(UoW incomingUoW){
        LOG.debug(".convertUoWContent2StimulusList(): Entry, incomingUoW (UoW) --> {}", incomingUoW);
        List<Stimulus> stimulusList = new ArrayList<Stimulus>();
    	IParser fhirResourceParser = fhirContextUtility.getJsonParser().setPrettyPrint(true);
    	@SuppressWarnings("rawtypes")
		Class resourceType = null;
    	try{
    		resourceType = getResourceTypeFromTopicToken(incomingUoW.getIngresContent().getPayloadTopicID());
		} catch(ClassNotFoundException noClassEx) {
			return(stimulusList);
		}
    	Resource fhirResource;
    	try {
    		fhirResource = (Resource)fhirResourceParser.parseResource(resourceType, incomingUoW.getIngresContent().getPayload());
    	} catch(Exception swallowEx) {
    		LOG.error(".assembleStimuliResources(): Error in reconstituting FHIR Resource --> {}", swallowEx);
    		return(stimulusList);
    	}
    	switch(fhirResource.getResourceType()) {
    		case Bundle:{
    			Bundle bundleResource = (Bundle)fhirResource;
    			stimulusList.addAll(deriveStimulusFromBundle(bundleResource,incomingUoW.getInstanceID()));
    			break;
    		}
    		default:{
    			Stimulus otherFHIRResourceStimulus = new Stimulus(incomingUoW.getInstanceID(), fhirResource);
    			stimulusList.add(otherFHIRResourceStimulus);
    		}
    	}
   		return(stimulusList);
    }
    
    @SuppressWarnings("rawtypes")
	private Class getResourceTypeFromTopicToken(TopicToken topicId) throws ClassNotFoundException {
    	try {
    		Class resourceClass = fhirElementTypeFromTopic.extractResourceType(topicId);
    		return(resourceClass);
    	} catch(ClassNotFoundException noClassEx) {
    		LOG.error(".getResourceTypeFromTopicToken(): Error in deriving FHIR Resource type from Topic ", noClassEx);
    		throw(noClassEx);
    	}
    }

    private List<Stimulus> deriveStimulusFromBundle(Bundle bundleResource, UoWIdentifier uowId){
    	ArrayList<Stimulus> stimulusList = new ArrayList<Stimulus>();
    	StimulusReason reason = new StimulusReason(StimulusReasonTypeEnum.STIMULUS_REASON_BUNDLE, bundleResource);
    	switch(bundleResource.getType()){
			case MESSAGE:{
				MessageHeader msgHeader = bundleDecoder.extractMessageHeader(bundleResource);
				if(msgHeader == null){
					return(stimulusList);
				}
				reason.setOriginalSource(msgHeader.getSource().getSoftware());
				// TODO need to investigate the use of getSource, getTarget, getRecipient & getEndpoint on destinationComponent
				MessageHeader.MessageDestinationComponent destinationComponent = msgHeader.getDestinationFirstRep();
				if(destinationComponent != null) {
					reason.setOriginalDestination(destinationComponent.getTarget().getDisplay());
				}
			}
			default:
				break;
		}
		for(Bundle.BundleEntryComponent entry: bundleResource.getEntry()){
			Stimulus entryAsStimulus = new Stimulus(uowId, entry.getResource());
			entryAsStimulus.setReason(reason);
			stimulusList.add(entryAsStimulus);
		}
    	return(stimulusList);
	}
}
