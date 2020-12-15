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
package net.fhirfactory.pegacorn.ladon.edge.answer.resourceproxies;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import net.fhirfactory.pegacorn.datasets.fhir.r4.operationaloutcome.OperationOutcomeGenerator;
import net.fhirfactory.pegacorn.ladon.edge.answer.resourceproxies.common.LadonEdgeSynchronousCRUDResourceBase;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBActionStatusEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.operations.VirtualDBMethodOutcome;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import net.fhirfactory.pegacorn.ladon.virtualdb.accessors.ProcedureAccessor;
import net.fhirfactory.pegacorn.ladon.virtualdb.accessors.common.AccessorBase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.OperationNotSupportedException;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class ProcedureProxy extends LadonEdgeSynchronousCRUDResourceBase implements IResourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ProcedureProxy.class);

    public ProcedureProxy() {
        super();
        this.setInitialised(false);
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Inject
    private ProcedureAccessor virtualDBAccessor;

    @Inject
    private OperationOutcomeGenerator outcomeGenerator;

    @Override
    protected AccessorBase specifyVirtualDBAccessor() {
        return (virtualDBAccessor);
    }

    @Override
    public Class<Procedure> getResourceType() {
        return (Procedure.class);
    }

    /**
     * The "@Create" annotation indicates that this method implements "create=type", which adds a
     * new instance of a resource to the server.
     */
    @Create()
    public MethodOutcome createProcedure(@ResourceParam Procedure theResource) {
        LOG.debug(".createPatient(): Entry, thePatient (Patient) --> {}", theResource);
        VirtualDBMethodOutcome outcome = getVirtualDBAccessor().createResource(theResource);
        return (outcome);
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this method supports the read and/or
     * get operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam} paramater, and should return a
     * single resource instance.
     * </p>
     *
     * @param resourceId The read operation takes one parameter, which must be of type IdDt and must be annotated
     *                   with the "@Read.IdParam" annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Procedure reviewProcedure(@IdParam IdType resourceId) {
        LOG.debug(".reviewProcedure(): Entry, resourceId (IdType) --> {}", resourceId);
        VirtualDBMethodOutcome outcome = getResource(resourceId);
        Procedure retrievedDocRef = (Procedure) outcome.getResource();
        LOG.debug(".reviewProcedure(): Exit, retrieved Document Reference (Procedure) --> {}", retrievedDocRef);
        return (retrievedDocRef);
    }

    /**
     * This is the "update" operation. The "@Update" annotation indicates that this method supports the update operation.
     * <p>
     * Update operations take a single parameter annotated with the {@ResourceParam} paramater, and should return a
     * MethodOutcome as to the sucess (or otherwise) of the operation.
     * </p>
     *
     * @param resourceToUpdate The update operation takes one parameter, which must be the Procedure resource to be updated.
     * @return Returns an outcome (MethodOutcome) detailing the success (or otherwise) of the action.
     */
    @Update()
    public MethodOutcome updateProcedure(@ResourceParam Procedure resourceToUpdate) {
        LOG.debug(".readProcedure(): Entry, docRefToUpdate (Procedure) --> {}", resourceToUpdate);
        VirtualDBMethodOutcome outcome = updateResource(resourceToUpdate);
        LOG.debug(".readProcedure(): Exit, outcome (VirtualDBMethodOutcome) --> {}", outcome);
        return (outcome);
    }

    @Delete()
    public MethodOutcome deleteProcedure(@IdParam IdType resourceId) throws OperationNotSupportedException {
        LOG.debug(".deleteProcedure(): Entry, resourceId (IdType) --> {}", resourceId);
        throw (new OperationNotSupportedException("deletion of a Procedure is not supported"));
    }

    //
    //
    // Support Searches
    //
    //

    @Search()
    public Bundle findByIdentifier(@RequiredParam(name = Procedure.SP_IDENTIFIER) TokenParam identifierParam) {
        getLogger().debug(".findByIdentifier(): Entry, identifierParam --> {}", identifierParam);
        Identifier identifierToSearchFor = tokenParam2Identifier(identifierParam);
        Resource outcome = (Resource) findResourceViaIdentifier(identifierToSearchFor);
        if (outcome.getResourceType().equals(ResourceType.Bundle)) {
            Bundle outcomeBundle = (Bundle) outcome;
            return (outcomeBundle);
        } else {
            Bundle outcomeBundle = getBundleContentHelper().buildSearchResponseBundle(outcome);
            return (outcomeBundle);
        }
    }

    @Search(queryName = "searchProceduresForPatientDuringPeriod")
    public Bundle searchProcedureSetForPatient(@RequiredParam(name = Procedure.SP_DATE) DateRangeParam theRange, @RequiredParam(name = "subject") TokenParam patientIdentifierParam) {
        LOG.debug(".searchByDateAndSubject(): Entry, DateTimeRange --> {}, Patient --> {}", theRange, patientIdentifierParam);

        HashMap<Property, Serializable> argumentList = new HashMap<>(); // TODO Need to replace "Serializable" with something more meaningful and appropriate

        // First Parameter, the DocumentReference.type
        Property subjectProperty = new Property(
                "subject",
                "Reference",
                "The person, animal or group on which the procedure was performed.",
                0,
                1,
                (List<? extends Base>) null);
        argumentList.put(subjectProperty, patientIdentifierParam);
        // Second Parameter, the DocumentReference.date (expressed as a Period, where the date is to be in-between)
        Period searchPeriod = new Period();
        Property docRefDateProperty = new Property(
                "performed",
                "dateTime",
                "Estimated or actual date, date-time, period, or age when the procedure was performed. Allows a period to support complex procedures that span more than one date, and also allows for the length of the procedure to be captured.",
                0,
                1,
                (List<? extends Base>) null);
        argumentList.put(docRefDateProperty, theRange);

        VirtualDBMethodOutcome outcome = getVirtualDBAccessor().searchUsingCriteria(ResourceType.DocumentReference, SearchNameEnum.PROCEDURE_PATIENT_AND_DATE, argumentList);

        if (outcome.getStatusEnum() == VirtualDBActionStatusEnum.SEARCH_FINISHED) {
            Bundle searchOutcome = (Bundle) outcome.getResource();
            return (searchOutcome);
        } else {
            Bundle outputBundle = new Bundle();
            outputBundle.setType(Bundle.BundleType.SEARCHSET);
            outputBundle.setTimestamp(Date.from(Instant.now()));
            outputBundle.setTotal(0);
            return (outputBundle);
        }
    }
}
