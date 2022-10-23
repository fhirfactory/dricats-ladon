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

import io.github.linuxforhealth.hl7.ConverterOptions;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import net.fhirfactory.dricats.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelFragmentQualityStatement;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelQualityStatement;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelFragmentQualityEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.dricats.model.petasos.uow.UoW;
import net.fhirfactory.dricats.model.petasos.uow.UoWPayload;
import net.fhirfactory.dricats.model.petasos.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.dricats.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.ladon.twin.activities.subsystem.common.FHIRResourceActivityBase;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Set;

/**
 *
 */
@Dependent
public class TransformHL7v2xToFHIR extends FHIRResourceActivityBase {
    private static final Logger LOG = LoggerFactory.getLogger(TransformHL7v2xToFHIR.class);

    HL7ToFHIRConverter ibmFHIRConverter;
    ConverterOptions ibmFHIRConverterOptions;

    @Inject
    private FHIRElementTopicFactory fhirTopicFactory;

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;



    //
    // Constructor(s)
    //

    public TransformHL7v2xToFHIR(){
        this.ibmFHIRConverter = new HL7ToFHIRConverter();
        this.ibmFHIRConverterOptions = new ConverterOptions.Builder().withPrettyPrint().build();
    }

    //
    // Post Construct
    //

    //
    // Business Method
    //

    /**
     * This method takes in a UoW, extracts the first entry of the egressPayload, assumes it is a HL7 2.x message, pushes
     * the message through a HL7v2.to.FHIR conversion function, wraps the output (which should be a JSON string
     * being an encoded FHIR::Bundle) into a UoWPayload, adds a DataParcelManifest to describe the FHIR::Bundle
     * payload, adds that UoWPayload to the egressContent of the incoming UoW, sets the outcomeStatus of the UoW
     * to SUCCESS if all went well and then returns that UoW.
     *
     * It then takes the FHIR::Bundle, iterates through each Resource contained there-in - and adds a UoWPayload
     * into the UoW for each resource.
     *
     * If things go wrong, then the outcomeStatus is set to FAILED and the failureDescription is updated to
     * reflect the failure message.
     *
     * @param uow A UoW with the "first" entry of the egressContent containing a HL7 v2x Message
     * @param camelExchange
     * @return A UoW with the egressContent containing a JSON String encapsulating a FHIR::Bundle or resources
     */
    public UoW convertHL7V2xMessage(UoW uow, Exchange camelExchange){
        getLogger().info(".convertHL7V2xMessage(): Entry, uow->{}", uow);
        UoW bundleAddedUoW = fromHL7v2MessageToFHIRBundle(uow);
        UoW allResourcesAddUoW = fromBundleToIndividualResources(bundleAddedUoW);
        getLogger().info(".convertHL7V2xMessage(): Exit, uow->{}", allResourcesAddUoW);
        return(allResourcesAddUoW);
    }

    protected UoW fromBundleToIndividualResources(UoW uow){
        getLogger().debug(".fromBundleToIndividualResources(): Entry, uow->{}", uow);

        //
        // Convert String to actual FHIR Bundle
        Bundle bundle = null;
        UoWPayload bundleUoWPayload = null;
        if(uow.getProcessingOutcome().equals(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS)){
            Set<UoWPayload> outputSet = uow.getEgressContent().getPayloadElements();
            String outputPayload = null;
            for(UoWPayload currentPayload: outputSet){
                if(isFHIRBundle(currentPayload)) {
                    outputPayload = currentPayload.getPayload();
                    bundleUoWPayload = currentPayload;
                    break;
                }
            }
            if(StringUtils.isNotEmpty(outputPayload)) {
                try {
                    bundle = getJsonFHIRParser().parseResource(Bundle.class, outputPayload);
                } catch (Exception ex){
                    getLogger().warn(".updatePersistedResources(): Could not parse JSON Resource Bundle");
                }
            }
        }

        boolean bundleIsProcessed = false;
        String errorMessage = null;

        if(bundle != null){
            if(bundle.hasEntry()) {
                for (Bundle.BundleEntryComponent currentEntry : bundle.getEntry()) {
                    if(currentEntry.hasResource()) {
                        try {
                            Resource currentResource = currentEntry.getResource();
                            DataParcelTypeDescriptor contentDescriptor = fhirTopicFactory.newTopicToken(currentResource.getResourceType().name(), currentResource.getStructureFhirVersionEnum().getFhirVersionString());
                            DataParcelManifest currentResourceManifest = SerializationUtils.clone(bundleUoWPayload.getPayloadManifest());
                            currentResourceManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_WORKFLOW_INPUT);
                            currentResourceManifest.setContentDescriptor(contentDescriptor);
                            currentResourceManifest.setContainerDescriptor(null);
                            String resourceAsString = getJsonFHIRParser().encodeResourceToString(currentResource);
                            UoWPayload resourceUoWPayload = new UoWPayload();
                            resourceUoWPayload.setPayload(resourceAsString);
                            resourceUoWPayload.setPayloadManifest(currentResourceManifest);
                            uow.getEgressContent().addPayloadElement(resourceUoWPayload);
                            bundleIsProcessed = true;
                        } catch (Exception ex){
                            getLogger().warn(".fromBundleToIndividualResources(): Could not add Resource->{} to UoW, error->{}", currentEntry.getResource(), ExceptionUtils.getMessage(ex));
                            bundleIsProcessed = false;
                            errorMessage = ExceptionUtils.getMessage(ex);
                            break;
                        }
                    }
                }
            }
        }

