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
package net.fhirfactory.pegacorn.ladon.mdr.fhirplace.conduits;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.ladon.mdr.conduit.controller.LocationSoTConduitController;
import net.fhirfactory.pegacorn.ladon.mdr.fhirplace.accessor.FHIRPlaceBaseEntitiesMDRAccessor;
import net.fhirfactory.pegacorn.ladon.mdr.fhirplace.conduits.common.FHIRPlaceSoTConduitCommon;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceGradeEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitActionResponse;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.ResourceSoTConduitSearchResponseElement;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.mdr.SoTConduitGradeEnum;
import net.fhirfactory.pegacorn.ladon.model.virtualdb.searches.SearchNameEnum;
import net.fhirfactory.pegacorn.platform.edge.ask.InternalFHIRClientServices;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LocationSoTResourceConduit extends FHIRPlaceSoTConduitCommon {
    private static final Logger LOG = LoggerFactory.getLogger(LocationSoTResourceConduit.class);

    @Inject
    private LocationSoTConduitController conduitController;

    @Inject
    private FHIRPlaceBaseEntitiesMDRAccessor servicesAccessor;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected String specifySourceOfTruthEndpointSystemName() {
        return (servicesAccessor.getFHIRServerSubsystemName());
    }

    @Override
    protected Identifier getBestIdentifier(MethodOutcome outcome) {
        if(outcome == null){
            return(null);
        }
        Resource containedResource = (Resource)outcome.getResource();
        if(containedResource == null){
            return(null);
        }
        Location actualResource = (Location)containedResource;
        if(actualResource.hasIdentifier()){
            Identifier bestIdentifier = getIdentifierDataTypeHelpers().getBestIdentifier(actualResource.getIdentifier());
            return(bestIdentifier);
        }
        return(null);
    }

    @Override
    protected InternalFHIRClientServices specifySecureAccessor() {
        return (servicesAccessor);
    }

    @Override
    protected void registerWithSoTCConduitController() {
        conduitController.addResourceConduit(this);
    }

    /**
     * This is the CREATE (POST) Function for the Location Resource --> persisting
     * the content within a FHIRPlace instance.
     *
     * This function, after invoking the default FHIRPlace writer/create function, adds the
     * CREATE_FULL_COVERAGE response - since, within the context of a Location
     * resource, FHIRPlace is the sole persistence framework (as of Release 1.0.0).
     *
     * @param resourceToCreate The Location (Resource) to be persisted
     * @return A Response/Outcome of the operation, including a copy of the Resource.
     */
    @Override
    public ResourceSoTConduitActionResponse createResource(Resource resourceToCreate) {
        LOG.debug(".createResource(): Entry, resourceToCreate --> {}", resourceToCreate);
        ResourceSoTConduitActionResponse outcome = standardCreateResource(resourceToCreate);
        outcome.setResponseResourceGrade(ResourceGradeEnum.THOROUGH);
        outcome.setSoTGrade(SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE);
        LOG.debug(".createResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }

    /**
     * This is the REVIEW (GET) Function for the Location Resource --> retrieving
     * the Location resource from a FHIRPlace instance.
     *
     * This function, after invoking the default FHIRPlace read/get function, adds the
     * REVIEW_FULL_COVERAGE response - since, within the context of a Location
     * resource, FHIRPlace is the sole persistence framework (as of Release 1.0.0).
     *
     * @param identifier The identifier of the Location to be retrieved
     * @return A Response/Outcome of the operation, including a copy of the Resource (if found).
     */
    @Override
    public ResourceSoTConduitActionResponse getResourceViaIdentifier(Identifier identifier) {
        LOG.debug(".readResource(): Entry, identifier --> {}", identifier);
        ResourceSoTConduitActionResponse outcome = standardGetResourceViaIdentifier(ResourceType.Location.toString(), identifier);
        outcome.setResponseResourceGrade(ResourceGradeEnum.THOROUGH);
        outcome.setSoTGrade(SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE);
        LOG.debug(".readResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }

    /**
     * This is the REVIEW (GET) Function for the CareTeam Resource --> retrieving
     * the CareTeam resource from a FHIRPlace instance.
     *
     * This function, after invoking the default FHIRPlace read/get function, adds the
     * REVIEW_FULL_COVERAGE response - since, within the context of a CareTeam
     * resource, FHIRPlace is the sole persistence framework (as of Release 1.0.0).
     *
     * @param id The identifier of the CareTeam to be retrieved
     * @return A Response/Outcome of the operation, including a copy of the Resource (if found).
     */
    @Override
    public ResourceSoTConduitActionResponse reviewResource(IdType id) {
        LOG.debug(".readResource(): Entry, identifier --> {}", id);
        ResourceSoTConduitActionResponse outcome = standardReviewResource(Location.class, id);
        outcome.setResponseResourceGrade(ResourceGradeEnum.THOROUGH);
        outcome.setSoTGrade(SoTConduitGradeEnum.AUTHORITATIVE);
        LOG.debug(".readResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }

    /**
     * This is the UPDATE (PUT) Function for the Location Resource --> updating the
     * content within a FHIRPlace instance.
     *
     * This function, after invoking the default FHIRPlace update/put function, adds the
     * UPDATE_FULL_COVERAGE response - since, within the context of a Location
     * resource, FHIRPlace is the sole persistence framework (as of Release 1.0.0).
     *
     * @param resourceToUpdate The Location (Resource) to be updated
     * @return A Response/Outcome of the operation, including a copy of the Resource.
     */
    @Override
    public ResourceSoTConduitActionResponse updateResource(Resource resourceToUpdate) {
        LOG.debug(".updateResource(): Entry, resourceToUpdate --> {}", resourceToUpdate);
        ResourceSoTConduitActionResponse outcome = standardUpdateResource(resourceToUpdate);
        outcome.setResponseResourceGrade(ResourceGradeEnum.THOROUGH);
        outcome.setSoTGrade(SoTConduitGradeEnum.PARTIALLY_AUTHORITATIVE);
        LOG.debug(".updateResource(): Exit, outcome --> {}", outcome);
        return(outcome);
    }

    @Override
    public ResourceSoTConduitActionResponse deleteResource(Resource resourceToDelete) {
        return null;
    }

    @Override
    public List<ResourceSoTConduitSearchResponseElement> searchSourceOfTruthUsingCriteria(ResourceType resourceType, SearchNameEnum searchName, Map<Property, Serializable> parameterSet) {
        return null;
    }

    @Override
    public boolean supportiveOfSearch(SearchNameEnum searchName) {
        return false;
    }

    @Override
    public boolean supportsDirectCreateAction(Resource wholeResource) {
        return true;
    }

    @Override
    public boolean supportsDirectUpdateAction(Resource wholeResource) {
        return true;
    }

    @Override
    public boolean supportsDirectDeleteAction(Resource wholeResource) {
        return false;
    }

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Location);
    }
    
    
    

    //
    // Supported Searches
    //

}
