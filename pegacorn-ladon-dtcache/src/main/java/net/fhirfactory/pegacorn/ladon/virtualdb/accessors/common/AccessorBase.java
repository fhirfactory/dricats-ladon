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
package net.fhirfactory.pegacorn.ladon.virtualdb.accessors.common;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierDataTypeHelpers;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import net.fhirfactory.pegacorn.ladon.processingplant.LadonProcessingPlant;
import net.fhirfactory.pegacorn.ladon.virtualdb.engine.common.ResourceDBEngine;
import net.fhirfactory.pegacorn.ladon.virtualdb.workshop.VirtualDBWorkshop;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.sta.TransactionStatusElement;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.platform.edge.model.common.ResourceAccessorInterfaceBase;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import net.fhirfactory.pegacorn.wups.archetypes.unmanaged.NonResilientWithAuditTrailWUP;
import org.hl7.fhir.r4.model.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

abstract public class AccessorBase extends NonResilientWithAuditTrailWUP implements ResourceAccessorInterfaceBase {

    private TopologyNodeFunctionFDN accessorFunctionToken;
    private WUPIdentifier accessorIdentifier;
    private String accessorName;
    private WUPJobCard accessorJobCard;
    private String version;


    @Inject
    private PegacornIdentifierDataTypeHelpers identifierDataTypeHelpers;

    @Inject
    private VirtualDBWorkshop workshop;

    public AccessorBase() {
        super();
        this.accessorName = specifyAccessorResourceTypeName();
        this.version = specifyAccessorResourceTypeVersion();
    }

    @Override
    protected String specifyWUPInstanceName(){
        return(specifyAccessorResourceTypeName()+"AccessorWUP");
    }

    @Override
    protected String specifyWUPInstanceVersion(){
        return(specifyAccessorResourceTypeVersion());
    }

    @Override
    protected Workshop specifyWorkshop() {
        return (workshop);
    }

    abstract protected String specifyAccessorResourceTypeName();
    abstract protected String specifyAccessorResourceTypeVersion();

    abstract protected ResourceDBEngine getResourceDBEngine();

    abstract protected List<Identifier> resolveIdentifierList(Resource resource);

    public Identifier getBestIdentifier(MethodOutcome outcome){
        Resource resource = (Resource)outcome.getResource();
        List<Identifier> identifiers = resolveIdentifierList(resource);
        Identifier identifier = identifierDataTypeHelpers.getBestIdentifier(identifiers);
        return(identifier);
    }

    protected String getResourceTypeName(){return(specifyAccessorResourceTypeName());}
    protected String getResourceTypeVersion(){return(specifyAccessorResourceTypeVersion());}


    @Inject
    private LadonProcessingPlant ladonPlant;
    
    @Inject
    private TransactionMethodOutcomeFactory outcomeFactory;


    public void initialiseServices() {
        initialise();
    }

    public TransactionMethodOutcome getResource(IdType id) {
        getLogger().debug(".getResource(): Entry, id (IdType) --> {}", id);
        TransactionStatusElement currentTransaction = this.beginRESTfulTransaction(id,getResourceTypeName(),null, null, TransactionTypeEnum.REVIEW);
        TransactionMethodOutcome outcome = getResourceDBEngine().getResource(id);
        currentTransaction.setTransactionStatus(outcome.getStatusEnum());
        this.endRESTfulTransaction(currentTransaction);
        getLogger().debug(".getResource(): Exit, Resource retrieved, outcome --> {}", outcome);
        return (outcome);
    }

    public TransactionMethodOutcome getResourceNoAudit(IdType id) {
        getLogger().debug(".getResourceNoAudit(): Entry, id (Identifier) --> {}", id);
        TransactionMethodOutcome outcome = getResourceDBEngine().getResource(id);
        getLogger().debug(".getResourceNoAudit(): Exit, Resource retrieved, outcome --> {}", outcome);
        return (outcome);
    }

    public TransactionMethodOutcome createResource(Resource newResource){
        getLogger().debug(".createResource(): Entry, newResource (Resource) --> {}", newResource);
        Identifier bestIdentifier = identifierDataTypeHelpers.getBestIdentifier(resolveIdentifierList(newResource));
        TransactionStatusElement currentTransaction = this.beginRESTfulTransaction(newResource.getIdElement(), getResourceTypeName() , bestIdentifier, newResource, TransactionTypeEnum.CREATE);
        TransactionMethodOutcome outcome = getResourceDBEngine().createResource(newResource);
        currentTransaction.setTransactionStatus(outcome.getStatusEnum());
        this.endRESTfulTransaction(currentTransaction);
        getLogger().debug(".createResource(): Exit, Resource Create, outcome --> {}", outcome);
        return(outcome);
    }

    public TransactionMethodOutcome deleteResource(Resource resourceToRemove){
        getLogger().debug(".deleteResource(): Entry, resourceToRemove --> {}", resourceToRemove);
        Identifier bestIdentifier = identifierDataTypeHelpers.getBestIdentifier(resolveIdentifierList(resourceToRemove));
        TransactionStatusElement currentTransaction = this.beginRESTfulTransaction(resourceToRemove.getIdElement(), getResourceTypeName() , bestIdentifier, resourceToRemove, TransactionTypeEnum.DELETE);
        TransactionMethodOutcome outcome  = getResourceDBEngine().deleteResource(resourceToRemove);
        currentTransaction.setTransactionStatus(outcome.getStatusEnum());
        this.endRESTfulTransaction(currentTransaction);
        getLogger().debug(".deleteResource(): Exit, Resource Deleted, outcome --> {}", outcome);
        return(outcome);
    }

