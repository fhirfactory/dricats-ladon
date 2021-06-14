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
package net.fhirfactory.pegacorn.ladon.mdr.conduit.controller.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Property;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import net.fhirfactory.pegacorn.ladon.mdr.conduit.controller.aggregationservices.common.ResourceContentAggregationServiceBase;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceGradeEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitActionResponse;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitSearchResponseElement;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.SoTResourceConduit;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;

public abstract class ResourceSoTConduitController {
    abstract protected Logger getLogger();

    private HashSet<SoTResourceConduit> conduitSet;
    private ResourceType resourceType;

    @Inject
    TransactionMethodOutcomeFactory outcomeFactory;

    public ResourceSoTConduitController(){
        this.conduitSet = new HashSet<>();
        this.resourceType = specifyResourceType();
    }

    public void addResourceConduit(SoTResourceConduit newConduit){
        getLogger().debug(".addResourceConduit(): Entry, newConduit (SoTResourceConduit) --> {}", newConduit);
        if(newConduit == null){
            getLogger().error(".addResourceConduit(): newConduit (SoTResourceConduit) is null");
            return;
        }
        if(conduitSet.contains(newConduit)){
            getLogger().debug(".addResourceConduit(): SoTResourceConduit already exists in set, exiting");
            return;
        }
        getLogger().trace(".addResourceConduit(): Adding SoTResourceConduit to set");
        conduitSet.add(newConduit);
        getLogger().debug(".addResourceConduit(): Exit, SoTResourceConduit added");
    }

    abstract protected ResourceType specifyResourceType();

    abstract protected ResourceContentAggregationServiceBase specifyAggregationService();

    protected ResourceType getResourceType(){
        return(specifyResourceType());
    }

    protected ResourceContentAggregationServiceBase getAggregationService(){
        return(specifyAggregationService());
    }


    //
    // Review / Get Conduit Invocation
    //

    /**
     *
     * @param identifier
     * @return
     */
    protected List<ResourceSoTConduitActionResponse> getResourceFromEachConduit(Identifier identifier){
        getLogger().debug(".getResourceFromEachConduit(Identifier): Entry, identifier (Identifier)--> {}", identifier);
        ArrayList<ResourceSoTConduitActionResponse> loadedResources = new ArrayList<ResourceSoTConduitActionResponse>();
        for(SoTResourceConduit currentConduit: conduitSet){
            getLogger().debug(".getResourceFromEachConduit(Identifier): trying conduit --> {}", currentConduit.getConduitName());
            ResourceSoTConduitActionResponse currentResponse = currentConduit.getResourceViaIdentifier(identifier);
            if(currentResponse.hasResource() && currentResponse.getStatusEnum().equals(TransactionStatusEnum.REVIEW_FINISH)) {
                loadedResources.add(currentResponse);
            }
        }
        getLogger().info(".getResourceFromEachConduit(Identifier): Exit, Number of Elements in List --> {}", loadedResources.size());
        return(loadedResources);
    }

    //
    // Review / Get Conduit Invocation
    //

    /**
     *
     * @param id
     * @return
     */
    protected List<ResourceSoTConduitActionResponse> getResourceFromEachConduit(IdType id){
        getLogger().debug(".getResourceFromEachConduit(IdType): Entry, id (IdType)--> {}", id);
        ArrayList<ResourceSoTConduitActionResponse> loadedResources = new ArrayList<ResourceSoTConduitActionResponse>();
        for(SoTResourceConduit currentConduit: conduitSet){
            ResourceSoTConduitActionResponse currentResponse = currentConduit.reviewResource(id);
            if(currentResponse.getResponseResourceGrade() != ResourceGradeEnum.EMPTY) {
                loadedResources.add(currentResponse);
            }
        }
        getLogger().debug(".getResourceFromEachConduit(IdType): Exit");
        return(loadedResources);
    }

    //
    // Create Conduit Invocation
    //

    /**
     *
     * @param wholeResource
     * @return
     */
    protected List<ResourceSoTConduitActionResponse> createResourceViaEachConduit(Resource wholeResource){
        getLogger().debug(".writeResourceToEachConduit(): Entry, wholeResource --> {}", wholeResource);
        ArrayList<ResourceSoTConduitActionResponse> outcomeSet = new ArrayList<>();
        for(SoTResourceConduit currentConduit: conduitSet) {
            if(currentConduit.supportsDirectCreateAction(wholeResource)) {
                ResourceSoTConduitActionResponse outcome = currentConduit.createResource(wholeResource);
                outcomeSet.add(outcome);
            }
        }
        getLogger().debug(".writeResourceToEachConduit(): Exit");
        return(outcomeSet);
    }

    //
    // Update Conduit Invocation
    //

    /**
     *
     * @param wholeResource
     * @return
     */
    protected List<ResourceSoTConduitActionResponse> updateResourceViaEachConduit(Resource wholeResource){
        getLogger().debug(".writeResourceToEachConduit(): Entry, wholeResource --> {}", wholeResource);
        ArrayList<ResourceSoTConduitActionResponse> outcomeSet = new ArrayList<>();
        for(SoTResourceConduit currentConduit: conduitSet) {
            if(currentConduit.supportsDirectUpdateAction(wholeResource)) {
                ResourceSoTConduitActionResponse outcome = currentConduit.updateResource(wholeResource);
                outcomeSet.add(outcome);
            }
        }
        getLogger().debug(".writeResourceToEachConduit(): Exit");
        return(outcomeSet);
    }

