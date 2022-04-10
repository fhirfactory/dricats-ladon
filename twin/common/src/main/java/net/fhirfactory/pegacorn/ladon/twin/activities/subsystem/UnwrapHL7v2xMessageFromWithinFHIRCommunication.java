/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.Communication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class UnwrapHL7v2xMessageFromWithinFHIRCommunication {
    private static final Logger LOG = LoggerFactory.getLogger(UnwrapHL7v2xMessageFromWithinFHIRCommunication.class);

    private IParser fhirResourceParser;
    private boolean initialised;

    protected Logger getLogger(){return(LOG);}

    @Inject
    protected FHIRContextUtility fhirContextUtility;

    //
    // Constructor
    //

    public UnwrapHL7v2xMessageFromWithinFHIRCommunication(){
        initialised = false;
    }

    //
    // Post Construct
    //
    
    @PostConstruct
    public void initialise(){
        if(!initialised) {
            fhirResourceParser = fhirContextUtility.getJsonParser().setPrettyPrint(true);
            initialised = true;
        }
    }

    //
    // Business Methods
    //

    /**
     *
     * @param uow
     * @return
     */
    public UoW extractHL7MessageFromCommunicationPayload(UoW uow) {
        getLogger().debug(".extractHL7MessageFromCommunicationPayload(): Entry, uow->{}", uow);

        try {

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Extracting payload from uow (UoW)");
            String communicationAsString = uow.getIngresContent().getPayload();

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Converting into (FHIR::Communication) from JSON String");
            Communication communication = fhirResourceParser.parseResource(Communication.class, communicationAsString);

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Pull the HL7v2x Message (as Text) from the Communication Payload");
            Communication.CommunicationPayloadComponent communicationPayload = communication.getPayloadFirstRep();
            String contentMessage = communicationPayload.getContentStringType().getValue();

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Clone the content for injection into the UoW egress payload");
            String clonedMessage = SerializationUtils.clone(contentMessage);

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Create the egress payload (UoWPayload) to contain the message");
            UoWPayload newPayload = new UoWPayload();

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Clone the manifest (DataParcelManifest) of the incoming payload");
            DataParcelManifest newManifest = SerializationUtils.clone(uow.getIngresContent().getPayloadManifest());

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Now, set the containerDescriptor to null, as we've removed payload from the Communication resource");
            newManifest.setContainerDescriptor(null);
            newManifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Populate the new Egress payload object");
            newPayload.setPayload(clonedMessage);
            newPayload.setPayloadManifest(newManifest);

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Add the new Egress payload to the UoW");
            uow.getEgressContent().addPayloadElement(newPayload);

            getLogger().trace(".extractHL7MessageFromCommunicationPayload(): Assign the processing outcome to the UoW");
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);

        } catch(Exception exception){
            String errorMessage =  ExceptionUtils.getMessage(exception);
            getLogger().warn(".extractHL7MessageFromCommunicationPayload(): Unable to extract message for FHIR::Communication resource, message->{}", errorMessage);
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
            uow.setFailureDescription(errorMessage);
        }

        getLogger().info(".extractMessage(): Exit, uow->{}", uow);
        return (uow);
    }    
}