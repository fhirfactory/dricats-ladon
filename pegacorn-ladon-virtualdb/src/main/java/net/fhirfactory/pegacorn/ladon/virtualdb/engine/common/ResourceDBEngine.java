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
package net.fhirfactory.pegacorn.ladon.virtualdb.engine.common;

import net.fhirfactory.pegacorn.deployment.properties.LadonDefaultDeploymentProperties;
import net.fhirfactory.pegacorn.ladon.mdr.conduit.controller.common.ResourceSoTConduitController;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.ResourceDBEngineInterface;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionStatusEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcome;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcomeFactory;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import net.fhirfactory.pegacorn.ladon.virtualdb.cache.common.VirtualDBIdTypeBasedCacheBase;
import net.fhirfactory.pegacorn.ladon.virtualdb.persistence.common.PersistenceServiceBase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ResourceDBEngine implements ResourceDBEngineInterface {

    @Inject
    private VirtualDBMethodOutcomeFactory outcomeFactory;

    @Inject
    private LadonDefaultDeploymentProperties ladonDefaultDeploymentProperties;

    abstract protected VirtualDBIdTypeBasedCacheBase specifyDBCache();

    abstract protected ResourceSoTConduitController specifySourceOfTruthAggregator();
    
    abstract protected PersistenceServiceBase specifyPersistenceService();

    abstract protected Logger getLogger();

    abstract protected ResourceType specifyResourceType();

    abstract protected List<Identifier> resolveIdentifierSet(Resource resource);

    
    protected ResourceSoTConduitController getSourceOfTruthAggregator(){
        return(specifySourceOfTruthAggregator());
    }

    protected VirtualDBIdTypeBasedCacheBase getDBCache(){
        return(specifyDBCache());
    }

    protected PersistenceServiceBase getPersistenceService(){
        return(specifyPersistenceService());
    }

    protected ResourceType getResourceType(){return(specifyResourceType());}

    @Override
    public VirtualDBMethodOutcome createResource(Resource resourceToCreate) {
        getLogger().debug(".createResource(): Entry, resourceToCreate --> {}", resourceToCreate);
        IdType newId = null;
        if(!resourceToCreate.hasId()){
            getLogger().trace(".createResource(): Resource did not have an Id, so creating one!");
            newId = new IdType();
            newId.setValueAsString(resourceToCreate.getResourceType().toString()+"/"+UUID.randomUUID().toString());
            resourceToCreate.setId(newId);
            getLogger().trace(".createResource(): Resource Id created and added to Resource, Id --> {}", newId);
        } else {
            newId = resourceToCreate.getIdElement();
        }
        VirtualDBMethodOutcome outcome = getSourceOfTruthAggregator().createResource(resourceToCreate);
        if (outcome.getStatusEnum().equals(VirtualDBActionStatusEnum.CREATION_FINISH)) {
            getLogger().trace(".createResource(): Resource successfully created in the MDR (Set), now adding it to the Cache & VirtualDB");
            VirtualDBMethodOutcome virtualDBOutcome = getPersistenceService().standardCreateResource(resourceToCreate);
            resourceToCreate.setId(virtualDBOutcome.getId());
            VirtualDBMethodOutcome cacheCreateOutcome = getDBCache().createResource(resourceToCreate);
            if(!newId.equals(outcome.getResource().getIdElement())){
                getLogger().error(".createResource(): Server Overwrote the Id! Intended Value --> {}, value from Server --> {}", newId, outcome.getId());
            }
        }
        getLogger().debug(".createResource(): Resource created, exiting");
        return (outcome);
    }

    /**
     * This function retrieves a Resource using the IdType id.
     *
     * If the resource is presently within the cache, it returns that instance
     *
     * If the resource is not in the cache, the Pegacorn Persistence Service is queried to see if the resource has been
     * previously loaded. If it has, then the version within the Persistence Service will contain mappings to the
     * per-SoT internal identifier for that resource. It will then iterate through the SoTs using the ids and
     * aggregate the result. Note that if the last time the resource was accessed exceeds a given threshold, then the
     * Identifiers contained within the aggregated instance within the Persistence Service will be used to extract
     * the resource from ALL known SoT's supporting that resource type. THis allows for the introduction of new SoT
     * Conduits.
     *
     * If there is no resource with the given id within the Pegacorn Persistence Service, then the retrieval is said
     * to have failed.
     *
     * @param id The "internal" id of the Resource
     * @return A VirtualDBMethodOutcome detailing the success (or otherwise) of the action. A .getResource() on the
     * returned object will retrieve the Resource in question if the retrieval was successful.
     */
    @Override
    public VirtualDBMethodOutcome getResource(IdType id) {
        getLogger().debug(".getResource(IdType): Entry, id --> {}", id);
        getLogger().trace(".getResource(IdType): Check to see if there is an entry in the cache");
/*        VirtualDBMethodOutcome outcome = getDBCache().getResource(id);
        if (outcome.getResource() != null && outcome.getStatusEnum().equals(VirtualDBActionStatusEnum.REVIEW_FINISH)) {
            getLogger().debug(".getResource(IdType): Resource is in cache, returning it");
        }
        getLogger().trace(".getResource(IdType): Check to see if the Resource is available within Pegacorn's Persistence Service");
        VirtualDBMethodOutcome persistenceServiceOutcome = getPersistenceService().getResourceById(getResourceType().toString(), id);
        if (outcome.getResource() != null && persistenceServiceOutcome.getStatusEnum().equals(VirtualDBActionStatusEnum.REVIEW_FINISH)) {
            Resource persistenceServiceOriginatedResource = (Resource) persistenceServiceOutcome.getResource();
            boolean requiresRefresh = true;
            if(persistenceServiceOriginatedResource.getMeta().hasLastUpdated()){
                Long resourceRefreshAge = Date.from(Instant.now()).getTime() - ladonDefaultDeploymentProperties.getResourceSourceOfTruthConduitScanningPeriod();
                if(persistenceServiceOriginatedResource.getMeta().getLastUpdated().getTime() > resourceRefreshAge ){
                    requiresRefresh = false;
                }
            }
            if(requiresRefresh){

            }
            List<Identifier> identifierList = resolveIdentifierSet(persistenceServiceOriginatedResource);
            if (identifierList.isEmpty()) {
                outcome = outcomeFactory.generateEmptyGetResponse(getResourceType(), id);
            } else {
                outcome = getSourceOfTruthAggregator().reviewResource(identifierList);
            }
            return (outcome);
        } else {
            outcome = outcomeFactory.generateEmptyGetResponse(getResourceType(), id);
            return (outcome);
        } */
        VirtualDBMethodOutcome outcome = outcomeFactory.generateEmptyGetResponse(getResourceType(), id);
        return (outcome);
    }

    @Override
    public VirtualDBMethodOutcome updateResource(Resource resourceToUpdate) {
        VirtualDBMethodOutcome outcome = getSourceOfTruthAggregator().updateResource(resourceToUpdate);
        if (outcome.getStatusEnum() == VirtualDBActionStatusEnum.UPDATE_FINISH) {
            VirtualDBMethodOutcome cacheCreateOutcome = getDBCache().updateResource(resourceToUpdate);
        }
        return (outcome);
    }

    @Override
    public VirtualDBMethodOutcome deleteResource(Resource resourceToDelete) {
        VirtualDBMethodOutcome outcome = getSourceOfTruthAggregator().deleteResource(resourceToDelete);
        VirtualDBMethodOutcome cacheCreateOutcome = getDBCache().deleteResource(resourceToDelete);
        return (outcome);
    }

    private void updateCache(VirtualDBMethodOutcome outcome){
        if(outcome == null){
            return;
        }
        if(outcome.getStatusEnum() != VirtualDBActionStatusEnum.SEARCH_FINISHED) {
            return;
        }
        Bundle outcomeBundle = (Bundle)outcome.getResource();
        if(outcomeBundle == null){
            return;
        }
        if(outcomeBundle.getTotal() < 1){
            return;
        }
        for(Bundle.BundleEntryComponent entry: outcomeBundle.getEntry()){
            VirtualDBMethodOutcome cacheCreateOutcome = getDBCache().syncResource(entry.getResource());
        }
    }

    //
    //
    // Searches
    //
    //

    public VirtualDBMethodOutcome findResourceViaIdentifier(Identifier identifier) {
        getLogger().debug(".findResourceViaIdentifier(): Entry");
        VirtualDBMethodOutcome outcome = getDBCache().getResource(identifier);
        if (outcome.getStatusEnum() == VirtualDBActionStatusEnum.REVIEW_RESOURCE_NOT_IN_CACHE) {
            getLogger().trace(".getResource(): Resource not in Cache, going to Sources-of-Truth");
            outcome = getSourceOfTruthAggregator().reviewResource(identifier);
        }
        getLogger().debug(".findResourceViaIdentifier(): Exit");
        return (outcome);
    }

    @Override
    public VirtualDBMethodOutcome getResourcesViaSearchCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet) {
        getLogger().debug("ResourceDBEngine::getResourcesViaSearchCriteria(): Entry, ResourceType --> {}, Search Name --> {}", resourceType.toString(), searchName.getSearchName());
        VirtualDBMethodOutcome outcome = getSourceOfTruthAggregator().getResourcesViaSearchCriteria(resourceType, searchName, parameterSet);
        updateCache(outcome);
        getLogger().debug("ResourceDBEngine::getResourcesViaSearchCriteria(): Exit");
        return(outcome);
    }
}
