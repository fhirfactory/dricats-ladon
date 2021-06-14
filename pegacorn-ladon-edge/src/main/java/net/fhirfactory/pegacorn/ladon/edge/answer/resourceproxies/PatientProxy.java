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
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.operationaloutcome.OperationOutcomeGenerator;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.bundle.BundleContentHelper;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.SearchSupportHelper;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import net.fhirfactory.pegacorn.ladon.virtualdb.accessors.PatientAccessor;
import net.fhirfactory.pegacorn.platform.edge.answer.resourceproxies.common.EdgeSynchronousCRUDResourceBase;
import net.fhirfactory.pegacorn.platform.edge.model.common.ResourceAccessorInterfaceBase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class PatientProxy extends EdgeSynchronousCRUDResourceBase implements IResourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PatientProxy.class);

    public PatientProxy() {
        super();
        this.setInitialised(false);
    }

    @Inject
    private PatientAccessor patientAccessor;

    @Inject
    private OperationOutcomeGenerator outcomeGenerator;

    @Inject
    private BundleContentHelper bundleContentHelper;

    @Inject
    private SearchSupportHelper searchSupportHelper;

    @Override
    public Class<Patient> getResourceType() {
        return (Patient.class);
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ResourceAccessorInterfaceBase specifyActualResourceAccessor() {
        return (patientAccessor);
    }

    /**
     * The "@Create" annotation indicates that this method implements "create=type",
     * which adds a new instance of a resource to the server.
     */
    @Create()
    public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
        LOG.debug(".createPatient(): Entry, thePatient (Patient) --> {}", thePatient);
        // validateResource(thePatient);
        TransactionMethodOutcome resourceActionOutcome = getActualResourceAccessor().createResource(thePatient);
        return (resourceActionOutcome);
    }

    /**
     * This is the "read" operation. The "@Read" annotation indicates that this
     * method supports the read and/or get operation.
     * <p>
     * Read operations take a single parameter annotated with the {@link IdParam}
     * paramater, and should return a single resource instance.
     * </p>
     *
     * @param patientID The read operation takes one parameter, which must be of
     *                  type IdDt and must be annotated with the "@Read.IdParam"
     *                  annotation.
     * @return Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Patient readPatient(@IdParam IdType patientID) {
        LOG.debug(".readPatient(): Entry, patientID (IdType) --> {}", patientID);
        TransactionMethodOutcome outcome = getActualResourceAccessor().getResource(patientID);
        Patient retrievedPatient = (Patient) outcome.getResource();
        LOG.debug(".readPatient(): Exit, retrieved Patient (Patient) --> {}", retrievedPatient);
        return (retrievedPatient);
    }

    /**
     * The "@Update" annotation indicates that this method implements "update=type",
     * which adds a new instance of a resource to the server.
     */
    @Update()
    public MethodOutcome updatePatient(@ResourceParam Patient thePatient) {
        LOG.debug(".createPatient(): Entry, thePatient (Patient) --> {}", thePatient);
        TransactionMethodOutcome resourceActionOutcome = getActualResourceAccessor().updateResource(thePatient);
        return (resourceActionOutcome);
    }

    @Delete()
    public MethodOutcome deletePatient(@IdParam IdType resourceId) {
        LOG.debug(".deletePatient(): Entry, resourceId (IdType) --> {}", resourceId);
        throw (new UnsupportedOperationException("deletePatient() is not supported"));
    }

    //
    //
    // Support Searches
    //
    //

    @Search()
    public Bundle findByIdentifier(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam identifierParam) {
        getLogger().debug("PatientProxy::findByIdentifier(): Entry, identifierParam --> {}", identifierParam);
        Identifier identifierToSearchFor = tokenParam2Identifier(identifierParam);
        Resource outcome = (Resource) findResourceViaIdentifier(identifierToSearchFor);
        if(outcome.getResourceType().equals(ResourceType.Bundle)){
            Bundle outcomeBundle = (Bundle)outcome;
            return(outcomeBundle);
        } else {
            Bundle outcomeBundle = getBundleContentHelper().buildSearchResponseBundle(outcome);
            return(outcomeBundle);
        }
    }

    //
    @Search(queryName = "patientQRYA19")
    public Bundle searchPatientQueryResponse(@RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam identifierParam, @RequiredParam(name = "qrya19") StringParam queryString){
        LOG.debug(".getPatientQueryResponse(): Entry, identifierParam --> {}, qrya19 --> {}", identifierParam, queryString);

        HashMap<Property, Serializable> argumentList = new HashMap<>(); // TODO Need to replace "Serializable" with something more meaningful and appropriate

        // First Parameter, the DocumentReference.type
        Property subjectProperty = new Property(
                "identifier",
                "Identifier",
                "The person, animal or group on which the procedure was performed.",
                0,
                1,
                (List<? extends Base>) null);
        argumentList.put(subjectProperty, identifierParam);
        // Second Parameter, the DocumentReference.date (expressed as a Period, where the date is to be in-between)
        Property extensionProperty = new Property(
                "extension",
                "String",
                "Query String for a QRY_A19 Action",
                0,
                1,
                (List<? extends Base>) null);
        argumentList.put(extensionProperty, queryString);

        TransactionMethodOutcome outcome = getActualResourceAccessor().searchUsingCriteria(ResourceType.Patient, SearchNameEnum.PATIENT_QRY_A19.getSearchName(), argumentList);

        if (outcome.getStatusEnum() == TransactionStatusEnum.SEARCH_FINISHED) {
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
