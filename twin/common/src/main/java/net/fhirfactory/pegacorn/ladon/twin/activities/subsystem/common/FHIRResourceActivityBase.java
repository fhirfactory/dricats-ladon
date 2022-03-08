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

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class FHIRResourceActivityBase {

    private boolean initialised;
    private IParser jsonFHIRParser;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    //
    // Constructor(s)
    //

    public FHIRResourceActivityBase(){
        this.initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Already initialised, nothing to do");
            // do nothing
        } else {
            getLogger().info(".initialise(): Initialisation Start");
            jsonFHIRParser = fhirContextUtility.getJsonParser();
            this.initialised = true;
            getLogger().info(".initialise(): Initialisation Finish");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Abstract Methods
    //

    abstract protected Logger getLogger();

    //
    // Business Methods
    //

    protected boolean isFHIRBundle(UoWPayload payload){
        boolean isBundleResource = isFHIRResource(ResourceType.Bundle, payload);
        return(isBundleResource);
    }

    protected boolean isFHIRResource(ResourceType resourceType, UoWPayload payload){
        if(payload == null){
            return(false);
        }
        if(StringUtils.isEmpty(payload.getPayload())){
            return(false);
        }
        DataParcelManifest payloadManifest = payload.getPayloadManifest();
        if(payloadManifest == null){
            return(false);
        }
        DataParcelTypeDescriptor payloadDescriptor = payloadManifest.getContentDescriptor();
        if(payloadDescriptor == null){
            return(false);
        }
        String resourceTypeName = payloadDescriptor.getDataParcelResource();
        if(StringUtils.isNotEmpty(resourceTypeName)){
            if(resourceTypeName.contentEquals(resourceType.name())){
                return(true);
            }
        }
        return(false);
    }

    //
    // Getters and Setters
    //

    protected IParser getJsonFHIRParser(){
        return(this.jsonFHIRParser);
    }
}