    public TransactionMethodOutcome updateResource(Resource resourceToUpdate){
        getLogger().debug(".updateResource(): Entry, resourceToUpdate --> {}", resourceToUpdate);
        Identifier bestIdentifier = identifierDataTypeHelpers.getBestIdentifier(resolveIdentifierList(resourceToUpdate));
        TransactionStatusElement currentTransaction = this.beginRESTfulTransaction(resourceToUpdate.getIdElement(), getResourceTypeName() ,bestIdentifier, resourceToUpdate, TransactionTypeEnum.UPDATE);
        TransactionMethodOutcome outcome  = getResourceDBEngine().deleteResource(resourceToUpdate);
        currentTransaction.setTransactionStatus(outcome.getStatusEnum());
        this.endRESTfulTransaction(currentTransaction);
        getLogger().debug(".updateResource(): Exit, Resource Updated, outcome --> {}", outcome);
        return(outcome);
    }

    /**
     *
     * @param resourceType
     * @param parameterSet
     * @return
     */
    public TransactionMethodOutcome searchUsingCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet) {
        getLogger().debug(".searchUsingCriteria(): Entry, Search Name --> {}, parameterSet --> {}", searchName, parameterSet);
        TransactionStatusElement currentTransaction = this.beginSearchTransaction(getResourceTypeName(),getResourceTypeVersion(), parameterSet);
        TransactionMethodOutcome outcome = getResourceDBEngine().getResourcesViaSearchCriteria(resourceType, searchName, parameterSet);
        if(outcome == null){
            currentTransaction.setTransactionStatusCommentary("Search Process Failed");
            currentTransaction.setTransactionStatus(TransactionStatusEnum.SEARCH_FAILURE);
            endSearchTransaction(new Bundle(), currentTransaction);
            outcome = outcomeFactory.generateBadAttributeOutcome(getResourceTypeName()+"searchUsingCriteria", TransactionTypeEnum.SEARCH, TransactionStatusEnum.SEARCH_FINISHED,"No Entries");
            return(outcome);
        }
        if(outcome.getStatusEnum() == TransactionStatusEnum.SEARCH_FAILURE) {
            currentTransaction.setTransactionStatusCommentary("Search Process Failed");
            currentTransaction.setTransactionStatus(TransactionStatusEnum.SEARCH_FAILURE);
            endSearchTransaction(new Bundle(), currentTransaction);
            return(outcome);
        }
        Resource searchResultResource = (Resource)outcome.getResource();
        if( searchResultResource == null){
            currentTransaction.setTransactionStatusCommentary("Search Process Failed");
            currentTransaction.setTransactionStatus(TransactionStatusEnum.SEARCH_FINISHED);
            endSearchTransaction(new Bundle(), currentTransaction);
            getLogger().debug(".searchUsingCriteria(): Exit, result set is null");
            return(outcome);
        }
        if(searchResultResource.getResourceType() == ResourceType.Bundle){
            Bundle searchResultBundle = (Bundle)searchResultResource;
            currentTransaction.setTransactionStatus(TransactionStatusEnum.SEARCH_FINISHED);
            endSearchTransaction(searchResultBundle, currentTransaction);
        } else {
            currentTransaction.setTransactionStatus(TransactionStatusEnum.SEARCH_FINISHED);
            endSearchTransaction(new Bundle(), currentTransaction);
        }
        getLogger().debug(".searchUsingCriteria(): Exit");
        return(outcome);
    }

    public TransactionMethodOutcome findResourceViaIdentifier(Identifier identifier) {
        getLogger().debug(".findResourceViaIdentifier(): Entry, identifier (Identifier) --> {}", identifier);
        TransactionStatusElement currentTransaction = this.beginRESTfulTransaction(null,getResourceTypeName(),identifier,null,TransactionTypeEnum.REVIEW);
        TransactionMethodOutcome outcome = getResourceDBEngine().findResourceViaIdentifier(identifier);
        if(getLogger().isTraceEnabled()) {
            getLogger().trace(".findResourceViaIdentifier(): outcome.id --> {}", outcome.getId());
        }
        if(outcome.getStatusEnum().equals(TransactionStatusEnum.REVIEW_FINISH)) {
            Resource retrievedResource = (Resource)outcome.getResource();
            if(getLogger().isTraceEnabled()) {
                getLogger().trace(".findResourceViaIdentifier(): Review Finsihed, resource found!");
                getLogger().trace(".findResourceViaIdentifier(): retrievedResource.id (Resource) --> {}", retrievedResource.getId());
                getLogger().trace(".findResourceViaIdentifier(): retrievedResource.type --> {}", retrievedResource.getResourceType());
            }
            currentTransaction.setTransactionStatus(outcome.getStatusEnum());
            // TODO fix loading resource
            this.endRESTfulTransaction(currentTransaction);
        } else {
            currentTransaction.setTransactionStatus(outcome.getStatusEnum());
            getLogger().debug(".findResourceViaIdentifier(): Review Finsihed, resource not found!");
            this.endRESTfulTransaction(currentTransaction);
        }
        getLogger().debug(".findResourceViaIdentifier(): Exit, Resource retrieved, outcome --> {}", outcome);
        return (outcome);
    }
    
    

}
