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
package net.fhirfactory.pegacorn.ladon.virtualdb.persistence.common;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.bundle.BundleContentHelper;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.ladon.virtualdb.workshop.VirtualDBWorkshop;
import net.fhirfactory.pegacorn.petasos.core.sta.wup.GenericSTAClientWUPTemplate;
import net.fhirfactory.pegacorn.platform.edge.ask.InternalFHIRClientServices;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.inject.Inject;

public abstract class PersistenceServiceBase extends GenericSTAClientWUPTemplate {

    @Inject
    private TopologyIM deploymentTopologyIM;

    @Inject
    BundleContentHelper bundleHelper;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private VirtualDBWorkshop workshop;

    @Inject
    private TransactionMethodOutcomeFactory virtualDBMethodOutcomeFactory;

    public PersistenceServiceBase() {
        super();
    }

    @Override
    protected WorkshopTopologyNode specifyWorkshop() {
        return (workshop.getWorkshopNode());
    }

    @Override
    protected PegacornTopologyFactoryInterface specifyTopologyFactory() {
        return (processingPlant.getTopologyFactory());
    }

    abstract protected String specifyPersistenceServiceName();
    abstract protected String specifyPersistenceServiceVersion();
    abstract protected Logger getLogger();
    abstract protected InternalFHIRClientServices getFHIRClientServices();
    abstract protected Identifier getBestIdentifier(MethodOutcome outcome);
    abstract public TransactionMethodOutcome synchroniseResource(ResourceType resourceType, Resource resource);

    @Override
    protected String specifySTAClientName() {
        return (specifyPersistenceServiceName());
    }

    @Override
    protected String specifySTAClientVersion() {
        return (specifyPersistenceServiceVersion());
    }

    @Override
    protected ProcessingPlantInterface specifyProcessingPlant() {
        return (processingPlant);
    }

    //
    // Database Transactions
    //

    public TransactionMethodOutcome getResourceById(String resourceType, IdType id){
        getLogger().debug(".standardReviewResource(): Entry, identifier --> {}", id);
        // Attempt to "get" the Resource
        Resource outputResource = (Resource)getFHIRClientServices().getClient()
                .read()
                .resource(resourceType)
                .withId(id)
                .execute();
        if(outputResource != null) {
            // There was no Resource with that Identifier....
            TransactionMethodOutcome outcome = new TransactionMethodOutcome();
            outcome.setCreated(false);
            outcome.setCausalAction(TransactionTypeEnum.REVIEW);
            outcome.setStatusEnum(TransactionStatusEnum.REVIEW_FINISH);
            CodeableConcept details = new CodeableConcept();
            Coding detailsCoding = new Coding();
            detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html"); // TODO Pegacorn specific encoding --> need to check validity
            detailsCoding.setCode("MSG_RESOURCE_RETRIEVED"); // TODO Pegacorn specific encoding --> need to check validity
            detailsCoding.setDisplay("Resource Id ("+ id +") has been retrieved");
            details.setText("Resource Id ("+ id +") has been retrieved");
            details.addCoding(detailsCoding);
            OperationOutcome opOutcome = new OperationOutcome();
            OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
            newOutcomeComponent.setDiagnostics("standardReviewResource()" + "::" + "REVIEW");
            newOutcomeComponent.setDetails(details);
            newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
            newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
            opOutcome.addIssue(newOutcomeComponent);
            outcome.setOperationOutcome(opOutcome);
            outcome.setResource(outputResource);
            return (outcome);
        } else {
            // There was no Resource with that Identifier....
            TransactionMethodOutcome outcome = new TransactionMethodOutcome();
            outcome.setCreated(false);
            outcome.setCausalAction(TransactionTypeEnum.REVIEW);
            outcome.setStatusEnum(TransactionStatusEnum.REVIEW_FAILURE);
            CodeableConcept details = new CodeableConcept();
            Coding detailsCoding = new Coding();
            detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
            detailsCoding.setCode("MSG_NO_MATCH");
            detailsCoding.setDisplay("No Resource found matching the query: " + id);
            details.setText("No Resource found matching the query: " + id);
            details.addCoding(detailsCoding);
            OperationOutcome opOutcome = new OperationOutcome();
            OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
            newOutcomeComponent.setDiagnostics("standardReviewResource()" + "::" + "REVIEW");
            newOutcomeComponent.setDetails(details);
            newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
            newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
            opOutcome.addIssue(newOutcomeComponent);
            outcome.setOperationOutcome(opOutcome);
            return (outcome);
        }
    }