    //
    // Delete Conduit Invocation
    //

    /**
     *
     * @param wholeResource
     * @return
     */
    protected List<ResourceSoTConduitActionResponse> deleteResourceViaEachConduit(Resource wholeResource){
        getLogger().debug(".deleteResourceViaEachConduit(): Entry, wholeResource --> {}", wholeResource);
        ArrayList<ResourceSoTConduitActionResponse> outcomeSet = new ArrayList<>();
        for(SoTResourceConduit currentConduit: conduitSet) {
            if(currentConduit.supportsDirectDeleteAction(wholeResource)) {
                ResourceSoTConduitActionResponse outcome = currentConduit.updateResource(wholeResource);
                outcomeSet.add(outcome);
            }
        }
        getLogger().debug(".deleteResourceViaEachConduit(): Exit");
        return(outcomeSet);
    }

    //
    // Searches
    //

    protected List<ResourceSoTConduitSearchResponseElement> attemptResourceSearch(SearchNameEnum searchName, Map<Property, Serializable> parameterSet){
        getLogger().info(".attemptResourceSearch(): Entry");
        ArrayList<ResourceSoTConduitSearchResponseElement> loadedResources = new ArrayList<ResourceSoTConduitSearchResponseElement>();
        getLogger().info(".attemptResourceSearch(): Iterating through available SoTConduits for Search function");
        for(SoTResourceConduit currentConduit: conduitSet) {
            if(getLogger().isInfoEnabled()){
                getLogger().info(".attemptResourceSearch(): SoTConduit --> {}", currentConduit.getConduitName());
            }
            List<ResourceSoTConduitSearchResponseElement> currentResponse = currentConduit.searchSourceOfTruthUsingCriteria(getResourceType(), searchName, parameterSet);
            if(getLogger().isInfoEnabled()){
                getLogger().info(".attemptResourceSearch(): SoTConduit Search Finished");
            }
            if(currentResponse != null) {
                if(getLogger().isInfoEnabled()){
                    getLogger().info(".attemptResourceSearch(): SoTConduit had results");
                }
                loadedResources.addAll(currentResponse);
            }
        }
        getLogger().info(".attemptResourceSearch(): Exit, result set size --> {}", loadedResources.size());
        return(loadedResources);
    }

    //
    // Public Methods
    //

    public TransactionMethodOutcome createResource(Resource resourceToCreate) {
        List<ResourceSoTConduitActionResponse> methodOutcomes = this.createResourceViaEachConduit(resourceToCreate);
        TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateCreateResponseSet(methodOutcomes);
        return(aggregatedMethodOutcome);
    }

    public TransactionMethodOutcome reviewResource(Identifier identifier) {
        getLogger().info(".reviewResource(): Entry, identifier --> {}");
        List<ResourceSoTConduitActionResponse> methodOutcomes = this.getResourceFromEachConduit(identifier);
        if(methodOutcomes.isEmpty()){
            getLogger().info(".reviewResource(): Failed to find a resource, generating failed outcome");
            String activityLocation = getResourceType().toString() + "reviewResource()";
            TransactionMethodOutcome aggregatedMethodOutcome = outcomeFactory.createResourceActivityOutcome(null, TransactionStatusEnum.REVIEW_FAILURE,activityLocation);
            getLogger().info(".reviewResource(): Exit, failed to find a resource from any Source of Truth, exiting");
            return(aggregatedMethodOutcome);
        } else {
            getLogger().info(".reviewResource(): Exit, found at least one resource, aggregating results");
            TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateGetResponseSet(methodOutcomes);
            getLogger().info(".reviewResource(): Exit, found at least one resource, returning it");
            return (aggregatedMethodOutcome);
        }
    }

    public TransactionMethodOutcome reviewResource(IdType id) {
        List<ResourceSoTConduitActionResponse> methodOutcomes = this.getResourceFromEachConduit(id);
        TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateGetResponseSet(methodOutcomes);
        return(aggregatedMethodOutcome);
    }

    public TransactionMethodOutcome updateResource(Resource resourceToUpdate) {
        List<ResourceSoTConduitActionResponse> methodOutcomes = this.updateResourceViaEachConduit(resourceToUpdate);
        TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateUpdateResponseSet(methodOutcomes);
        return(aggregatedMethodOutcome);
    }

    public TransactionMethodOutcome deleteResource(Resource resourceToDelete) {
        List<ResourceSoTConduitActionResponse> methodOutcomes = this.deleteResourceViaEachConduit(resourceToDelete);
        TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateDeleteResponseSet(methodOutcomes);
        return(aggregatedMethodOutcome);
    }

    public TransactionMethodOutcome getResourcesViaSearchCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet) {
        getLogger().debug("ResourceSoTConduitController::getResourcesViaSearchCriteria(): Entry");
        List<ResourceSoTConduitSearchResponseElement> responseElements = this.attemptResourceSearch(searchName, parameterSet);
        getLogger().trace("ResourceSoTConduitController::getResourcesViaSearchCriteria(): attempted search, now consolidating");
        TransactionMethodOutcome aggregatedMethodOutcome = getAggregationService().aggregateSearchResultSet(responseElements);
        getLogger().debug("ResourceSoTConduitController::getResourcesViaSearchCriteria(): Exit");
        return(aggregatedMethodOutcome);
    }
}
