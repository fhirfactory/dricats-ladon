/*
 * Copyright (c) 2021 ACT Health
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
package net.fhirfactory.pegacorn.ladon.twin.activities.subsystem;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;
import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.HL7V2XTopicFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.communication.extensions.CommunicationPayloadTypeExtensionEnricher;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.communication.factories.CommunicationFactory;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WrapHL7v2xMessageWithinFHIRCommunication {
    private static final Logger LOG = LoggerFactory.getLogger(WrapHL7v2xMessageWithinFHIRCommunication.class);

    private HapiContext hapiContext;
    private IParser fhirParser;
    private boolean initialised;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    @Inject
    private FHIRElementTopicFactory fhirTopicFactory;

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    @Inject
    private CommunicationFactory communicationFactory;

    @Inject
    private CommunicationPayloadTypeExtensionEnricher payloadTypeExtensionEnricher;

    @Inject
    private HL7V2XTopicFactory hl7v2TopicFactory;

    //
    // Constructor
    //

    public WrapHL7v2xMessageWithinFHIRCommunication(){
        hapiContext = new DefaultHapiContext();
        initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        fhirParser = fhirContextUtility.getJsonParser().setPrettyPrint(true);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){return(LOG);}

    //
    // Business Methods
    //

    public UoW wrapHL7v2xMessage(UoW uow, Exchange exchange){
        getLogger().info(".encapsulateMessage(): Entry, uow->{}", uow);
        if(uow == null){
            getLogger().warn(".encapsulateMessage(): Exit, message is null!");
            return(uow);
        }

        getLogger().info(".wrapHL7v2xMessage(): [Get v2.x Message] Start...");
        UoWPayload messagePayload = null;
        if (uow.getProcessingOutcome().equals(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS)) {
            if(uow.hasEgressContent()){
                for(UoWPayload currentPayload: uow.getEgressContent().getPayloadElements()){
                    if(currentPayload.getPayloadManifest().getContentDescriptor() != null) {
                        boolean isHL7v2xDefiner = currentPayload.getPayloadManifest().getContentDescriptor().getDataParcelDefiner().equals(hl7v2TopicFactory.getHl7MessageDefiner());
                        boolean isHL7v2xCategory = currentPayload.getPayloadManifest().getContentDescriptor().getDataParcelCategory().equals(hl7v2TopicFactory.getHl7MessageCategory());
                        if(isHL7v2xCategory && isHL7v2xDefiner){
                            messagePayload = SerializationUtils.clone(currentPayload);
                            break;
                        }
                    }
                }
            }
        }
        getLogger().info(".wrapHL7v2xMessage(): [Get v2.x Message] Finish..., message->{}", messagePayload);

        if(messagePayload != null) {
            try {
                getLogger().info(".wrapHL7v2xMessage(): [Wrap Message within FHIR::Communication] Start");
                String messageAsString = messagePayload.getPayload();
                String messageID = extractMessageID(messageAsString);

                getLogger().trace(".buildDefaultCommunicationMessage(): Add Id value (from the m.room.message::event_id");
                Communication newCommunication = communicationFactory.newCommunicationResource(messageID, null);
                newCommunication.setStatus(Communication.CommunicationStatus.COMPLETED);
                getLogger().trace("buildDefaultCommunicationMessage(): Set the FHIR::Communication.CommunicationPriority to ROUTINE (we make no distinction - all are real-time)");
                newCommunication.setPriority(Communication.CommunicationPriority.ROUTINE);
                //
                // Add payload
                //
                Communication.CommunicationPayloadComponent payload = new Communication.CommunicationPayloadComponent();
                StringType messageStringType = new StringType(messageAsString);
                payload.setContent(messageStringType);
                payloadTypeExtensionEnricher.injectPayloadTypeExtension(payload, messagePayload.getPayloadManifest().getContentDescriptor());
                newCommunication.getPayload().add(payload);
                String communicationAsString = fhirParser.encodeResourceToString(newCommunication);
                DataParcelTypeDescriptor parcelContainerDescriptor = fhirTopicFactory.newTopicToken(ResourceType.Communication.name(), pegacornReferenceProperties.getPegacornDefaultFHIRVersion());
                messagePayload.getPayloadManifest().setContainerDescriptor(parcelContainerDescriptor);
                messagePayload.getPayloadManifest().setInterSubsystemDistributable(true);
                messagePayload.setPayload(communicationAsString);
                uow.getEgressContent().addPayloadElement(messagePayload);
                uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
                getLogger().info(".wrapHL7v2xMessage(): [Wrap Message within FHIR::Communication] Finish");
            } catch (Exception ex){
                uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
                uow.setFailureDescription(ExceptionUtils.getMessage(ex));
                getLogger().warn(".wrapHL7v2xMessage(): [Wrap Message within FHIR::Communication] failed, error->{}", ExceptionUtils.getMessage(ex));
            }
        } else {
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
            uow.setFailureDescription("No HL7v2 Message to Process!");
            getLogger().warn(".wrapHL7v2xMessage(): No HL7v2 Message to Process!");

        }
        getLogger().info(".encapsulateMessage(): Exit, uow->{}", uow);
        return (uow);
    }

    public String extractMessageID(Message message) {
        Terser terser = new Terser(message);

        LOG.debug(".extractMessageID(): Entry, messageAsText->{}", message);
        if(message != null) {
            try {
                return terser.get("MSH-10");
            } catch (HL7Exception e) {
                LOG.warn(".extractMessageID(): Cannot extract MessageControlID, error -> {}", e.getMessage());
                return (null);
            }
        } else {
            return(null);
        }
    }

    public String extractMessageID(String messageAsText) {
        LOG.debug(".extractMessageID(): Entry, messageAsText->{}", messageAsText);
        Message message = convertToHL7v2Message(messageAsText);
        String messageID = extractMessageID(message);
        return(messageID);
    }

    public Message convertToHL7v2Message(String messageText){
        LOG.debug(".convertToHL7v2Message(): Entry, messageText->{}", messageText);
        Parser parser = hapiContext.getPipeParser();
        parser.getParserConfiguration().setValidating(false);
        parser.getParserConfiguration().setEncodeEmptyMandatoryFirstSegments(true);
        try {
            Message hl7Msg = parser.parse(messageText);
            return(hl7Msg);
        } catch (HL7Exception e) {
            LOG.warn(".convertToHL7v2Message(): Cannot parse HL7 Message, error -> {}", e.getMessage());
            return(null);
        }
    }
}
