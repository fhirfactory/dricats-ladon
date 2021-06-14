package net.fhirfactory.pegacorn.ladon.model.virtualdb;

import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import org.hl7.fhir.r4.model.*;

import java.io.Serializable;
import java.util.Map;

public interface ResourceDBEngineInterface {
    //
    // Create
    //
    public TransactionMethodOutcome createResource(Resource resourceToCreate);

    //
    // Review / Get
    //
    public TransactionMethodOutcome getResource(IdType id);
    //
    // Update
    //
    public TransactionMethodOutcome updateResource(Resource resourceToUpdate);
    //
    // Delete
    //
    public TransactionMethodOutcome deleteResource(Resource resourceToDelete);

    //
    // resourceSearches (base set, keep limited or else)
    //
    public TransactionMethodOutcome getResourcesViaSearchCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet);
    public TransactionMethodOutcome findResourceViaIdentifier(Identifier identifier);
}