    public TransactionMethodOutcome standardCreateResource(Resource resourceToCreate) {
        getLogger().debug(".standardCreateResource(): Entry, resourceToCreate --> {}", resourceToCreate);
        MethodOutcome callOutcome = getFHIRClientServices().getClient()
                .create()
                .resource(resourceToCreate)
                .prettyPrint()
                .encodedJson()
                .execute();
        if(!callOutcome.getCreated()) {
            getLogger().error(".writeResource(): Can't create Resource {}, error --> {}", callOutcome.getOperationOutcome());
        }
        Identifier bestIdentifier = getBestIdentifier(callOutcome);
        TransactionMethodOutcome outcome = new TransactionMethodOutcome(TransactionTypeEnum.CREATE, bestIdentifier, callOutcome);
        getLogger().debug(".standardCreateResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }

    public TransactionMethodOutcome standardReviewResource(Class <? extends IBaseResource> resourceClass, Identifier identifier){
        getLogger().debug(".standardReviewResource(): Entry, identifier --> {}", identifier);
        TransactionMethodOutcome outcome = standardGetResourceViaIdentifier(resourceClass, identifier);
        return(outcome);
    }

    /**
     *
     * @param resourceClass
     * @param identifier
     * @return
     */

    public TransactionMethodOutcome standardGetResourceViaIdentifier(Class <? extends IBaseResource> resourceClass, Identifier identifier){
        getLogger().debug(".standardGetResourceViaIdentifier(): Entry, identifier --> {}", identifier);
        if(getLogger().isDebugEnabled()) {
            getLogger().debug(".standardGetResourceViaIdentifier(): Entry identifier.type.system --> {}", identifier.getType().getCodingFirstRep().getSystem());
            getLogger().debug(".standardGetResourceViaIdentifier(): Entry, identifier.type.code --> {}", identifier.getType().getCodingFirstRep().getCode());
            getLogger().debug(".standardGetResourceViaIdentifier(): Entry, identifier.value --> {}", identifier.getValue());
        }
        String activityLocation = resourceClass.getSimpleName() + "SoTResourceConduit::standardGetResourceViaIdentifier()";
        Resource retrievedResource = (Resource)getFHIRClientServices().findResourceByIdentifier(resourceClass.getSimpleName(), identifier);
        if (retrievedResource == null){
            // There was no response to the query or it was in error....
            getLogger().trace(".standardGetResourceViaIdentifier(): There was no response to the query or it was in error....");
            TransactionMethodOutcome outcome = virtualDBMethodOutcomeFactory.createResourceActivityOutcome(null, TransactionStatusEnum.REVIEW_FAILURE,activityLocation);
            outcome.setIdentifier(identifier);
            return(outcome);
        } else {
            getLogger().trace(".standardGetResourceViaIdentifier(): There is a Resource with that Identifier....");
            TransactionMethodOutcome outcome = virtualDBMethodOutcomeFactory.createResourceActivityOutcome(
                    null,
                    TransactionStatusEnum.REVIEW_FINISH,
                    activityLocation);
            outcome.setIdentifier(identifier);
            outcome.setResource(retrievedResource);
            getLogger().debug(".standardGetResourceViaIdentifier(): Exit, outcome --> {}", outcome);
            return (outcome);
        }
    }

    public TransactionMethodOutcome standardUpdateResource(Resource resourceToUpdate) {
        getLogger().debug(".standardUpdateResource(): Entry, resourceToUpdate --> {}", resourceToUpdate);
        MethodOutcome callOutcome = getFHIRClientServices().getClient()
                .update()
                .resource(resourceToUpdate)
                .prettyPrint()
                .encodedJson()
                .execute();
        if(!callOutcome.getCreated()) {
            getLogger().error(".writeResource(): Can't update Resource {}, error --> {}", callOutcome.getOperationOutcome());
        }
        Identifier bestIdentifier = getBestIdentifier(callOutcome);
        TransactionMethodOutcome outcome = new TransactionMethodOutcome(TransactionTypeEnum.UPDATE, bestIdentifier, callOutcome);
        getLogger().debug(".standardUpdateResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }
}
