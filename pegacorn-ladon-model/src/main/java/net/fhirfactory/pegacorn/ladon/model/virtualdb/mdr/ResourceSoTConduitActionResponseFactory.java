package net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.PegacornFunctionStatusEnum;

@ApplicationScoped
public class ResourceSoTConduitActionResponseFactory {

    @Inject
    TransactionMethodOutcomeFactory outcomeFactory;

    @Deprecated
    public ResourceSoTConduitActionResponse createResourceConduitActionResponse(Reference originatingSystem, Reference originatingOrganization, IdType resourceId, TransactionStatusEnum status, String activityLocation){
        ResourceSoTConduitActionResponse sotConduitOutcome = new ResourceSoTConduitActionResponse(originatingSystem, originatingOrganization);
        outcomeFactory.populateResourceActivityOutcome(sotConduitOutcome, resourceId,  status, activityLocation);
        switch(status){
            case REVIEW_FAILURE:
                sotConduitOutcome.setResponseResourceGrade(ResourceGradeEnum.NO_RESOURCE);
                break;
            case REVIEW_FINISH:
                sotConduitOutcome.setResponseResourceGrade(ResourceGradeEnum.ESTABLISHED);
                break;
            default:
                sotConduitOutcome.setResponseResourceGrade(ResourceGradeEnum.LIMITED);
        }
        return(sotConduitOutcome);
    }

    public ResourceSoTConduitActionResponse createResourceConduitActionResponse(String sotName, PegacornFunctionStatusEnum functionStatusEnum, Resource resource, IdType resourceId, TransactionStatusEnum status, String activityLocation){
        ResourceSoTConduitActionResponse sotConduitOutcome = new ResourceSoTConduitActionResponse(sotName, functionStatusEnum);
        outcomeFactory.populateResourceActivityOutcome(sotConduitOutcome, resourceId,  status, activityLocation);
        if(resourceId != null){
            sotConduitOutcome.setId(resourceId);
        }
        if(resource != null) {
            sotConduitOutcome.setResource(resource);
            // If the Resource has an Id, overwrite the value set above.
            if (resource.hasId()) {
                sotConduitOutcome.setId(resource.getIdElement());
            }
        }
        return(sotConduitOutcome);
    }
}
