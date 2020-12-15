package net.fhirfactory.pegacorn.ladon.model.virtualdb;

import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcome;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import org.hl7.fhir.r4.model.*;

import java.io.Serializable;
import java.util.Map;

public interface ResourceDBEngineInterface {
    //
    // Create
    //
    public VirtualDBMethodOutcome createResource(Resource resourceToCreate);

    //
    // Review / Get
    //
    public VirtualDBMethodOutcome getResource(IdType id);
    //
    // Update
    //
    public VirtualDBMethodOutcome updateResource(Resource resourceToUpdate);
    //
    // Delete
    //
    public VirtualDBMethodOutcome deleteResource(Resource resourceToDelete);

    //
    // resourceSearches (base set, keep limited or else)
    //
    public VirtualDBMethodOutcome getResourcesViaSearchCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet);
    public VirtualDBMethodOutcome findResourceViaIdentifier(Identifier identifier);
}
