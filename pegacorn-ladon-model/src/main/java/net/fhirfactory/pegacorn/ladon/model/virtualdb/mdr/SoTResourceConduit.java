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
package net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.common.model.FDN;
import net.fhirfactory.pegacorn.common.model.RDN;
import net.fhirfactory.pegacorn.datasets.fhir.r4.base.entities.endpoint.EndpointIdentifierBuilder;
import net.fhirfactory.pegacorn.datasets.fhir.r4.base.entities.organization.OrganizationIdentifierBuilder;
import net.fhirfactory.pegacorn.datasets.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.datasets.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class SoTResourceConduit implements SoTConduitInterface {
    private ResourceType resourceType;
    private SoTIdentifier sotIdentifier;
    private CodeableConcept sotRecordIDCode;
    private Reference sourceOfTruthEndpoint;
    private Reference sourceOfTruthOwningOrganization;

    @Inject
    private EndpointIdentifierBuilder endpointIdentifierBuilder;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private OrganizationIdentifierBuilder organizationIdentifierBuilder;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    abstract protected void registerWithSoTCConduitController();
    abstract protected Logger getLogger();
    abstract protected Identifier getBestIdentifier(MethodOutcome outcome);
    abstract protected void doSubclassInitialisations();

    abstract protected String specifySourceOfTruthEndpointSystemName();
    abstract protected String specifySourceOfTruthOwningOrganization();

    public FHIRContextUtility getFHIRContextUtility(){ return(fhirContextUtility);}
    public SoTIdentifier getSotIdentifier() {
        return sotIdentifier;
    }

    public void setSotIdentifier(SoTIdentifier sotIdentifier) {
        this.sotIdentifier = sotIdentifier;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    protected String getSourceOfTruthEndpointName(){return(specifySourceOfTruthEndpointSystemName());}

    public Reference getSourceOfTruthEndpoint() {
        return (this.sourceOfTruthEndpoint);
    }

    public Reference getSourceOfTruthOwningOrganization() {
        return (this.sourceOfTruthOwningOrganization);
    }

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        registerWithSoTCConduitController();
        doSubclassInitialisations();
        this.sourceOfTruthEndpoint = buildEndpointSystemReference();
        this.sourceOfTruthOwningOrganization = buildOrganizationReference();
        buildSoTIdentifierDetail();
        getLogger().debug(".initialise(): Exit");
    }

    private void buildSoTIdentifierDetail(){
        FDN localFDN = new FDN();
        localFDN.appendRDN (new RDN("SoTConduit", getConduitName()));
        this.sotIdentifier = new SoTIdentifier(localFDN.getToken());
        this.sotRecordIDCode = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM);
    }

    private Reference buildEndpointSystemReference(){
        Identifier identifier = endpointIdentifierBuilder.constructEndpointIdentifier(specifySourceOfTruthEndpointSystemName());
        Reference reference = new Reference();
        reference.setIdentifier(identifier);
        reference.setType(ResourceType.Endpoint.toString());
        reference.setDisplay(specifySourceOfTruthEndpointSystemName());
        return(reference);
    }

    private Reference buildOrganizationReference(){
        Identifier identifier = organizationIdentifierBuilder.constructOrganizationIdentifier(specifySourceOfTruthOwningOrganization());
        Reference reference = new Reference();
        reference.setIdentifier(identifier);
        reference.setType(ResourceType.Organization.toString());
        reference.setDisplay(specifySourceOfTruthOwningOrganization());
        return(reference);
    }
}
