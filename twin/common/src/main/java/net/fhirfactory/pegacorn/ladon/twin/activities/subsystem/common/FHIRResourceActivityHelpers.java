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
package net.fhirfactory.pegacorn.ladon.twin.activities.subsystem.common;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import net.fhirfactory.dricats.model.petasos.uow.UoWPayload;
import net.fhirfactory.dricats.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.dricats.internals.hl7.v2x.PegacornInternalHL7MessageSimpleUtils;
import net.fhirfactory.dricats.internals.hl7.v2x.PegacornInternalHL7TerserBasedUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FHIRResourceActivityHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRResourceActivityHelpers.class);


    @Inject
    private PegacornInternalHL7TerserBasedUtils internalHL7TerserBasedUtils;

    @Inject
    private PegacornInternalHL7MessageSimpleUtils internalHL7MessageSimpleUtils;


    //
    // Business Methods
    //

    /**
     * This method finds the UoWPayload that contains the original HL7 v2 message. Because IIE is providing us ONLY with
     * ADT messages, we only need to check for this within the DataParcelManifest.
     *
     * @param egressPayload
     * @return
     */
    protected String extractHL7v2Message(UoWPayloadSet egressPayload){
        if(egressPayload.getPayloadElements().isEmpty()){
            return(null);
        }
        for(UoWPayload currentPayload: egressPayload.getPayloadElements()){
            if(currentPayload.getPayloadManifest().getContentDescriptor().getDataParcelSubCategory().contentEquals("ADT")){
                return(currentPayload.getPayload());
            }
        }
        return(null);
    }

    protected Identifier buildMRNIdentifier(String messageAsString){
        getLogger().info(".addSystemAndCodeToIdentifier(): Entry, messageAsString->{}",  messageAsString);
        if(StringUtils.isEmpty(messageAsString)){
            return(null);
        }
        Message message = internalHL7MessageSimpleUtils.getMessage(messageAsString);
        getLogger().info(".addSystemAndCodeToIdentifier(): Converted messageAsString to HL7 Message");
        Identifier mrIdentifier = null;
        if(message != null) {
            getLogger().info(".addSystemAndCodeToIdentifier(): Message is Not NULL --> So we can process");
            String identifierSystem = null;
            String identifierCode = null;
            String identifierDisplay = null;
            String identifierValue = null;
            String identifierTypeSystem = "http://terminology.hl7.org/CodeSystem/v2-0203";
            try {
                Terser terser = new Terser(message);
                identifierCode = terser.get("PID-2-5");
                identifierValue = terser.get("PID-2-1");
                if(StringUtils.isNotEmpty(identifierCode)){
                    if(identifierCode.equalsIgnoreCase("MR")){
                        identifierSystem = "ACTPAS";
                        identifierDisplay = "Medical Record Number";
                    }
                }
            } catch(Exception ex){
                getLogger().warn(".addSystemAndCodeToIdentifier(): Could not parse HL7 v2 message, error->{}", ExceptionUtils.getMessage(ex));
            }
            if(StringUtils.isNotEmpty(identifierCode)){
                mrIdentifier.setSystem(identifierSystem);
                CodeableConcept newCC = new CodeableConcept();
                Coding newCoding = new Coding();
                newCoding.setCode(identifierCode);
                newCoding.setSystem(identifierTypeSystem);
                newCoding.setDisplay(identifierDisplay);
                newCC.addCoding(newCoding);
                newCC.setText("MR: Medical Record Number");
                mrIdentifier.setType(newCC);
                mrIdentifier.setValue(identifierValue);
            }
        }
        return(mrIdentifier);
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