        if(bundleIsProcessed){
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        } else {
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
            if(StringUtils.isNotEmpty(errorMessage)) {
                uow.setFailureDescription(errorMessage);
            } else {
                uow.setFailureDescription("Unable to convert whole FHIR::Bundle to individual Resources");
            }
        }

        getLogger().debug(".fromBundleToIndividualResources(): Exit, uow->{}", uow);
        return(uow);
    }



    protected UoW fromHL7v2MessageToFHIRBundle(UoW uow){
        getLogger().debug(".fromHL7v2MessageToFHIRBundle(): Entry, uow->{}", uow);

        String hl7Message = null;

        if(uow.hasEgressContent()){
            UoWPayload firstEgressPayload = null;
            for(UoWPayload currentPayload: uow.getEgressContent().getPayloadElements()){
                firstEgressPayload = currentPayload;
                break;
            }
            if(firstEgressPayload != null) {
                if (StringUtils.isNotEmpty(firstEgressPayload.getPayload())) {
                    hl7Message = firstEgressPayload.getPayload();
                }
            }
        }

        getLogger().info(".fromHL7v2MessageToFHIRBundle(): Extracted ingresContent->{}", hl7Message);

        if(StringUtils.isNotEmpty(hl7Message)) {
            getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Start...");
            try {
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Extracted hl7Message->{}", hl7Message);
                String resourceBundle = getIBMFHIRConverter().convert(hl7Message, getIBMFHIRConverterOptions());
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Converted to FHIR resourceBundle->{}", resourceBundle);
                UoWPayload outputPayload = new UoWPayload();
                outputPayload.setPayload(resourceBundle);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Added content to new (Egress) UoWPayload");
                DataParcelManifest outputManifest = new DataParcelManifest();
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Creating DataParcelManifest");
                DataParcelTypeDescriptor bundleDescriptor = fhirTopicFactory.newTopicToken("Bundle", pegacornReferenceProperties.getPegacornDefaultFHIRVersion());
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Creating contentDescriptor->{}", bundleDescriptor);
                outputManifest.setContentDescriptor(bundleDescriptor);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Setting Associated DataParcelManifest values");
                outputManifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
                outputManifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
                outputManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_WORKFLOW_TRANSIENT);
                outputManifest.setInterSubsystemDistributable(false);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Adding Manifest to (Egress) UoWPayload");
                outputPayload.setPayloadManifest(outputManifest);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Updating uow with new (Egress) payload");
                uow.getEgressContent().addPayloadElement(outputPayload);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Updating uow with -SUCCESS- outcome");
                uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
                getLogger().info(".fromHL7v2MessageToFHIRBundle(): [HL7v2 --> FHIR] Finished...");
            } catch (Exception ex) {
                uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
                uow.setFailureDescription(ExceptionUtils.getMessage(ex));
                getLogger().warn(".fromHL7v2MessageToFHIRBundle(): Could not process HL7 message, error message->{}", ExceptionUtils.getMessage(ex));
            }
        } else {
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
            uow.setFailureDescription("First Egress Content is Empty");
        }

        getLogger().debug(".fromHL7v2MessageToFHIRBundle(): Exit, uow->{}", uow);
        return(uow);
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    protected HL7ToFHIRConverter getIBMFHIRConverter(){
        return(this.ibmFHIRConverter);
    }

    protected ConverterOptions getIBMFHIRConverterOptions(){
        return(this.ibmFHIRConverterOptions);
    }

}
