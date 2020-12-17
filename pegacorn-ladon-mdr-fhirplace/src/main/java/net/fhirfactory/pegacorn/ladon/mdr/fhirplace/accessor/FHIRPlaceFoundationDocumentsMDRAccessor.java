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
package net.fhirfactory.pegacorn.ladon.mdr.fhirplace.accessor;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.deployment.names.PegacornFHIRPlaceMDRComponentNames;
import net.fhirfactory.pegacorn.platform.restfulapi.PegacornInternalFHIRClientServices;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class FHIRPlaceFoundationDocumentsMDRAccessor extends PegacornInternalFHIRClientServices {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRPlaceFoundationDocumentsMDRAccessor.class);

    public FHIRPlaceFoundationDocumentsMDRAccessor(){
        super();
    }

    @Override
    protected Logger getLogger(){return(LOG);}

    @Inject
    private PegacornFHIRPlaceMDRComponentNames pegacornMDRComponentNames;

    @Override
    protected String specifyFHIRServerService() {
        return (pegacornMDRComponentNames.getFoundationDocumentsPegacornMDRService());
    }

    @Override
    protected String specifyFHIRServerProcessingPlant() {
        return (pegacornMDRComponentNames.getFoundationDocumentsPegacornMDRProcessingPlant());
    }

    @Override
    protected String specifyFHIRServerSubsystemName() {
        return (pegacornMDRComponentNames.getFoundationDocumentsPegacornMDRSubsystem());
    }

    @Override
    protected String specifyFHIRServerSubsystemVersion() {
        return (pegacornMDRComponentNames.getFoundationDocumentsPegacornMDRSubsystemVersion());
    }

    @Override
    protected String specifyFHIRServerServerEndpointName() {
        return (pegacornMDRComponentNames.getFoundationDocumentsPegacornMDREndpointFhirApi());
    }

    @Override
    public Resource findResourceByIdentifier(String resourceType, String identifierSystem, String identifierCode, String identifierValue){
        getLogger().info(".findResourceByIdentifier(): Entry, resourceType --> {}, identfierSystem --> {}, identifierCode --> {}, identifierValue -->{}", resourceType, identifierSystem, identifierCode, identifierValue);
        String urlEncodedString = null;
        String rawSearchString = identifierSystem + "|" + identifierValue;
        urlEncodedString = "identifier=" + URLEncoder.encode(rawSearchString, StandardCharsets.UTF_8);
        String searchURL = resourceType + "?" + urlEncodedString;
        getLogger().info(".findResourceByIdentifier(): URL --> {}", searchURL);
        Bundle response = getClient().search()
                .byUrl(searchURL)
                .returnBundle(Bundle.class)
                .execute();
        IParser r4Parser = getFHIRContextUtility().getJsonParser().setPrettyPrint(true);
        if(getLogger().isInfoEnabled()) {
            if(response != null) {
                getLogger().info(".findResourceByIdentifier(): Retrieved Bundle --> {}", r4Parser.encodeResourceToString(response));
            }
        }
        Resource resource = getBundleContentHelper().extractFirstRepOfType(response, resourceType);
        getLogger().info(".findResourceByIdentifier(): Retrieved Resource --> {}", resource);
        return (resource);
    }
}
