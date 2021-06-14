package net.fhirfactory.pegacorn.ladon.mdr.conduit.controller.aggregationservices.common;


import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.common.SourceOfTruthRIDIdentifierBuilder;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierDataTypeHelpers;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitActionResponse;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitSearchResponseElement;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

public abstract class ResourceContentAggregationServiceBase {

    protected abstract Logger getLogger();
    protected abstract String getAggregationServiceName();
    protected abstract Identifier getBestIdentifier(Resource resource);
    protected abstract void addIdentifier(Resource resource, Identifier ridIdentifier);
    protected abstract List<Identifier> getIdentifiers(ResourceSoTConduitActionResponse actionResponse);
    protected abstract ResourceType getResourceType();

    @Inject
    private PegacornIdentifierDataTypeHelpers VirtualDBKeyHelpers;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private SourceOfTruthRIDIdentifierBuilder sourceOfTruthRIDIdentifierBuilder;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    protected FHIRContextUtility getFhirContextUtility(){
        return(fhirContextUtility);
    }

    protected PegacornIdentifierDataTypeHelpers getIdentifierPicker(){return(VirtualDBKeyHelpers);}

    public PegacornIdentifierCodeSystemFactory getPegacornIdentifierCodeSystemFactory() {
        return pegacornIdentifierCodeSystemFactory;
    }

    public SourceOfTruthRIDIdentifierBuilder getSourceOfTruthRIDIdentifierBuilder() {
        return sourceOfTruthRIDIdentifierBuilder;
    }

    //
    // Create Aggregation Methods
    //
    public abstract TransactionMethodOutcome aggregateCreateResponseSet(List<ResourceSoTConduitActionResponse> responseSet);
    //
    // Review / Get Aggregation Methods
    //
    public abstract TransactionMethodOutcome aggregateGetResponseSet(List<ResourceSoTConduitActionResponse> responseSet);
    //
    // Update Aggregation Methods
    //
    public abstract TransactionMethodOutcome aggregateUpdateResponseSet(List<ResourceSoTConduitActionResponse> responseSet);
    //
    // Delete Aggregation Methods
    //
    public abstract TransactionMethodOutcome aggregateDeleteResponseSet(List<ResourceSoTConduitActionResponse> responseSet);
    //
    // Search Result Aggregation
    //
    public abstract TransactionMethodOutcome aggregateSearchResultSet(List<ResourceSoTConduitSearchResponseElement> responseSet);

    protected void mapIdToIdentifier(ResourceSoTConduitActionResponse actionResponse){
        List<Identifier> identifierList = getIdentifiers(actionResponse);
        if(identifierList.isEmpty()){
            return;
        }
        CodeableConcept sotRIDCode = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_SOURCE_OF_TRUTH_RECORD_ID);
        for(Identifier currentIdentifier: identifierList){
            if(currentIdentifier.getType().equalsDeep(sotRIDCode)){
                return;
            }
        }
        Resource responseResource = (Resource) actionResponse.getResource();
        if(!responseResource.hasId()){
            return;
        }
        Identifier sotRIDIdentifier = sourceOfTruthRIDIdentifierBuilder.constructRIDIdentifier(actionResponse.getSourceOfTruthEndpoint().getIdentifier().getValue(),responseResource.getId());
        addIdentifier(responseResource, sotRIDIdentifier);
    }
 }
