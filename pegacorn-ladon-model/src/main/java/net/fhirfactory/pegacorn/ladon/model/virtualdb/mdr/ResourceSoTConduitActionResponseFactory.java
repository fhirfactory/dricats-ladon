package net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr;

import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionStatusEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcomeFactory;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.valuesets.ComponentFunctionStatusEnum;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ResourceSoTConduitActionResponseFactory {

    @Inject
    VirtualDBMethodOutcomeFactory outcomeFactory;

    @Deprecated
    public ResourceSoTConduitActionResponse createResourceConduitActionResponse(Reference originatingSystem, Reference originatingOrganization, IdType resourceId, VirtualDBActionStatusEnum status, String activityLocation){
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

    public ResourceSoTConduitActionResponse createResourceConduitActionResponse(String sotName, ComponentFunctionStatusEnum functionStatusEnum, Resource resource, IdType resourceId, VirtualDBActionStatusEnum status, String activityLocation){
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
